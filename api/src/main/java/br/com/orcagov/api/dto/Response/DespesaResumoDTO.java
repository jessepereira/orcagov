package br.com.orcagov.api.dto.Response;

import br.com.orcagov.api.entity.enums.StatusDespesa;
import br.com.orcagov.api.entity.enums.TipoDespesa;
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
public class DespesaResumoDTO {

    private Long id;
    private String numeroProtocolo;
    private TipoDespesa tipoDespesa;
    private LocalDate dataVencimento;
    private String credorDespesas;
    private BigDecimal valorDespesas;
    private StatusDespesa status;
}