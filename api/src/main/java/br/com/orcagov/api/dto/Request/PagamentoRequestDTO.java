package br.com.orcagov.api.dto.Request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {

    @NotNull(message = "Data do pagamento é obrigatória")
    private LocalDate dataPagamento;

    @NotNull(message = "Valor do pagamento é obrigatório")
    @Positive(message = "Valor do pagamento deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor mínimo é R$ 0,01")
    private BigDecimal valorPagamento;

    @Size(max = 1000, message = "Observação deve ter no máximo 1000 caracteres")
    private String observacao;

    @NotNull(message = "ID do empenho é obrigatório")
    @Positive(message = "ID do empenho deve ser positivo")
    private Long empenhoId;
}