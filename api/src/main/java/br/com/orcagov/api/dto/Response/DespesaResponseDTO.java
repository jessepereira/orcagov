package br.com.orcagov.api.dto.Response;

import br.com.orcagov.api.entity.enums.StatusDespesa;
import br.com.orcagov.api.entity.enums.TipoDespesa;
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
public class DespesaResponseDTO {

    private Long id;
    private String numeroProtocolo;
    private TipoDespesa tipoDespesa;
    private LocalDateTime dataProtocolo;
    private LocalDate dataVencimento;
    private String credorDespesas;
    private String descricaoDespesas;
    private BigDecimal valorDespesas;
    private StatusDespesa status;
    private BigDecimal valorTotalEmpenhado;
    private BigDecimal valorTotalPago;
    private BigDecimal valorRestante;
    private Boolean vencida;
    private List<EmpenhoResumoDTO> empenhos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}