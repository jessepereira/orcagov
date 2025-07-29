package br.com.orcagov.api.repository;

import br.com.orcagov.api.entity.Empenho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpenhoRepository extends JpaRepository<Empenho, Long> {
    
    // Buscar por número do empenho
    Optional<Empenho> findByNumeroEmpenho(String numeroEmpenho);
    
    // Verificar se número do empenho já existe
    boolean existsByNumeroEmpenho(String numeroEmpenho);
    
    // Buscar empenhos por despesa
    List<Empenho> findByDespesaId(Long despesaId);
    
    // Buscar empenhos por número de protocolo da despesa
    @Query("SELECT e FROM Empenho e WHERE e.despesa.numeroProtocolo = :numeroProtocolo")
    List<Empenho> findByDespesaNumeroProtocolo(@Param("numeroProtocolo") String numeroProtocolo);
    
    // Buscar empenhos por data
    List<Empenho> findByDataEmpenhoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    // Buscar empenhos por faixa de valor
    List<Empenho> findByValorBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);
    
    // Buscar empenhos sem pagamentos
    @Query("SELECT e FROM Empenho e WHERE e.pagamentos IS EMPTY")
    List<Empenho> findEmpenhosSemPagamentos();
    
    // Buscar empenhos com pagamentos
    @Query("SELECT DISTINCT e FROM Empenho e JOIN e.pagamentos p")
    List<Empenho> findEmpenhosComPagamentos();
    
    // Somar total de empenhos por despesa
    @Query("SELECT e.despesa.id, SUM(e.valor) FROM Empenho e GROUP BY e.despesa.id")
    List<Object[]> somarEmpenhosPorDespesa();
    
    // Buscar próximo número sequencial do ano
    @Query("SELECT COUNT(e) FROM Empenho e WHERE e.numeroEmpenho LIKE CONCAT(:ano, 'NE%')")
    Long contarEmpenhosPorAno(@Param("ano") String ano);
    
    // Relatório: Total empenhado por mês
    @Query("SELECT EXTRACT(YEAR FROM e.dataEmpenho), EXTRACT(MONTH FROM e.dataEmpenho), SUM(e.valor) " +
           "FROM Empenho e GROUP BY EXTRACT(YEAR FROM e.dataEmpenho), EXTRACT(MONTH FROM e.dataEmpenho) " +
           "ORDER BY EXTRACT(YEAR FROM e.dataEmpenho), EXTRACT(MONTH FROM e.dataEmpenho)")
    List<Object[]> totalEmpenhadoPorMes();
    
    // Validar se soma dos empenhos não ultrapassa valor da despesa
    @Query("SELECT CASE WHEN SUM(e.valor) <= e.despesa.valorDespesas THEN true ELSE false END " +
           "FROM Empenho e WHERE e.despesa.id = :despesaId")
    Boolean validarSomaEmpenhosDespesa(@Param("despesaId") Long despesaId);
    
    // Buscar empenhos com filtros
    @Query("SELECT e FROM Empenho e WHERE " +
           "(:numeroEmpenho IS NULL OR e.numeroEmpenho = :numeroEmpenho) AND " +
           "(:despesaId IS NULL OR e.despesa.id = :despesaId) AND " +
           "(:dataInicio IS NULL OR e.dataEmpenho >= :dataInicio) AND " +
           "(:dataFim IS NULL OR e.dataEmpenho <= :dataFim) AND " +
           "(:valorMinimo IS NULL OR e.valor >= :valorMinimo) AND " +
           "(:valorMaximo IS NULL OR e.valor <= :valorMaximo)")
    List<Empenho> buscarComFiltros(
            @Param("numeroEmpenho") String numeroEmpenho,
            @Param("despesaId") Long despesaId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("valorMinimo") BigDecimal valorMinimo,
            @Param("valorMaximo") BigDecimal valorMaximo);
}