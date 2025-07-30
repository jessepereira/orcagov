package br.com.orcagov.api.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import br.com.orcagov.api.entity.enums.TipoDespesa;
import br.com.orcagov.api.entity.enums.StatusDespesa;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Entity
@Table(name = "despesas")
public class Despesa {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_protocolo", unique = true, nullable = false, updatable = false)
    private String numeroProtocolo; // Removida a anotação @Pattern - será gerado automaticamente


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
    
    @ManyToOne
    @JoinColumn(name = "usuario_criador_id")
    private Usuario usuarioCriador;

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

    public StatusDespesa calculateStatus() {
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


    public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getNumeroProtocolo() {
    return numeroProtocolo;
}

public void setNumeroProtocolo(String numeroProtocolo) {
    this.numeroProtocolo = numeroProtocolo;
}

public TipoDespesa getTipoDespesa() {
    return tipoDespesa;
}

public void setTipoDespesa(TipoDespesa tipoDespesa) {
    this.tipoDespesa = tipoDespesa;
}

public LocalDateTime getDataProtocolo() {
    return dataProtocolo;
}

public void setDataProtocolo(LocalDateTime dataProtocolo) {
    this.dataProtocolo = dataProtocolo;
}

public LocalDate getDataVencimento() {
    return dataVencimento;
}

public void setDataVencimento(LocalDate dataVencimento) {
    this.dataVencimento = dataVencimento;
}

public String getCredorDespesas() {
    return credorDespesas;
}

public void setCredorDespesas(String credorDespesas) {
    this.credorDespesas = credorDespesas;
}

public String getDescricaoDespesas() {
    return descricaoDespesas;
}

public void setDescricaoDespesas(String descricaoDespesas) {
    this.descricaoDespesas = descricaoDespesas;
}

public BigDecimal getValorDespesas() {
    return valorDespesas;
}

public void setValorDespesas(BigDecimal valorDespesas) {
    this.valorDespesas = valorDespesas;
}

public void setStatus(StatusDespesa status) {
    this.status = status;
}

public List<Empenho> getEmpenhos() {
    return empenhos;
}

public void setEmpenhos(List<Empenho> empenhos) {
    this.empenhos = empenhos;
}

public Usuario getUsuarioCriador() {
    return usuarioCriador;
}

public void setUsuarioCriador(Usuario usuarioCriador) {
    this.usuarioCriador = usuarioCriador;
}

public LocalDateTime getDataCriacao() {
    return dataCriacao;
}

public void setDataCriacao(LocalDateTime dataCriacao) {
    this.dataCriacao = dataCriacao;
}

public LocalDateTime getDataAtualizacao() {
    return dataAtualizacao;
}

public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
    this.dataAtualizacao = dataAtualizacao;
}

}

