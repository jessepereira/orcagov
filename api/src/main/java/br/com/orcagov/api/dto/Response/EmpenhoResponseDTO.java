package br.com.orcagov.api.dto.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpenhoResponseDTO {

    private Long id;
    private String numeroEmpenho;
    private LocalDate dataEmpenho;
    private BigDecimal valor;
    private String observacao;
    private BigDecimal valorTotalPago;
    private BigDecimal valorRestante;
    private DespesaResumoDTO despesa;
    private List<PagamentoResumoDTO> pagamentos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}