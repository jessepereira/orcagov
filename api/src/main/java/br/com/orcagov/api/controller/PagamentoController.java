package br.com.orcagov.api.controller;

import br.com.orcagov.api.dto.Common.ApiResponseDTO;
import br.com.orcagov.api.dto.Request.PagamentoRequestDTO;
import br.com.orcagov.api.dto.Response.PagamentoResponseDTO;
import br.com.orcagov.api.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    /**
     * Criar novo pagamento
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PagamentoResponseDTO>> criarPagamento(
            @Valid @RequestBody PagamentoRequestDTO request,
            Authentication authentication) {
        
        PagamentoResponseDTO pagamento = pagamentoService.criarPagamento(request, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(pagamento, "Pagamento criado com sucesso"));
    }

    /**
     * Buscar pagamento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PagamentoResponseDTO>> buscarPorId(@PathVariable Long id) {
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamento));
    }

    /**
     * Buscar pagamento por número
     */
    @GetMapping("/numero/{numeroPagamento}")
    public ResponseEntity<ApiResponseDTO<PagamentoResponseDTO>> buscarPorNumero(
            @PathVariable String numeroPagamento) {
        
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorNumero(numeroPagamento);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamento));
    }

    /**
     * Listar todos os pagamentos com paginação
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<PagamentoResponseDTO>>> listarTodos(Pageable pageable) {
        Page<PagamentoResponseDTO> pagamentos = pagamentoService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Atualizar pagamento
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PagamentoResponseDTO>> atualizarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoRequestDTO request,
            Authentication authentication) {
        
        PagamentoResponseDTO pagamento = pagamentoService.atualizarPagamento(id, request, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(pagamento, "Pagamento atualizado com sucesso"));
    }

    /**
     * Excluir pagamento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluirPagamento(
            @PathVariable Long id,
            Authentication authentication) {
        
        pagamentoService.excluirPagamento(id, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Pagamento excluído com sucesso"));
    }

    // ========================================
    // ENDPOINTS DE BUSCA POR EMPENHO
    // ========================================

    /**
     * Listar pagamentos de um empenho específico
     */
    @GetMapping("/empenho/{empenhoId}")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> listarPorEmpenho(
            @PathVariable Long empenhoId) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.listarPorEmpenho(empenhoId);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Listar pagamentos por número do empenho
     */
    @GetMapping("/empenho/numero/{numeroEmpenho}")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> listarPorNumeroEmpenho(
            @PathVariable String numeroEmpenho) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.listarPorNumeroEmpenho(numeroEmpenho);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Listar pagamentos de uma despesa (através dos empenhos)
     */
    @GetMapping("/despesa/{despesaId}")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> listarPorDespesa(
            @PathVariable Long despesaId) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.listarPorDespesa(despesaId);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Listar pagamentos por protocolo da despesa
     */
    @GetMapping("/despesa/protocolo/{numeroProtocolo}")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> listarPorProtocoloDespesa(
            @PathVariable String numeroProtocolo) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.listarPorProtocoloDespesa(numeroProtocolo);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    // ========================================
    // ENDPOINTS DE BUSCA E FILTROS
    // ========================================

    /**
     * Buscar pagamentos com filtros
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> buscarComFiltros(
            @RequestParam(required = false) String numeroPagamento,
            @RequestParam(required = false) Long empenhoId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.buscarComFiltros(
                numeroPagamento, empenhoId, dataInicio, dataFim, valorMinimo, valorMaximo);
        
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Buscar pagamentos por período
     */
    @GetMapping("/periodo")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> buscarPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.buscarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Buscar pagamentos por faixa de valor
     */
    @GetMapping("/valor")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> buscarPorFaixaValor(
            @RequestParam BigDecimal valorMinimo,
            @RequestParam BigDecimal valorMaximo) {
        
        List<PagamentoResponseDTO> pagamentos = pagamentoService.buscarPorFaixaValor(valorMinimo, valorMaximo);
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Buscar pagamentos do dia
     */
    @GetMapping("/hoje")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> buscarPagamentosHoje() {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.buscarPagamentosHoje();
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Buscar pagamentos da semana
     */
    @GetMapping("/semana")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> buscarPagamentosSemana() {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.buscarPagamentosSemana();
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    /**
     * Buscar pagamentos do mês
     */
    @GetMapping("/mes")
    public ResponseEntity<ApiResponseDTO<List<PagamentoResponseDTO>>> buscarPagamentosMes() {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.buscarPagamentosMes();
        return ResponseEntity.ok(ApiResponseDTO.success(pagamentos));
    }

    // ========================================
    // ENDPOINTS DE RELATÓRIOS E ESTATÍSTICAS
    // ========================================

    /**
     * Obter estatísticas dos pagamentos
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<ApiResponseDTO<PagamentoService.PagamentoEstatisticasDTO>> obterEstatisticas() {
        PagamentoService.PagamentoEstatisticasDTO stats = pagamentoService.obterEstatisticas();
        return ResponseEntity.ok(ApiResponseDTO.success(stats));
    }

    /**
     * Relatório de pagamentos por mês
     */
    @GetMapping("/relatorio/mensal")
    public ResponseEntity<ApiResponseDTO<List<PagamentoService.RelatorioMensalDTO>>> relatorioMensal() {
        List<PagamentoService.RelatorioMensalDTO> relatorio = pagamentoService.obterRelatorioMensal();
        return ResponseEntity.ok(ApiResponseDTO.success(relatorio));
    }

    /**
     * Relatório detalhado por período
     */
    @GetMapping("/relatorio/detalhado")
    public ResponseEntity<ApiResponseDTO<List<PagamentoService.RelatorioPagamentoDTO>>> relatorioDetalhado(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        
        List<PagamentoService.RelatorioPagamentoDTO> relatorio = 
            pagamentoService.obterRelatorioDetalhado(dataInicio, dataFim);
        
        return ResponseEntity.ok(ApiResponseDTO.success(relatorio));
    }

    /**
     * Validar se é possível adicionar valor ao pagamento
     */
    @GetMapping("/validar-valor/{empenhoId}")
    public ResponseEntity<ApiResponseDTO<PagamentoService.ValidacaoValorDTO>> validarValorPagamento(
            @PathVariable Long empenhoId,
            @RequestParam BigDecimal valorPagamento) {
        
        PagamentoService.ValidacaoValorDTO validacao = 
            pagamentoService.validarValorPagamento(empenhoId, valorPagamento);
        
        return ResponseEntity.ok(ApiResponseDTO.success(validacao));
    }

    // ========================================
    // ENDPOINTS DE AÇÕES ESPECÍFICAS
    // ========================================

    /**
     * Cancelar pagamento
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponseDTO<PagamentoResponseDTO>> cancelarPagamento(
            @PathVariable Long id,
            @RequestParam String motivo,
            Authentication authentication) {
        
        PagamentoResponseDTO pagamento = pagamentoService.cancelarPagamento(id, motivo, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(pagamento, "Pagamento cancelado com sucesso"));
    }

    /**
     * Estornar pagamento
     */
    @PatchMapping("/{id}/estornar")
    public ResponseEntity<ApiResponseDTO<PagamentoResponseDTO>> estornarPagamento(
            @PathVariable Long id,
            @RequestParam String motivo,
            Authentication authentication) {
        
        PagamentoResponseDTO pagamento = pagamentoService.estornarPagamento(id, motivo, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(pagamento, "Pagamento estornado com sucesso"));
    }

    /**
     * Obter próximo número de pagamento disponível
     */
    @GetMapping("/proximo-numero")
    public ResponseEntity<ApiResponseDTO<String>> obterProximoNumero() {
        String proximoNumero = pagamentoService.obterProximoNumero();
        return ResponseEntity.ok(ApiResponseDTO.success(proximoNumero));
    }

    /**
     * Verificar se número de pagamento já existe
     */
    @GetMapping("/verificar-numero/{numeroPagamento}")
    public ResponseEntity<ApiResponseDTO<Boolean>> verificarNumeroExiste(
            @PathVariable String numeroPagamento) {
        
        boolean existe = pagamentoService.verificarNumeroExiste(numeroPagamento);
        return ResponseEntity.ok(ApiResponseDTO.success(existe));
    }

    /**
     * Obter resumo financeiro de um empenho
     */
    @GetMapping("/resumo-empenho/{empenhoId}")
    public ResponseEntity<ApiResponseDTO<PagamentoService.ResumoFinanceiroDTO>> obterResumoEmpenho(
            @PathVariable Long empenhoId) {
        
        PagamentoService.ResumoFinanceiroDTO resumo = pagamentoService.obterResumoFinanceiro(empenhoId);
        return ResponseEntity.ok(ApiResponseDTO.success(resumo));
    }
}