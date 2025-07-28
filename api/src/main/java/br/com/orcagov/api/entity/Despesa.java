package br.com.orcagov.api.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.processing.Pattern;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "despesas")
public class Despesa {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_protocolo", unique = true, nullable = false, updatable = false)
    @Pattern(regexp = "\\d{5}\\.\\d{6}/\\d{4}-\\d{2}", 
      message = "Número do protocolo deve seguir o formato: #####.######/####-##")
    private String numeroProtocolo;


    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo de despesa é obrigatório")
    @Column(name = "tipo_despesa", nullable = false)
    private TipoDespesa tipoDespesa;

    @NotNull(message = "Data do protocolo é obrigatória")
    @Column(name = "data_protocolo", nullable = false)
    private LocalDateTime dataProtocolo;

    @NotNull(message = "Data de vencimento é obrigatória")
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @NotBlank(message = "Credor da despesa é obrigatório")
    @Column(name = "credor_despesas", nullable = false)
    private String credorDespesas;

    @NotBlank(message = "Descrição da despesa é obrigatória")
    @Column(name = "descricao_despesas", nullable = false, columnDefinition = "TEXT")
    private String descricaoDespesas;

    @NotNull(message = "Valor da despesa é obrigatório")
    @Positive(message = "Valor da despesa deve ser positivo")
    @Column(name = "valor_despesas", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDespesas;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusDespesa status;

    @OneToMany(mappedBy = "despesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Empenho> empenhos = new ArrayList<>();

    @CreatedDate
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Business methods
    public StatusDespesa getStatus() {
        if (status != null) {
            return status;
        }
        return calculateStatus();
    }

    public void adicionarEmpenho(Empenho empenho) {
        empenhos.add(empenho);
        empenho.setDespesa(this);
        this.status = calculateStatus();
    }

    public void removerEmpenho(Empenho empenho) {
        empenhos.remove(empenho);
        empenho.setDespesa(null);
        this.status = calculateStatus();
    }

    public BigDecimal getValorTotalEmpenhado() {
        return empenhos.stream()
                .map(Empenho::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTotalPago() {
        return empenhos.stream()
                .flatMap(empenho -> empenho.getPagamentos().stream())
                .map(Pagamento::getValorPagamento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean validarValores() {
        return getValorTotalEmpenhado().compareTo(valorDespesas) <= 0;
    }

    private StatusDespesa calculateStatus() {
        if (empenhos.isEmpty()) {
            return StatusDespesa.AGUARDANDO_EMPENHO;
        }

        BigDecimal totalEmpenhado = getValorTotalEmpenhado();
        BigDecimal totalPago = getValorTotalPago();

        if (totalPago.compareTo(valorDespesas) == 0) {
            return StatusDespesa.PAGA;
        }

        if (totalEmpenhado.compareTo(valorDespesas) < 0) {
            return StatusDespesa.PARCIALMENTE_EMPENHADA;
        }

        if (totalEmpenhado.compareTo(valorDespesas) == 0) {
            if (totalPago.compareTo(BigDecimal.ZERO) == 0) {
                return StatusDespesa.AGUARDANDO_PAGAMENTO;
            } else if (totalPago.compareTo(valorDespesas) < 0) {
                return StatusDespesa.PARCIALMENTE_PAGA;
            }
        }

        return StatusDespesa.AGUARDANDO_EMPENHO;
    }

    public enum TipoDespesa {
        OBRA_DE_EDIFICACAO("Obra de Edificação"),
        OBRA_DE_RODOVIAS("Obra de Rodovias"),
        OUTROS("Outros");

        private final String descricao;

        TipoDespesa(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusDespesa {
        AGUARDANDO_EMPENHO("Aguardando Empenho"),
        PARCIALMENTE_EMPENHADA("Parcialmente Empenhada"),
        AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
        PARCIALMENTE_PAGA("Parcialmente Paga"),
        PAGA("Paga");

        private final String descricao;

        StatusDespesa(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
}
