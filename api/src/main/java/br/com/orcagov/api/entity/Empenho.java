package br.com.orcagov.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empenhos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Empenho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Número do empenho é obrigatório")
    @Pattern(regexp = "\\d{4}NE\\d{4}", 
             message = "Número do empenho deve seguir o formato: anoAtualNEsequencial (ex: 2025NE0001)")
    @Column(name = "numero_empenho", unique = true, nullable = false)
    private String numeroEmpenho;

    @NotNull(message = "Data do empenho é obrigatória")
    @Column(name = "data_empenho", nullable = false)
    private LocalDate dataEmpenho;

    @NotNull(message = "Valor do empenho é obrigatório")
    @Positive(message = "Valor do empenho deve ser positivo")
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "despesa_id", nullable = false)
    @NotNull(message = "Empenho deve estar associado a uma despesa")
    @JsonBackReference
    private Despesa despesa;

    @OneToMany(mappedBy = "empenho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Pagamento> pagamentos = new ArrayList<>();

    @CreatedDate
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Business methods
    public void adicionarPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
        pagamento.setEmpenho(this);
    }

    public void removerPagamento(Pagamento pagamento) {
        pagamentos.remove(pagamento);
        pagamento.setEmpenho(null);
    }

    public BigDecimal getValorTotalPago() {
        return pagamentos.stream()
                .map(Pagamento::getValorPagamento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean validarValores() {
        return getValorTotalPago().compareTo(valor) <= 0;
    }

    public boolean temPagamentos() {
        return !pagamentos.isEmpty();
    }
}