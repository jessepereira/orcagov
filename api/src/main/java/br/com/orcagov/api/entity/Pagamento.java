package br.com.orcagov.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Número do pagamento é obrigatório")
    @Pattern(regexp = "\\d{4}NP\\d{4}", 
             message = "Número do pagamento deve seguir o formato: anoAtualNPsequencial (ex: 2025NP0001)")
    @Column(name = "numero_pagamento", unique = true, nullable = false)
    private String numeroPagamento;

    @NotNull(message = "Data do pagamento é obrigatória")
    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @NotNull(message = "Valor do pagamento é obrigatório")
    @Positive(message = "Valor do pagamento deve ser positivo")
    @Column(name = "valor_pagamento", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPagamento;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empenho_id", nullable = false)
    @NotNull(message = "Pagamento deve estar associado a um empenho")
    @JsonBackReference
    private Empenho empenho;

    @CreatedDate
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Business methods
    public boolean validarValor() {
        if (empenho == null) {
            return false;
        }
        
        BigDecimal totalPagoOutros = empenho.getPagamentos().stream()
                .filter(p -> !p.getId().equals(this.getId()))
                .map(Pagamento::getValorPagamento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalPagoOutros.add(valorPagamento).compareTo(empenho.getValor()) <= 0;
    }
}