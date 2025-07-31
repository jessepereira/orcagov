package br.com.orcagov.api.service;

import br.com.orcagov.api.dto.Request.EmpenhoRequestDTO;
import br.com.orcagov.api.dto.Response.DespesaResumoDTO;
import br.com.orcagov.api.dto.Response.EmpenhoResponseDTO;
import br.com.orcagov.api.dto.Response.PagamentoResumoDTO;
import br.com.orcagov.api.entity.Despesa;
import br.com.orcagov.api.entity.Empenho;
import br.com.orcagov.api.entity.Usuario;
import br.com.orcagov.api.entity.enums.StatusDespesa;
import br.com.orcagov.api.repository.DespesaRepository;
import br.com.orcagov.api.repository.EmpenhoRepository;
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
public class EmpenhoService {

    @Autowired
    private EmpenhoRepository empenhoRepository;
    
    @Autowired
    private DespesaRepository despesaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    // ==========================================
    // MÉTODOS CRUD PRINCIPAIS
    // ==========================================

    /**
     * Criar novo empenho
     */
    public EmpenhoResponseDTO criarEmpenho(EmpenhoRequestDTO request, String userName) {
        // Buscar usuário logado
        Usuario usuario = buscarUsuarioPorNome(userName);
        
        // Buscar despesa
        Despesa despesa = buscarDespesaPorId(request.getDespesaId());
        
        // Validar se pode adicionar empenho à despesa
        validarEmpenhoParaDespesa(despesa, request.getValor());
        
        // Criar entidade Empenho
        Empenho empenho = new Empenho();
        empenho.setNumeroEmpenho(sequenceGeneratorService.gerarNumeroEmpenho());
        empenho.setDataEmpenho(request.getDataEmpenho());
        empenho.setValor(request.getValor());
        empenho.setObservacao(request.getObservacao());
        empenho.setDespesa(despesa);
        empenho.setUsuarioCriador(usuario);
        empenho.setDataCriacao(LocalDateTime.now());
        empenho.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar no banco
        Empenho empenhoSalvo = empenhoRepository.save(empenho);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(despesa);
        
        // Converter para DTO de resposta
        return converterParaResponseDTO(empenhoSalvo);
    }

    /**
     * Buscar empenho por ID
     */
    @Transactional(readOnly = true)
    public EmpenhoResponseDTO buscarPorId(Long id) {
        Empenho empenho = buscarEmpenhoPorId(id);
        return converterParaResponseDTO(empenho);
    }

    /**
     * Buscar empenho por número
     */
    @Transactional(readOnly = true)
    public EmpenhoResponseDTO buscarPorNumero(String numeroEmpenho) {
        Empenho empenho = empenhoRepository.findByNumeroEmpenho(numeroEmpenho)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Empenho não encontrado com o número: " + numeroEmpenho));
        
        return converterParaResponseDTO(empenho);
    }

    /**
     * Listar todos os empenhos com paginação
     */
    @Transactional(readOnly = true)
    public Page<EmpenhoResponseDTO> listarTodos(Pageable pageable) {
        Page<Empenho> empenhos = empenhoRepository.findAll(pageable);
        return empenhos.map(this::converterParaResponseDTO);
    }

    /**
     * Atualizar empenho existente
     */
    public EmpenhoResponseDTO atualizarEmpenho(Long id, EmpenhoRequestDTO request, String userName) {
        // Buscar empenho existente
        Empenho empenho = buscarEmpenhoPorId(id);
        
        // Validar se pode ser alterado
        validarSePermiteAlteracao(empenho);
        
        // Se mudou o valor, validar
        if (request.getValor().compareTo(empenho.getValor()) != 0) {
            validarAlteracaoValor(empenho, request.getValor());
        }
        
        // Atualizar campos
        empenho.setDataEmpenho(request.getDataEmpenho());
        empenho.setValor(request.getValor());
        empenho.setObservacao(request.getObservacao());
        empenho.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar alterações
        Empenho empenhoAtualizado = empenhoRepository.save(empenho);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(empenho.getDespesa());
        
        return converterParaResponseDTO(empenhoAtualizado);
    }

    /**
     * Excluir empenho
     */
    public void excluirEmpenho(Long id, String userName) {
        Empenho empenho = buscarEmpenhoPorId(id);
        
        // Validar se pode ser excluído
        validarSePermiteExclusao(empenho);
        
        Despesa despesa = empenho.getDespesa();
        
        empenhoRepository.delete(empenho);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(despesa);
    }

