package br.com.orcagov.api.service;

import br.com.orcagov.api.dto.Request.DespesaRequestDTO;
import br.com.orcagov.api.dto.Response.DespesaResponseDTO;
import br.com.orcagov.api.dto.Response.DespesaResumoDTO;
import br.com.orcagov.api.dto.Response.EmpenhoResumoDTO;
import br.com.orcagov.api.entity.Despesa;
import br.com.orcagov.api.entity.Usuario;
import br.com.orcagov.api.entity.enums.StatusDespesa;
import br.com.orcagov.api.entity.enums.TipoDespesa;
import br.com.orcagov.api.repository.DespesaRepository;
import br.com.orcagov.api.repository.UsuarioRepository;
import br.com.orcagov.api.exception.BusinessException;
import br.com.orcagov.api.exception.ResourceNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProtocoloGeneratorService protocoloGeneratorService;

    // ==========================================
    // MÉTODOS CRUD PRINCIPAIS
    // ==========================================

    /**
     * Criar nova despesa
     */
    public DespesaResponseDTO criarDespesa(DespesaRequestDTO request, String userName) {
        // Buscar usuário logado
        Usuario usuario = buscarUsuarioPorNome(userName);
        
        // Criar entidade Despesa
        Despesa despesa = new Despesa();
        despesa.setNumeroProtocolo(protocoloGeneratorService.gerarNumeroProtocolo());
        despesa.setTipoDespesa(request.getTipoDespesa());
        despesa.setDataProtocolo(LocalDateTime.now());
        despesa.setDataVencimento(request.getDataVencimento());
        despesa.setCredorDespesas(request.getCredorDespesas());
        despesa.setDescricaoDespesas(request.getDescricaoDespesas());
        despesa.setValorDespesas(request.getValorDespesas());
        despesa.setStatus(StatusDespesa.AGUARDANDO_EMPENHO);
        despesa.setUsuarioCriador(usuario);
        despesa.setDataCriacao(LocalDateTime.now());
        despesa.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar no banco
        Despesa despesaSalva = despesaRepository.save(despesa);
        
        // Converter para DTO de resposta
        return converterParaResponseDTO(despesaSalva);
    }

    /**
     * Buscar despesa por ID
     */
    @Transactional(readOnly = true)
    public DespesaResponseDTO buscarPorId(Long id) {
        Despesa despesa = buscarDespesaPorId(id);
        return converterParaResponseDTO(despesa);
    }

    /**
     * Buscar despesa por número de protocolo
     */
    @Transactional(readOnly = true)
    public DespesaResponseDTO buscarPorProtocolo(String numeroProtocolo) {
        Despesa despesa = despesaRepository.findByNumeroProtocolo(numeroProtocolo)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Despesa não encontrada com o protocolo: " + numeroProtocolo));
        
        return converterParaResponseDTO(despesa);
    }

    /**
     * Listar todas as despesas com paginação
     */
    @Transactional(readOnly = true)
    public Page<DespesaResponseDTO> listarTodas(Pageable pageable) {
        Page<Despesa> despesas = despesaRepository.findAll(pageable);
        return despesas.map(this::converterParaResponseDTO);
    }

    /**
     * Atualizar despesa existente
     */
    public DespesaResponseDTO atualizarDespesa(Long id, DespesaRequestDTO request, String userName) {
        // Buscar despesa existente
        Despesa despesa = buscarDespesaPorId(id);
        
        // Validar se pode ser alterada
        validarSePermiteAlteracao(despesa);
        
        // Validar se mudança de valor não afeta empenhos
        if (request.getValorDespesas().compareTo(despesa.getValorDespesas()) != 0) {
            validarAlteracaoValor(despesa, request.getValorDespesas());
        }
        
        // Atualizar campos
        despesa.setTipoDespesa(request.getTipoDespesa());
        despesa.setDataVencimento(request.getDataVencimento());
        despesa.setCredorDespesas(request.getCredorDespesas());
        despesa.setDescricaoDespesas(request.getDescricaoDespesas());
        despesa.setValorDespesas(request.getValorDespesas());
        despesa.setDataAtualizacao(LocalDateTime.now());
        
        // Recalcular status se necessário
        despesa.setStatus(despesa.calculateStatus());
        
        // Salvar alterações
        Despesa despesaAtualizada = despesaRepository.save(despesa);
        
        return converterParaResponseDTO(despesaAtualizada);
    }

    /**
     * Excluir despesa
     */
    public void excluirDespesa(Long id, String userName) {
        Despesa despesa = buscarDespesaPorId(id);
        
        // Validar se pode ser excluída
        validarSePermiteExclusao(despesa);
        
        despesaRepository.delete(despesa);
    }

    // ==========================================
    // MÉTODOS DE BUSCA E FILTROS
    // ==========================================

    /**
     * Buscar despesas com filtros
     */
    @Transactional(readOnly = true)
    public List<DespesaResponseDTO> buscarComFiltros(
            String numeroProtocolo,
            TipoDespesa tipoDespesa,
            StatusDespesa status,
            String credor,
            LocalDate dataInicio,
            LocalDate dataFim) {
        
        List<Despesa> despesas = despesaRepository.buscarComFiltros(
                numeroProtocolo, tipoDespesa, status, credor, dataInicio, dataFim);
        
        return despesas.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar despesas vencidas
     */
    @Transactional(readOnly = true)
    public List<DespesaResponseDTO> buscarDespesasVencidas() {
        List<Despesa> despesas = despesaRepository
                .findByDataVencimentoBeforeOrderByDataVencimentoAsc(LocalDate.now());
        
        return despesas.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar despesas que vencem nos próximos X dias
     */
    @Transactional(readOnly = true)
    public List<DespesaResponseDTO> buscarDespesasVencendoEm(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(dias);
        
        List<Despesa> despesas = despesaRepository
                .findDespesasVencendoEm(hoje, dataLimite);
        
        return despesas.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar despesas por tipo
     */
    @Transactional(readOnly = true)
    public List<DespesaResponseDTO> buscarPorTipo(TipoDespesa tipoDespesa) {
        List<Despesa> despesas = despesaRepository.findByTipoDespesa(tipoDespesa);
        
        return despesas.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar despesas por status
     */
    @Transactional(readOnly = true)
    public List<DespesaResponseDTO> buscarPorStatus(StatusDespesa status) {
        List<Despesa> despesas = despesaRepository.findByStatus(status);
        
        return despesas.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS DE VALIDAÇÃO
    // ==========================================

    private void validarSePermiteAlteracao(Despesa despesa) {
        if (despesa.getStatus() == StatusDespesa.PAGA) {
            throw new BusinessException("Não é possível alterar despesa já paga");
        }
    }

    private void validarSePermiteExclusao(Despesa despesa) {
        if (!despesa.getEmpenhos().isEmpty()) {
            throw new BusinessException("Não é possível excluir despesa que possui empenhos");
        }
    }

    private void validarAlteracaoValor(Despesa despesa, BigDecimal novoValor) {
        BigDecimal valorTotalEmpenhado = despesa.getValorTotalEmpenhado();
        
        if (novoValor.compareTo(valorTotalEmpenhado) < 0) {
            throw new BusinessException(
                String.format("Novo valor (R$ %.2f) não pode ser menor que o valor já empenhado (R$ %.2f)",
                    novoValor, valorTotalEmpenhado));
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private Despesa buscarDespesaPorId(Long id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Despesa não encontrada com ID: " + id));
    }

    private Usuario buscarUsuarioPorNome(String userName) {
        return usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Usuário não encontrado: " + userName));
    }

    // ==========================================
    // MÉTODOS DE CONVERSÃO DTO
    // ==========================================

    private DespesaResponseDTO converterParaResponseDTO(Despesa despesa) {
        return DespesaResponseDTO.builder()
                .id(despesa.getId())
                .numeroProtocolo(despesa.getNumeroProtocolo())
                .tipoDespesa(despesa.getTipoDespesa())
                .dataProtocolo(despesa.getDataProtocolo())
                .dataVencimento(despesa.getDataVencimento())
                .credorDespesas(despesa.getCredorDespesas())
                .descricaoDespesas(despesa.getDescricaoDespesas())
                .valorDespesas(despesa.getValorDespesas())
                .status(despesa.getStatus())
                .valorTotalEmpenhado(despesa.getValorTotalEmpenhado())
                .valorTotalPago(despesa.getValorTotalPago())
                .valorRestante(despesa.getValorDespesas().subtract(despesa.getValorTotalEmpenhado()))
                .vencida(despesa.getDataVencimento().isBefore(LocalDate.now()))
                .empenhos(converterEmpenhosParaResumo(despesa))
                .dataCriacao(despesa.getDataCriacao())
                .dataAtualizacao(despesa.getDataAtualizacao())
                .build();
    }

    private List<EmpenhoResumoDTO> converterEmpenhosParaResumo(Despesa despesa) {
        return despesa.getEmpenhos().stream()
                .map(empenho -> EmpenhoResumoDTO.builder()
                        .id(empenho.getId())
                        .numeroEmpenho(empenho.getNumeroEmpenho())
                        .dataEmpenho(empenho.getDataEmpenho())
                        .valor(empenho.getValor())
                        .valorPago(empenho.getValorTotalPago())
                        .build())
                .collect(Collectors.toList());
    }

    public DespesaResumoDTO converterParaResumoDTO(Despesa despesa) {
        return DespesaResumoDTO.builder()
                .id(despesa.getId())
                .numeroProtocolo(despesa.getNumeroProtocolo())
                .tipoDespesa(despesa.getTipoDespesa())
                .dataVencimento(despesa.getDataVencimento())
                .credorDespesas(despesa.getCredorDespesas())
                .valorDespesas(despesa.getValorDespesas())
                .status(despesa.getStatus())
                .build();
    }

    // ==========================================
    // MÉTODOS PARA RELATÓRIOS E ESTATÍSTICAS
    // ==========================================

    /**
     * Obter estatísticas gerais das despesas
     */
    @Transactional(readOnly = true)
    public DespesaEstatisticasDTO obterEstatisticas() {
        List<Despesa> todasDespesas = despesaRepository.findAll();
        
        BigDecimal valorTotal = todasDespesas.stream()
                .map(Despesa::getValorDespesas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valorEmpenhado = todasDespesas.stream()
                .map(Despesa::getValorTotalEmpenhado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valorPago = todasDespesas.stream()
                .map(Despesa::getValorTotalPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long despesasVencidas = todasDespesas.stream()
                .filter(d -> d.getDataVencimento().isBefore(LocalDate.now()))
                .count();
        
        return DespesaEstatisticasDTO.builder()
                .totalDespesas((long) todasDespesas.size())
                .valorTotalDespesas(valorTotal)
                .valorTotalEmpenhado(valorEmpenhado)
                .valorTotalPago(valorPago)
                .valorRestante(valorTotal.subtract(valorEmpenhado))
                .despesasVencidas(despesasVencidas)
                .build();
    }

    // DTO para estatísticas (criar em dto/response)
    public static class DespesaEstatisticasDTO {
        private Long totalDespesas;
        private BigDecimal valorTotalDespesas;
        private BigDecimal valorTotalEmpenhado;
        private BigDecimal valorTotalPago;
        private BigDecimal valorRestante;
        private Long despesasVencidas;
        
        // Builder pattern e getters/setters
        public static DespesaEstatisticasDTOBuilder builder() {
            return new DespesaEstatisticasDTOBuilder();
        }
        
        public static class DespesaEstatisticasDTOBuilder {
            private DespesaEstatisticasDTO dto = new DespesaEstatisticasDTO();
            
            public DespesaEstatisticasDTOBuilder totalDespesas(Long total) {
                dto.totalDespesas = total;
                return this;
            }
            
            public DespesaEstatisticasDTOBuilder valorTotalDespesas(BigDecimal valor) {
                dto.valorTotalDespesas = valor;
                return this;
            }
            
            public DespesaEstatisticasDTOBuilder valorTotalEmpenhado(BigDecimal valor) {
                dto.valorTotalEmpenhado = valor;
                return this;
            }
            
            public DespesaEstatisticasDTOBuilder valorTotalPago(BigDecimal valor) {
                dto.valorTotalPago = valor;
                return this;
            }
            
            public DespesaEstatisticasDTOBuilder valorRestante(BigDecimal valor) {
                dto.valorRestante = valor;
                return this;
            }
            
            public DespesaEstatisticasDTOBuilder despesasVencidas(Long count) {
                dto.despesasVencidas = count;
                return this;
            }
            
            public DespesaEstatisticasDTO build() {
                return dto;
            }
        }
        
        // Getters
        public Long getTotalDespesas() { return totalDespesas; }
        public BigDecimal getValorTotalDespesas() { return valorTotalDespesas; }
        public BigDecimal getValorTotalEmpenhado() { return valorTotalEmpenhado; }
        public BigDecimal getValorTotalPago() { return valorTotalPago; }
        public BigDecimal getValorRestante() { return valorRestante; }
        public Long getDespesasVencidas() { return despesasVencidas; }
    }
}