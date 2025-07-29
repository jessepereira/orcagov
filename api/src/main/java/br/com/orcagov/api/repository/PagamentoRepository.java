package br.com.orcagov.api.repository;

import br.com.orcagov.api.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
    // Buscar por número do pagamento
    Optional<Pagamento> findByNumeroPagamento(String numeroPagamento);
    
    // Verificar se número do pagamento já existe
    boolean existsByNumeroPagamento(String numeroPagamento);
    
    // Buscar pagamentos por empenho
    List<Pagamento> findByEmpenhoId(Long empenhoId);
    
    // Buscar pagamentos por número do empenho
    @Query("SELECT p FROM Pagamento p WHERE p.empenho.numeroEmpenho = :numeroEmpenho")
    List<Pagamento> findByEmpenhoNumeroEmpenho(@Param("numeroEmpenho") String numeroEmpenho);
    
    // Buscar pagamentos por despesa (através do empenho)
    @Query("SELECT p FROM Pagamento p WHERE p.empenho.despesa.id = :despesaId")
    List<Pagamento> findByDespesaId(@Param("despesaId") Long despesaId);
    
    // Buscar pagamentos por protocolo da despesa
    @Query("SELECT p FROM Pagamento p WHERE p.empenho.despesa.numeroProtocolo = :numeroProtocolo")
    List<Pagamento> findByDespesaNumeroProtocolo(@Param("numeroProtocolo") String numeroProtocolo);
    
    // Buscar pagamentos por data
    List<Pagamento> findByDataPagamentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    // Buscar pagamentos por faixa de valor
    List<Pagamento> findByValorPagamentoBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);
    
    // Somar total de pagamentos por empenho
    @Query("SELECT p.empenho.id, SUM(p.valorPagamento) FROM Pagamento p GROUP BY p.empenho.id")
    List<Object[]> somarPagamentosPorEmpenho();
    
    // Somar total de pagamentos por despesa
    @Query("SELECT p.empenho.despesa.id, SUM(p.valorPagamento) FROM Pagamento p GROUP BY p.empenho.despesa.id")
    List<Object[]> somarPagamentosPorDespesa();
    
    // Buscar próximo número sequencial do ano
    @Query("SELECT COUNT(p) FROM Pagamento p WHERE p.numeroPagamento LIKE CONCAT(:ano, 'NP%')")
    Long contarPagamentosPorAno(@Param("ano") String ano);
    
    // Relatório: Total pago por mês
    @Query("SELECT EXTRACT(YEAR FROM p.dataPagamento), EXTRACT(MONTH FROM p.dataPagamento), SUM(p.valorPagamento) " +
           "FROM Pagamento p GROUP BY EXTRACT(YEAR FROM p.dataPagamento), EXTRACT(MONTH FROM p.dataPagamento) " +
           "ORDER BY EXTRACT(YEAR FROM p.dataPagamento), EXTRACT(MONTH FROM p.dataPagamento)")
    List<Object[]> totalPagoPorMes();
    
    // Validar se soma dos pagamentos não ultrapassa valor do empenho
    @Query("SELECT CASE WHEN SUM(p.valorPagamento) <= p.empenho.valor THEN true ELSE false END " +
           "FROM Pagamento p WHERE p.empenho.id = :empenhoId")
    Boolean validarSomaPagamentosEmpenho(@Param("empenhoId") Long empenhoId);
    
    // Relatório: Pagamentos por período com detalhes
    @Query("SELECT p.numeroPagamento, p.dataPagamento, p.valorPagamento, " +
           "p.empenho.numeroEmpenho, p.empenho.despesa.numeroProtocolo, " +
           "p.empenho.despesa.credorDespesas " +
           "FROM Pagamento p WHERE p.dataPagamento BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY p.dataPagamento DESC")
    List<Object[]> relatorioPagamentosPorPeriodo(@Param("dataInicio") LocalDate dataInicio, 
                                                @Param("dataFim") LocalDate dataFim);
    
    // Buscar pagamentos com filtros
    @Query("SELECT p FROM Pagamento p WHERE " +
           "(:numeroPagamento IS NULL OR p.numeroPagamento = :numeroPagamento) AND " +
           "(:empenhoId IS NULL OR p.empenho.id = :empenhoId) AND " +
           "(:dataInicio IS NULL OR p.dataPagamento >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataPagamento <= :dataFim) AND " +
           "(:valorMinimo IS NULL OR p.valorPagamento >= :valorMinimo) AND " +
           "(:valorMaximo IS NULL OR p.valorPagamento <= :valorMaximo)")
    List<Pagamento> buscarComFiltros(
            @Param("numeroPagamento") String numeroPagamento,
            @Param("empenhoId") Long empenhoId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("valorMinimo") BigDecimal valorMinimo,
            @Param("valorMaximo") BigDecimal valorMaximo);
}