    // ==========================================
    // MÉTODOS DE BUSCA POR DESPESA
    // ==========================================

    /**
     * Listar empenhos de uma despesa específica
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> listarPorDespesa(Long despesaId) {
        List<Empenho> empenhos = empenhoRepository.findByDespesaId(despesaId);
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar empenhos por número de protocolo da despesa
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> listarPorProtocoloDespesa(String numeroProtocolo) {
        List<Empenho> empenhos = empenhoRepository.findByDespesaNumeroProtocolo(numeroProtocolo);
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS DE BUSCA E FILTROS
    // ==========================================

    /**
     * Buscar empenhos com filtros
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> buscarComFiltros(
            String numeroEmpenho,
            Long despesaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorMinimo,
            BigDecimal valorMaximo) {
        
        List<Empenho> empenhos = empenhoRepository.buscarComFiltros(
                numeroEmpenho, despesaId, dataInicio, dataFim, valorMinimo, valorMaximo);
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar empenhos por período
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Empenho> empenhos = empenhoRepository.findByDataEmpenhoBetween(dataInicio, dataFim);
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar empenhos por faixa de valor
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> buscarPorFaixaValor(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        List<Empenho> empenhos = empenhoRepository.findByValorBetween(valorMinimo, valorMaximo);
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar empenhos sem pagamentos
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> buscarSemPagamentos() {
        List<Empenho> empenhos = empenhoRepository.findEmpenhosSemPagamentos();
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar empenhos com pagamentos
     */
    @Transactional(readOnly = true)
    public List<EmpenhoResponseDTO> buscarComPagamentos() {
        List<Empenho> empenhos = empenhoRepository.findEmpenhosComPagamentos();
        
        return empenhos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS DE VALIDAÇÃO
    // ==========================================

    private void validarEmpenhoParaDespesa(Despesa despesa, BigDecimal valorEmpenho) {
        // Verificar se despesa permite empenho
        if (despesa.getStatus() == StatusDespesa.PAGA) {
            throw new BusinessException("Não é possível adicionar empenho a despesa já paga");
        }
        
        // Verificar se soma dos empenhos não ultrapassa valor da despesa
        BigDecimal valorTotalEmpenhado = despesa.getValorTotalEmpenhado();
        BigDecimal novoTotal = valorTotalEmpenhado.add(valorEmpenho);
        
        if (novoTotal.compareTo(despesa.getValorDespesas()) > 0) {
            throw new BusinessException(
                String.format("Valor do empenho (R$ %.2f) excede o valor disponível da despesa (R$ %.2f). " +
                             "Valor já empenhado: R$ %.2f",
                    valorEmpenho, 
                    despesa.getValorDespesas().subtract(valorTotalEmpenhado),
                    valorTotalEmpenhado));
        }
    }

    private void validarSePermiteAlteracao(Empenho empenho) {
        if (empenho.temPagamentos()) {
            throw new BusinessException("Não é possível alterar empenho que possui pagamentos");
        }
    }

    private void validarSePermiteExclusao(Empenho empenho) {
        if (empenho.temPagamentos()) {
            throw new BusinessException("Não é possível excluir empenho que possui pagamentos");
        }
    }

    private void validarAlteracaoValor(Empenho empenho, BigDecimal novoValor) {
        // Verificar se novo valor não é menor que o já pago
        BigDecimal valorPago = empenho.getValorTotalPago();
        if (novoValor.compareTo(valorPago) < 0) {
            throw new BusinessException(
                String.format("Novo valor (R$ %.2f) não pode ser menor que o valor já pago (R$ %.2f)",
                    novoValor, valorPago));
        }
        
        // Verificar se não excede valor disponível da despesa
        Despesa despesa = empenho.getDespesa();
        BigDecimal valorOutrosEmpenhos = despesa.getValorTotalEmpenhado().subtract(empenho.getValor());
        BigDecimal novoTotalEmpenhos = valorOutrosEmpenhos.add(novoValor);
        
        if (novoTotalEmpenhos.compareTo(despesa.getValorDespesas()) > 0) {
            throw new BusinessException(
                String.format("Novo valor excede o valor disponível da despesa"));
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private Empenho buscarEmpenhoPorId(Long id) {
        return empenhoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Empenho não encontrado com ID: " + id));
    }

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

    private void atualizarStatusDespesa(Despesa despesa) {
        despesa.setStatus(despesa.calculateStatus());
        despesaRepository.save(despesa);
    }

    // ==========================================
    // MÉTODOS ESPECÍFICOS DO CONTROLLER
    // ==========================================

    public EmpenhoResponseDTO cancelarEmpenho(Long id, String motivo, String userName) {
        // Implementar lógica de cancelamento se necessário
        throw new BusinessException("Funcionalidade de cancelamento não implementada");
    }

    public EmpenhoResponseDTO reativarEmpenho(Long id, String userName) {
        // Implementar lógica de reativação se necessário
        throw new BusinessException("Funcionalidade de reativação não implementada");
    }

    public String obterProximoNumero() {
        return sequenceGeneratorService.gerarNumeroEmpenho();
    }

    public boolean verificarNumeroExiste(String numeroEmpenho) {
        return empenhoRepository.existsByNumeroEmpenho(numeroEmpenho);
    }

    public ValidacaoValorDTO validarValorEmpenho(Long despesaId, BigDecimal valorEmpenho) {
        Despesa despesa = buscarDespesaPorId(despesaId);
        
        BigDecimal valorTotalEmpenhado = despesa.getValorTotalEmpenhado();
        BigDecimal valorDisponivel = despesa.getValorDespesas().subtract(valorTotalEmpenhado);
        
        boolean valido = valorEmpenho.compareTo(valorDisponivel) <= 0;
        String mensagem = valido ? "Valor válido" : 
            String.format("Valor excede o disponível (R$ %.2f)", valorDisponivel);
        
        return ValidacaoValorDTO.builder()
                .valido(valido)
                .valorDisponivel(valorDisponivel)
                .valorSolicitado(valorEmpenho)
                .mensagem(mensagem)
                .build();
    }

    // ==========================================
    // MÉTODOS DE CONVERSÃO DTO
    // ==========================================

    private EmpenhoResponseDTO converterParaResponseDTO(Empenho empenho) {
        return EmpenhoResponseDTO.builder()
                .id(empenho.getId())
                .numeroEmpenho(empenho.getNumeroEmpenho())
                .dataEmpenho(empenho.getDataEmpenho())
                .valor(empenho.getValor())
                .observacao(empenho.getObservacao())
                .valorTotalPago(empenho.getValorTotalPago())
                .valorRestante(empenho.getValor().subtract(empenho.getValorTotalPago()))
                .despesa(converterDespesaParaResumo(empenho.getDespesa()))
                .pagamentos(converterPagamentosParaResumo(empenho))
                .dataCriacao(empenho.getDataCriacao())
                .dataAtualizacao(empenho.getDataAtualizacao())
                .build();
    }

    private DespesaResumoDTO converterDespesaParaResumo(Despesa despesa) {
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

    private List<PagamentoResumoDTO> converterPagamentosParaResumo(Empenho empenho) {
        return empenho.getPagamentos().stream()
                .map(pagamento -> PagamentoResumoDTO.builder()
                        .id(pagamento.getId())
                        .numeroPagamento(pagamento.getNumeroPagamento())
                        .dataPagamento(pagamento.getDataPagamento())
                        .valorPagamento(pagamento.getValorPagamento())
                        .build())
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS PARA RELATÓRIOS E ESTATÍSTICAS
    // ==========================================

    /**
     * Obter estatísticas gerais dos empenhos
     */
    @Transactional(readOnly = true)
    public EmpenhoEstatisticasDTO obterEstatisticas() {
        List<Empenho> todosEmpenhos = empenhoRepository.findAll();
        
        BigDecimal valorTotal = todosEmpenhos.stream()
                .map(Empenho::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal valorPago = todosEmpenhos.stream()
                .map(Empenho::getValorTotalPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long empenhosSemPagamentos = todosEmpenhos.stream()
                .filter(e -> e.getPagamentos().isEmpty())
                .count();
        
        return EmpenhoEstatisticasDTO.builder()
                .totalEmpenhos((long) todosEmpenhos.size())
                .valorTotalEmpenhado(valorTotal)
                .valorTotalPago(valorPago)
                .valorRestante(valorTotal.subtract(valorPago))
                .empenhosSemPagamentos(empenhosSemPagamentos)
                .build();
    }

    /**
     * Relatório mensal de empenhos
     */
    @Transactional(readOnly = true)
    public List<RelatorioMensalDTO> obterRelatorioMensal() {
        List<Object[]> resultados = empenhoRepository.totalEmpenhadoPorMes();
        
        return resultados.stream()
                .map(resultado -> RelatorioMensalDTO.builder()
                        .ano(((Number) resultado[0]).intValue())
                        .mes(((Number) resultado[1]).intValue())
                        .valorTotal((BigDecimal) resultado[2])
                        .build())
                .collect(Collectors.toList());
    }

    // ==========================================
    // DTOs PARA ESTATÍSTICAS E RELATÓRIOS
    // ==========================================

    public static class EmpenhoEstatisticasDTO {
        private Long totalEmpenhos;
        private BigDecimal valorTotalEmpenhado;
        private BigDecimal valorTotalPago;
        private BigDecimal valorRestante;
        private Long empenhosSemPagamentos;
        
        public static EmpenhoEstatisticasDTOBuilder builder() {
            return new EmpenhoEstatisticasDTOBuilder();
        }
        
        public static class EmpenhoEstatisticasDTOBuilder {
            private EmpenhoEstatisticasDTO dto = new EmpenhoEstatisticasDTO();
            
            public EmpenhoEstatisticasDTOBuilder totalEmpenhos(Long total) {
                dto.totalEmpenhos = total;
                return this;
            }
            
            public EmpenhoEstatisticasDTOBuilder valorTotalEmpenhado(BigDecimal valor) {
                dto.valorTotalEmpenhado = valor;
                return this;
            }
            
            public EmpenhoEstatisticasDTOBuilder valorTotalPago(BigDecimal valor) {
                dto.valorTotalPago = valor;
                return this;
            }
            
            public EmpenhoEstatisticasDTOBuilder valorRestante(BigDecimal valor) {
                dto.valorRestante = valor;
                return this;
            }
            
            public EmpenhoEstatisticasDTOBuilder empenhosSemPagamentos(Long count) {
                dto.empenhosSemPagamentos = count;
                return this;
            }
            
            public EmpenhoEstatisticasDTO build() {
                return dto;
            }
        }
        
        // Getters
        public Long getTotalEmpenhos() { return totalEmpenhos; }
        public BigDecimal getValorTotalEmpenhado() { return valorTotalEmpenhado; }
        public BigDecimal getValorTotalPago() { return valorTotalPago; }
        public BigDecimal getValorRestante() { return valorRestante; }
        public Long getEmpenhosSemPagamentos() { return empenhosSemPagamentos; }
    }

    public static class RelatorioMensalDTO {
        private Integer ano;
        private Integer mes;
        private BigDecimal valorTotal;
        
        public static RelatorioMensalDTOBuilder builder() {
            return new RelatorioMensalDTOBuilder();
        }
        
        public static class RelatorioMensalDTOBuilder {
            private RelatorioMensalDTO dto = new RelatorioMensalDTO();
            
            public RelatorioMensalDTOBuilder ano(Integer ano) {
                dto.ano = ano;
                return this;
            }
            
            public RelatorioMensalDTOBuilder mes(Integer mes) {
                dto.mes = mes;
                return this;
            }
            
            public RelatorioMensalDTOBuilder valorTotal(BigDecimal valor) {
                dto.valorTotal = valor;
                return this;
            }
            
            public RelatorioMensalDTO build() {
                return dto;
            }
        }
        
        // Getters
        public Integer getAno() { return ano; }
        public Integer getMes() { return mes; }
        public BigDecimal getValorTotal() { return valorTotal; }
    }

    public static class ValidacaoValorDTO {
        private Boolean valido;
        private BigDecimal valorDisponivel;
        private BigDecimal valorSolicitado;
        private String mensagem;
        
        public static ValidacaoValorDTOBuilder builder() {
            return new ValidacaoValorDTOBuilder();
        }
        
        public static class ValidacaoValorDTOBuilder {
            private ValidacaoValorDTO dto = new ValidacaoValorDTO();
            
            public ValidacaoValorDTOBuilder valido(Boolean valido) {
                dto.valido = valido;
                return this;
            }
            
            public ValidacaoValorDTOBuilder valorDisponivel(BigDecimal valor) {
                dto.valorDisponivel = valor;
                return this;
            }
            
            public ValidacaoValorDTOBuilder valorSolicitado(BigDecimal valor) {
                dto.valorSolicitado = valor;
                return this;
            }
            
            public ValidacaoValorDTOBuilder mensagem(String mensagem) {
                dto.mensagem = mensagem;
                return this;
            }
            
            public ValidacaoValorDTO build() {
                return dto;
            }
        }
        
        // Getters
        public Boolean getValido() { return valido; }
        public BigDecimal getValorDisponivel() { return valorDisponivel; }
        public BigDecimal getValorSolicitado() { return valorSolicitado; }
        public String getMensagem() { return mensagem; }
    }
}