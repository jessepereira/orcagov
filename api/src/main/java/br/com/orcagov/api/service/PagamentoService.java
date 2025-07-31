package br.com.orcagov.api.service;

import br.com.orcagov.api.dto.Request.PagamentoRequestDTO;
import br.com.orcagov.api.dto.Response.EmpenhoResumoDTO;
import br.com.orcagov.api.dto.Response.PagamentoResponseDTO;
import br.com.orcagov.api.dto.Response.ValidacaoValorDTO;
import br.com.orcagov.api.entity.Empenho;
import br.com.orcagov.api.entity.Pagamento;
import br.com.orcagov.api.entity.Usuario;
import br.com.orcagov.api.repository.EmpenhoRepository;
import br.com.orcagov.api.repository.PagamentoRepository;
import br.com.orcagov.api.repository.UsuarioRepository;
import br.com.orcagov.api.repository.DespesaRepository;
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
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private EmpenhoRepository empenhoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private DespesaRepository despesaRepository;
    
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    // ==========================================
    // MÉTODOS CRUD PRINCIPAIS
    // ==========================================

    /**
     * Criar novo pagamento
     */
    public PagamentoResponseDTO criarPagamento(PagamentoRequestDTO request, String userName) {
        // Buscar usuário logado
        Usuario usuario = buscarUsuarioPorNome(userName);
        
        // Buscar empenho
        Empenho empenho = buscarEmpenhoPorId(request.getEmpenhoId());
        
        // Validar se pode adicionar pagamento ao empenho
        validarPagamentoParaEmpenho(empenho, request.getValorPagamento());
        
        // Criar entidade Pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setNumeroPagamento(sequenceGeneratorService.gerarNumeroPagamento());
        pagamento.setDataPagamento(request.getDataPagamento());
        pagamento.setValorPagamento(request.getValorPagamento());
        pagamento.setObservacao(request.getObservacao());
        pagamento.setEmpenho(empenho);
        pagamento.setUsuarioCriador(usuario);
        pagamento.setDataCriacao(LocalDateTime.now());
        pagamento.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar no banco
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(empenho);
        
        // Converter para DTO de resposta
        return converterParaResponseDTO(pagamentoSalvo);
    }

    /**
     * Buscar pagamento por ID
     */
    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorId(Long id) {
        Pagamento pagamento = buscarPagamentoPorId(id);
        return converterParaResponseDTO(pagamento);
    }

    /**
     * Buscar pagamento por número
     */
    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorNumero(String numeroPagamento) {
        Pagamento pagamento = pagamentoRepository.findByNumeroPagamento(numeroPagamento)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Pagamento não encontrado com o número: " + numeroPagamento));
        
        return converterParaResponseDTO(pagamento);
    }

    /**
     * Listar todos os pagamentos com paginação
     */
    @Transactional(readOnly = true)
    public Page<PagamentoResponseDTO> listarTodos(Pageable pageable) {
        Page<Pagamento> pagamentos = pagamentoRepository.findAll(pageable);
        return pagamentos.map(this::converterParaResponseDTO);
    }

    /**
     * Atualizar pagamento existente
     */
    public PagamentoResponseDTO atualizarPagamento(Long id, PagamentoRequestDTO request, String userName) {
        // Buscar pagamento existente
        Pagamento pagamento = buscarPagamentoPorId(id);
        
        // Validar se pode ser alterado
        validarSePermiteAlteracao(pagamento);
        
        // Se mudou o valor, validar
        if (request.getValorPagamento().compareTo(pagamento.getValorPagamento()) != 0) {
            validarAlteracaoValor(pagamento, request.getValorPagamento());
        }
        
        // Atualizar campos
        pagamento.setDataPagamento(request.getDataPagamento());
        pagamento.setValorPagamento(request.getValorPagamento());
        pagamento.setObservacao(request.getObservacao());
        pagamento.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar alterações
        Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamento);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(pagamento.getEmpenho());
        
        return converterParaResponseDTO(pagamentoAtualizado);
    }

    /**
     * Excluir pagamento
     */
    public void excluirPagamento(Long id, String userName) {
        Pagamento pagamento = buscarPagamentoPorId(id);
        
        // Validar se pode ser excluído
        validarSePermiteExclusao(pagamento);
        
        Empenho empenho = pagamento.getEmpenho();
        
        pagamentoRepository.delete(pagamento);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(empenho);
    }

    // ==========================================
    // MÉTODOS DE BUSCA POR EMPENHO
    // ==========================================

    /**
     * Listar pagamentos de um empenho específico
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarPorEmpenho(Long empenhoId) {
        List<Pagamento> pagamentos = pagamentoRepository.findByEmpenhoId(empenhoId);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar pagamentos por número do empenho
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarPorNumeroEmpenho(String numeroEmpenho) {
        List<Pagamento> pagamentos = pagamentoRepository.findByEmpenhoNumeroEmpenho(numeroEmpenho);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar pagamentos de uma despesa
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarPorDespesa(Long despesaId) {
        List<Pagamento> pagamentos = pagamentoRepository.findByDespesaId(despesaId);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar pagamentos por protocolo da despesa
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarPorProtocoloDespesa(String numeroProtocolo) {
        List<Pagamento> pagamentos = pagamentoRepository.findByDespesaNumeroProtocolo(numeroProtocolo);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS DE BUSCA E FILTROS
    // ==========================================

    /**
     * Buscar pagamentos com filtros
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarComFiltros(
            String numeroPagamento,
            Long empenhoId,
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorMinimo,
            BigDecimal valorMaximo) {
        
        List<Pagamento> pagamentos = pagamentoRepository.buscarComFiltros(
                numeroPagamento, empenhoId, dataInicio, dataFim, valorMinimo, valorMaximo);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar pagamentos por período
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Pagamento> pagamentos = pagamentoRepository.findByDataPagamentoBetween(dataInicio, dataFim);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar pagamentos por faixa de valor
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarPorFaixaValor(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        List<Pagamento> pagamentos = pagamentoRepository.findByValorPagamentoBetween(valorMinimo, valorMaximo);
        
        return pagamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar pagamentos do dia
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarPagamentosHoje() {
        LocalDate hoje = LocalDate.now();
        return buscarPorPeriodo(hoje, hoje);
    }

    /**
     * Buscar pagamentos da semana
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarPagamentosSemana() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate fimSemana = hoje.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        
        return buscarPorPeriodo(inicioSemana, fimSemana);
    }

    /**
     * Buscar pagamentos do mês
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarPagamentosMes() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth());
        
        return buscarPorPeriodo(inicioMes, fimMes);
    }

    // ==========================================
    // MÉTODOS ESPECÍFICOS DO CONTROLLER
    // ==========================================

    public PagamentoResponseDTO cancelarPagamento(Long id, String motivo, String userName) {
        Pagamento pagamento = buscarPagamentoPorId(id);
        
        // Validar se pode ser cancelado
        validarSePermiteCancelamento(pagamento);
        
        // Implementar lógica de cancelamento
        pagamento.setStatus("CANCELADO");
        pagamento.setObservacao(pagamento.getObservacao() + " | CANCELADO: " + motivo);
        pagamento.setDataAtualizacao(LocalDateTime.now());
        
        Pagamento pagamentoCancelado = pagamentoRepository.save(pagamento);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(pagamento.getEmpenho());
        
        return converterParaResponseDTO(pagamentoCancelado);
    }

    public PagamentoResponseDTO estornarPagamento(Long id, String motivo, String userName) {
        Pagamento pagamento = buscarPagamentoPorId(id);
        
        // Validar se pode ser estornado
        validarSePermiteEstorno(pagamento);
        
        // Implementar lógica de estorno
        pagamento.setStatus("ESTORNADO");
        pagamento.setObservacao(pagamento.getObservacao() + " | ESTORNADO: " + motivo);
        pagamento.setDataAtualizacao(LocalDateTime.now());
        
        Pagamento pagamentoEstornado = pagamentoRepository.save(pagamento);
        
        // Atualizar status da despesa
        atualizarStatusDespesa(pagamento.getEmpenho());
        
        return converterParaResponseDTO(pagamentoEstornado);
    }

    public String obterProximoNumero() {
        return sequenceGeneratorService.gerarNumeroPagamento();
    }

    public boolean verificarNumeroExiste(String numeroPagamento) {
        return pagamentoRepository.existsByNumeroPagamento(numeroPagamento);
    }

    public ValidacaoValorDTO validarValorPagamento(Long empenhoId, BigDecimal valorPagamento) {
        Empenho empenho = buscarEmpenhoPorId(empenhoId);
        
        BigDecimal valorTotalPago = empenho.getValorTotalPago();
        BigDecimal valorDisponivel = empenho.getValor().subtract(valorTotalPago);
        
        boolean valido = valorPagamento.compareTo(valorDisponivel) <= 0;
        
        ValidacaoValorDTO validacao = new ValidacaoValorDTO();
        validacao.setValido(valido);
        validacao.setValorSolicitado(valorPagamento);
        validacao.setValorDisponivel(valorDisponivel);
        validacao.setValorEmpenho(empenho.getValor());
        validacao.setValorTotalPago(valorTotalPago);
        
        if (!valido) {
            validacao.setMensagem(String.format(
                "Valor solicitado (R$ %.2f) excede o valor disponível (R$ %.2f)",
                valorPagamento, valorDisponivel));
        }
        
        return validacao;
    }

    // ==========================================
    // MÉTODOS DE VALIDAÇÃO
    // ==========================================

    private void validarPagamentoParaEmpenho(Empenho empenho, BigDecimal valorPagamento) {
        // Verificar se soma dos pagamentos não ultrapassa valor do empenho
        BigDecimal valorTotalPago = empenho.getValorTotalPago();
        BigDecimal novoTotal = valorTotalPago.add(valorPagamento);
        
        if (novoTotal.compareTo(empenho.getValor()) > 0) {
            throw new BusinessException(
                String.format("Valor do pagamento (R$ %.2f) excede o valor disponível do empenho (R$ %.2f). " +
                             "Valor já pago: R$ %.2f",
                    valorPagamento, 
                    empenho.getValor().subtract(valorTotalPago),
                    valorTotalPago));
        }
    }

    private void validarSePermiteAlteracao(Pagamento pagamento) {
        if ("CANCELADO".equals(pagamento.getStatus()) || "ESTORNADO".equals(pagamento.getStatus())) {
            throw new BusinessException("Não é possível alterar um pagamento cancelado ou estornado");
        }
    }

    private void validarSePermiteExclusao(Pagamento pagamento) {
        if ("CANCELADO".equals(pagamento.getStatus()) || "ESTORNADO".equals(pagamento.getStatus())) {
            throw new BusinessException("Não é possível excluir um pagamento cancelado ou estornado");
        }
    }

    private void validarSePermiteCancelamento(Pagamento pagamento) {
        if ("CANCELADO".equals(pagamento.getStatus())) {
            throw new BusinessException("Pagamento já está cancelado");
        }
        if ("ESTORNADO".equals(pagamento.getStatus())) {
            throw new BusinessException("Pagamento estornado não pode ser cancelado");
        }
    }

    private void validarSePermiteEstorno(Pagamento pagamento) {
        if ("ESTORNADO".equals(pagamento.getStatus())) {
            throw new BusinessException("Pagamento já está estornado");
        }
        if ("CANCELADO".equals(pagamento.getStatus())) {
            throw new BusinessException("Pagamento cancelado não pode ser estornado");
        }
    }

    private void validarAlteracaoValor(Pagamento pagamento, BigDecimal novoValor) {
        // Verificar se não excede valor disponível do empenho
        Empenho empenho = pagamento.getEmpenho();
        BigDecimal valorOutrosPagamentos = empenho.getValorTotalPago().subtract(pagamento.getValorPagamento());
        BigDecimal novoTotalPagamentos = valorOutrosPagamentos.add(novoValor);
        
        if (novoTotalPagamentos.compareTo(empenho.getValor()) > 0) {
            throw new BusinessException(
                String.format("Novo valor excede o valor disponível do empenho"));
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private Pagamento buscarPagamentoPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Pagamento não encontrado com ID: " + id));
    }

    private Empenho buscarEmpenhoPorId(Long id) {
        return empenhoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Empenho não encontrado com ID: " + id));
    }

    private Usuario buscarUsuarioPorNome(String userName) {
        return usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Usuário não encontrado: " + userName));
    }

    private void atualizarStatusDespesa(Empenho empenho) {
        empenho.getDespesa().setStatus(empenho.getDespesa().calculateStatus());
        despesaRepository.save(empenho.getDespesa());
    }

    private PagamentoResponseDTO converterParaResponseDTO(Pagamento pagamento) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();
        dto.setId(pagamento.getId());
        dto.setNumeroPagamento(pagamento.getNumeroPagamento());
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setValorPagamento(pagamento.getValorPagamento());
        dto.setObservacao(pagamento.getObservacao());
        dto.setStatus(pagamento.getStatus());
        dto.setDataCriacao(pagamento.getDataCriacao());
        dto.setDataAtualizacao(pagamento.getDataAtualizacao());
        
        // Dados do empenho
        if (pagamento.getEmpenho() != null) {
            EmpenhoResumoDTO empenhoResumo = new EmpenhoResumoDTO();
            empenhoResumo.setId(pagamento.getEmpenho().getId());
            empenhoResumo.setNumeroEmpenho(pagamento.getEmpenho().getNumeroEmpenho());
            empenhoResumo.setValor(pagamento.getEmpenho().getValor());
            empenhoResumo.setDataEmpenho(pagamento.getEmpenho().getDataEmpenho());
            dto.setEmpenho(empenhoResumo);
        }
        
        // Dados do usuário criador
        if (pagamento.getUsuarioCriador() != null) {
            dto.setUsuarioCriador(pagamento.getUsuarioCriador().getUserName());
        }
        
        return dto;
    }
}