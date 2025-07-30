package br.com.orcagov.api.dto.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoResponseDTO {

    private Long id;
    private String numeroPagamento;
    private LocalDate dataPagamento;
    private BigDecimal valorPagamento;
    private String observacao;
    private EmpenhoResumoDTO empenho;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}