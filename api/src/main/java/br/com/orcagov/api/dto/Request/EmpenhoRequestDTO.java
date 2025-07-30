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
public class EmpenhoRequestDTO {

    @NotNull(message = "Data do empenho é obrigatória")
    private LocalDate dataEmpenho;

    @NotNull(message = "Valor do empenho é obrigatório")
    @Positive(message = "Valor do empenho deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor mínimo é R$ 0,01")
    private BigDecimal valor;

    @Size(max = 1000, message = "Observação deve ter no máximo 1000 caracteres")
    private String observacao;

    @NotNull(message = "ID da despesa é obrigatório")
    @Positive(message = "ID da despesa deve ser positivo")
    private Long despesaId;
}
