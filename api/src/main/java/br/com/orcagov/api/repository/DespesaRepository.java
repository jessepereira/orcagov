package br.com.orcagov.api.repository;

import br.com.orcagov.api.entity.Despesa;
import br.com.orcagov.api.entity.enums.StatusDespesa;
import br.com.orcagov.api.entity.enums.TipoDespesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    
    // Buscar por número de protocolo
    Optional<Despesa> findByNumeroProtocolo(String numeroProtocolo);
    
    // Verificar se protocolo já existe
    boolean existsByNumeroProtocolo(String numeroProtocolo);
    
    // Buscar por tipo de despesa
    List<Despesa> findByTipoDespesa(TipoDespesa tipoDespesa);
    
    // Buscar por status
    List<Despesa> findByStatus(StatusDespesa status);
    
    // Buscar por credor
    List<Despesa> findByCredorDespesasContainingIgnoreCase(String credor);
    
    // Buscar despesas por data de vencimento
    List<Despesa> findByDataVencimentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    // Buscar despesas vencidas
    List<Despesa> findByDataVencimentoBeforeOrderByDataVencimentoAsc(LocalDate dataAtual);
    
    // Buscar despesas que vencem em X dias
    @Query("SELECT d FROM Despesa d WHERE d.dataVencimento BETWEEN :dataAtual AND :dataLimite")
    List<Despesa> findDespesasVencendoEm(@Param("dataAtual") LocalDate dataAtual, 
                                        @Param("dataLimite") LocalDate dataLimite);
    
    // Buscar por faixa de valor
    List<Despesa> findByValorDespesasBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);
    
    // Buscar despesas por período de protocolo
    List<Despesa> findByDataProtocoloBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    
    // Buscar despesas sem empenhos
    @Query("SELECT d FROM Despesa d WHERE d.empenhos IS EMPTY")
    List<Despesa> findDespesasSemEmpenhos();
    
    // Buscar despesas com empenhos
    @Query("SELECT DISTINCT d FROM Despesa d JOIN d.empenhos e")
    List<Despesa> findDespesasComEmpenhos();
    
    // Relatório: Soma total de despesas por tipo
    @Query("SELECT d.tipoDespesa, SUM(d.valorDespesas) FROM Despesa d GROUP BY d.tipoDespesa")
    List<Object[]> somarDespesasPorTipo();
    
    // Relatório: Contagem de despesas por status
    @Query("SELECT d.status, COUNT(d) FROM Despesa d GROUP BY d.status")
    List<Object[]> contarDespesasPorStatus();
    
    // Busca completa com filtros
    @Query("SELECT d FROM Despesa d WHERE " +
           "(:numeroProtocolo IS NULL OR d.numeroProtocolo = :numeroProtocolo) AND " +
           "(:tipoDespesa IS NULL OR d.tipoDespesa = :tipoDespesa) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:credor IS NULL OR LOWER(d.credorDespesas) LIKE LOWER(CONCAT('%', :credor, '%'))) AND " +
           "(:dataInicio IS NULL OR d.dataVencimento >= :dataInicio) AND " +
           "(:dataFim IS NULL OR d.dataVencimento <= :dataFim)")
    List<Despesa> buscarComFiltros(
            @Param("numeroProtocolo") String numeroProtocolo,
            @Param("tipoDespesa") TipoDespesa tipoDespesa,
            @Param("status") StatusDespesa status,
            @Param("credor") String credor,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);
}