package br.com.orcagov.api.controller;

import br.com.orcagov.api.dto.Common.ApiResponseDTO;
import br.com.orcagov.api.dto.Request.DespesaRequestDTO;
import br.com.orcagov.api.dto.Response.DespesaResponseDTO;
import br.com.orcagov.api.entity.enums.StatusDespesa;
import br.com.orcagov.api.entity.enums.TipoDespesa;
import br.com.orcagov.api.service.DespesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/despesas")
@CrossOrigin(origins = "*")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    /**
     * Criar nova despesa
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<DespesaResponseDTO>> criarDespesa(
            @Valid @RequestBody DespesaRequestDTO request,
            Authentication authentication) {
        
        DespesaResponseDTO despesa = despesaService.criarDespesa(request, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(despesa, "Despesa criada com sucesso"));
    }

    /**
     * Buscar despesa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DespesaResponseDTO>> buscarPorId(@PathVariable Long id) {
        DespesaResponseDTO despesa = despesaService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseDTO.success(despesa));
    }

    /**
     * Buscar despesa por número de protocolo
     */
    @GetMapping("/protocolo/{numeroProtocolo}")
    public ResponseEntity<ApiResponseDTO<DespesaResponseDTO>> buscarPorProtocolo(
            @PathVariable String numeroProtocolo) {
        
        DespesaResponseDTO despesa = despesaService.buscarPorProtocolo(numeroProtocolo);
        return ResponseEntity.ok(ApiResponseDTO.success(despesa));
    }

    /**
     * Listar todas as despesas com paginação
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<DespesaResponseDTO>>> listarTodas(Pageable pageable) {
        Page<DespesaResponseDTO> despesas = despesaService.listarTodas(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(despesas));
    }

    /**
     * Atualizar despesa
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DespesaResponseDTO>> atualizarDespesa(
            @PathVariable Long id,
            @Valid @RequestBody DespesaRequestDTO request,
            Authentication authentication) {
        
        DespesaResponseDTO despesa = despesaService.atualizarDespesa(id, request, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(despesa, "Despesa atualizada com sucesso"));
    }

    /**
     * Excluir despesa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> excluirDespesa(
            @PathVariable Long id,
            Authentication authentication) {
        
        despesaService.excluirDespesa(id, authentication.getName());
        
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Despesa excluída com sucesso"));
    }

    // ========================================
    // ENDPOINTS DE BUSCA E FILTROS
    // ========================================

    /**
     * Buscar despesas com filtros
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<DespesaResponseDTO>>> buscarComFiltros(
            @RequestParam(required = false) String numeroProtocolo,
            @RequestParam(required = false) TipoDespesa tipoDespesa,
            @RequestParam(required = false) StatusDespesa status,
            @RequestParam(required = false) String credor,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        
        List<DespesaResponseDTO> despesas = despesaService.buscarComFiltros(
                numeroProtocolo, tipoDespesa, status, credor, dataInicio, dataFim);
        
        return ResponseEntity.ok(ApiResponseDTO.success(despesas));
    }

    /**
     * Buscar despesas vencidas
     */
    @GetMapping("/vencidas")
    public ResponseEntity<ApiResponseDTO<List<DespesaResponseDTO>>> buscarDespesasVencidas() {
        List<DespesaResponseDTO> despesas = despesaService.buscarDespesasVencidas();
        return ResponseEntity.ok(ApiResponseDTO.success(despesas));
    }

    /**
     * Buscar despesas que vencem nos próximos X dias
     */
    @GetMapping("/vencendo")
    public ResponseEntity<ApiResponseDTO<List<DespesaResponseDTO>>> buscarDespesasVencendoEm(
            @RequestParam(defaultValue = "30") int dias) {
        
        List<DespesaResponseDTO> despesas = despesaService.buscarDespesasVencendoEm(dias);
        return ResponseEntity.ok(ApiResponseDTO.success(despesas));
    }

    /**
     * Buscar despesas por tipo
     */
    @GetMapping("/tipo/{tipoDespesa}")
    public ResponseEntity<ApiResponseDTO<List<DespesaResponseDTO>>> buscarPorTipo(
            @PathVariable TipoDespesa tipoDespesa) {
        
        List<DespesaResponseDTO> despesas = despesaService.buscarPorTipo(tipoDespesa);
        return ResponseEntity.ok(ApiResponseDTO.success(despesas));
    }

    /**
     * Buscar despesas por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<List<DespesaResponseDTO>>> buscarPorStatus(
            @PathVariable StatusDespesa status) {
        
        List<DespesaResponseDTO> despesas = despesaService.buscarPorStatus(status);
        return ResponseEntity.ok(ApiResponseDTO.success(despesas));
    }

    /**
     * Obter estatísticas das despesas
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<ApiResponseDTO<DespesaService.DespesaEstatisticasDTO>> obterEstatisticas() {
        DespesaService.DespesaEstatisticasDTO stats = despesaService.obterEstatisticas();
        return ResponseEntity.ok(ApiResponseDTO.success(stats));
    }
}