package br.com.orcagov.api.controller;

import br.com.orcagov.api.dto.Common.ApiResponseDTO;
import br.com.orcagov.api.dto.Request.EmpenhoRequestDTO;
import br.com.orcagov.api.dto.Response.EmpenhoResponseDTO;
import br.com.orcagov.api.service.EmpenhoService;
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
@RequestMapping("/empenhos")
@CrossOrigin(origins = "*")
public class EmpenhoController {

    @Autowired
    private EmpenhoService empenhoService;

    /**
     * Criar novo empenho
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmpenhoResponseDTO>> criarEmpenho(
            @Valid @RequestBody EmpenhoRequestDTO request,
            Authentication authentication) {
        
        EmpenhoResponseDTO empenho = empenhoService.criarEmpenho(request, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(empenho, "Empenho criado com sucesso"));
    }

    /**
     * Buscar empenho por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpenhoResponseDTO>> buscarPorId(@PathVariable Long id) {
        EmpenhoResponseDTO empenho = empenhoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseDTO.success(empenho));
    }

    /**
     * Buscar empenho por número
     */
    @GetMapping("/numero/{numeroEmpenho}")
    public ResponseEntity<ApiResponseDTO<EmpenhoResponseDTO>> buscarPorNumero(
            @PathVariable String numeroEmpenho) {
        
        EmpenhoResponseDTO empenho = empenhoService.buscarPorNumero(numeroEmpenho);
        return ResponseEntity.ok(ApiResponseDTO.success(empenho));
    }

