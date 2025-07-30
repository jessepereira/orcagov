package br.com.orcagov.api.dto.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoResumoDTO {

    private Long id;
    private String numeroPagamento;
    private LocalDate dataPagamento;
    private BigDecimal valorPagamento;
}