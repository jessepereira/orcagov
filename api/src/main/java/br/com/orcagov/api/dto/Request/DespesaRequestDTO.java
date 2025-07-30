package br.com.orcagov.api.dto.Request;

import br.com.orcagov.api.entity.enums.TipoDespesa;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespesaRequestDTO {

    @NotNull(message = "Tipo de despesa é obrigatório")
    private TipoDespesa tipoDespesa;

    @NotNull(message = "Data de vencimento é obrigatória")
    @FutureOrPresent(message = "Data de vencimento deve ser hoje ou no futuro")
    private LocalDate dataVencimento;

    @NotBlank(message = "Credor da despesa é obrigatório")
    @Size(min = 3, max = 255, message = "Credor deve ter entre 3 e 255 caracteres")
    private String credorDespesas;

    @NotBlank(message = "Descrição da despesa é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricaoDespesas;

    @NotNull(message = "Valor da despesa é obrigatório")
    @Positive(message = "Valor da despesa deve ser positivo")
    @DecimalMin(value = "0.01", message = "Valor mínimo é R$ 0,01")
    @DecimalMax(value = "999999999.99", message = "Valor máximo é R$ 999.999.999,99")
    private BigDecimal valorDespesas;
}