    /**
     * Listar todos os empenhos com paginação
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<EmpenhoResponseDTO>>> listarTodos(Pageable pageable) {
        Page<EmpenhoResponseDTO> empenhos = empenhoService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    /**
     * Atualizar empenho
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmpenhoResponseDTO>> atualizarEmpenho(
            @PathVariable Long id,
            @Valid @RequestBody EmpenhoRequestDTO request,
            Authentication authentication) {
        
        EmpenhoResponseDTO empenho = empenhoService.atualizarEmpenho(id, request, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(empenho, "Empenho atualizado com sucesso"));
    }

    /**
     * Excluir empenho
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluirEmpenho(
            @PathVariable Long id,
            Authentication authentication) {
        
        empenhoService.excluirEmpenho(id, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Empenho excluído com sucesso"));
    }

    // ========================================
    // ENDPOINTS DE BUSCA POR DESPESA
    // ========================================

    /**
     * Listar empenhos de uma despesa específica
     */
    @GetMapping("/despesa/{despesaId}")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> listarPorDespesa(
            @PathVariable Long despesaId) {
        
        List<EmpenhoResponseDTO> empenhos = empenhoService.listarPorDespesa(despesaId);
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    /**
     * Listar empenhos por número de protocolo da despesa
     */
    @GetMapping("/despesa/protocolo/{numeroProtocolo}")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> listarPorProtocoloDespesa(
            @PathVariable String numeroProtocolo) {
        
        List<EmpenhoResponseDTO> empenhos = empenhoService.listarPorProtocoloDespesa(numeroProtocolo);
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    // ========================================
    // ENDPOINTS DE BUSCA E FILTROS
    // ========================================

    /**
     * Buscar empenhos com filtros
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> buscarComFiltros(
            @RequestParam(required = false) String numeroEmpenho,
            @RequestParam(required = false) Long despesaId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo) {
        
        List<EmpenhoResponseDTO> empenhos = empenhoService.buscarComFiltros(
                numeroEmpenho, despesaId, dataInicio, dataFim, valorMinimo, valorMaximo);
        
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    /**
     * Buscar empenhos por período
     */
    @GetMapping("/periodo")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> buscarPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        
        List<EmpenhoResponseDTO> empenhos = empenhoService.buscarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    /**
     * Buscar empenhos por faixa de valor
     */
    @GetMapping("/valor")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> buscarPorFaixaValor(
            @RequestParam BigDecimal valorMinimo,
            @RequestParam BigDecimal valorMaximo) {
        
        List<EmpenhoResponseDTO> empenhos = empenhoService.buscarPorFaixaValor(valorMinimo, valorMaximo);
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    /**
     * Buscar empenhos sem pagamentos
     */
    @GetMapping("/sem-pagamentos")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> buscarSemPagamentos() {
        List<EmpenhoResponseDTO> empenhos = empenhoService.buscarSemPagamentos();
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    /**
     * Buscar empenhos com pagamentos
     */
    @GetMapping("/com-pagamentos")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoResponseDTO>>> buscarComPagamentos() {
        List<EmpenhoResponseDTO> empenhos = empenhoService.buscarComPagamentos();
        return ResponseEntity.ok(ApiResponseDTO.success(empenhos));
    }

    // ========================================
    // ENDPOINTS DE RELATÓRIOS E ESTATÍSTICAS
    // ========================================

    /**
     * Obter estatísticas dos empenhos
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<ApiResponseDTO<EmpenhoService.EmpenhoEstatisticasDTO>> obterEstatisticas() {
        EmpenhoService.EmpenhoEstatisticasDTO stats = empenhoService.obterEstatisticas();
        return ResponseEntity.ok(ApiResponseDTO.success(stats));
    }

    /**
     * Relatório de empenhos por mês
     */
    @GetMapping("/relatorio/mensal")
    public ResponseEntity<ApiResponseDTO<List<EmpenhoService.RelatorioMensalDTO>>> relatorioMensal() {
        List<EmpenhoService.RelatorioMensalDTO> relatorio = empenhoService.obterRelatorioMensal();
        return ResponseEntity.ok(ApiResponseDTO.success(relatorio));
    }

    /**
     * Validar se é possível adicionar valor ao empenho
     */
    @GetMapping("/validar-valor/{despesaId}")
    public ResponseEntity<ApiResponseDTO<EmpenhoService.ValidacaoValorDTO>> validarValorEmpenho(
            @PathVariable Long despesaId,
            @RequestParam BigDecimal valorEmpenho) {
        
        EmpenhoService.ValidacaoValorDTO validacao = empenhoService.validarValorEmpenho(despesaId, valorEmpenho);
        return ResponseEntity.ok(ApiResponseDTO.success(validacao));
    }

    // ========================================
    // ENDPOINTS DE AÇÕES ESPECÍFICAS
    // ========================================

    /**
     * Cancelar empenho (se não tiver pagamentos)
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponseDTO<EmpenhoResponseDTO>> cancelarEmpenho(
            @PathVariable Long id,
            @RequestParam String motivo,
            Authentication authentication) {
        
        EmpenhoResponseDTO empenho = empenhoService.cancelarEmpenho(id, motivo, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(empenho, "Empenho cancelado com sucesso"));
    }

    /**
     * Reativar empenho cancelado
     */
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ApiResponseDTO<EmpenhoResponseDTO>> reativarEmpenho(
            @PathVariable Long id,
            Authentication authentication) {
        
        EmpenhoResponseDTO empenho = empenhoService.reativarEmpenho(id, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(empenho, "Empenho reativado com sucesso"));
    }

    /**
     * Obter próximo número de empenho disponível
     */
    @GetMapping("/proximo-numero")
    public ResponseEntity<ApiResponseDTO<String>> obterProximoNumero() {
        String proximoNumero = empenhoService.obterProximoNumero();
        return ResponseEntity.ok(ApiResponseDTO.success(proximoNumero));
    }

    /**
     * Verificar se número de empenho já existe
     */
    @GetMapping("/verificar-numero/{numeroEmpenho}")
    public ResponseEntity<ApiResponseDTO<Boolean>> verificarNumeroExiste(
            @PathVariable String numeroEmpenho) {
        
        boolean existe = empenhoService.verificarNumeroExiste(numeroEmpenho);
        return ResponseEntity.ok(ApiResponseDTO.success(existe));
    }
}
