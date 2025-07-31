package br.com.orcagov.api.service;

import br.com.orcagov.api.repository.EmpenhoRepository;
import br.com.orcagov.api.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class SequenceGeneratorService {
    
    @Autowired
    private EmpenhoRepository empenhoRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    /**
     * Gera número de empenho no formato: 2025NE0001
     */
    @Transactional
    public String gerarNumeroEmpenho() {
        String anoAtual = String.valueOf(Year.now().getValue());
        Long count = empenhoRepository.contarEmpenhosPorAno(anoAtual);
        long proximoSequencial = (count != null ? count : 0) + 1;
        
        String numeroGerado;
        boolean numeroExiste;
        
        // Garantir que o número não existe (double-check)
        do {
            numeroGerado = String.format("%sNE%04d", anoAtual, proximoSequencial);
            numeroExiste = empenhoRepository.existsByNumeroEmpenho(numeroGerado);
            if (numeroExiste) {
                proximoSequencial++;
            }
        } while (numeroExiste);
        
        return numeroGerado;
    }
    
    /**
     * Gera número de pagamento no formato: 2025NP0001
     */
    @Transactional
    public String gerarNumeroPagamento() {
        String anoAtual = String.valueOf(Year.now().getValue());
        Long count = pagamentoRepository.contarPagamentosPorAno(anoAtual);
        long proximoSequencial = (count != null ? count : 0) + 1;
        
        String numeroGerado;
        boolean numeroExiste;
        
        // Garantir que o número não existe (double-check)
        do {
            numeroGerado = String.format("%sNP%04d", anoAtual, proximoSequencial);
            numeroExiste = pagamentoRepository.existsByNumeroPagamento(numeroGerado);
            if (numeroExiste) {
                proximoSequencial++;
            }
        } while (numeroExiste);
        
        return numeroGerado;
    }
    
    /**
     * Validar formato do número de empenho
     */
    public boolean validarFormatoEmpenho(String numeroEmpenho) {
        if (numeroEmpenho == null) {
            return false;
        }
        // Formato: yyyyNEnnnn (ex: 2025NE0001)
        return numeroEmpenho.matches("\\d{4}NE\\d{4}");
    }
    
    /**
     * Validar formato do número de pagamento
     */
    public boolean validarFormatoPagamento(String numeroPagamento) {
        if (numeroPagamento == null) {
            return false;
        }
        // Formato: yyyyNPnnnn (ex: 2025NP0001)
        return numeroPagamento.matches("\\d{4}NP\\d{4}");
    }
    
    /**
     * Extrair ano do número de empenho
     */
    public String extrairAnoEmpenho(String numeroEmpenho) {
        if (validarFormatoEmpenho(numeroEmpenho)) {
            return numeroEmpenho.substring(0, 4);
        }
        return null;
    }
    
    /**
     * Extrair ano do número de pagamento
     */
    public String extrairAnoPagamento(String numeroPagamento) {
        if (validarFormatoPagamento(numeroPagamento)) {
            return numeroPagamento.substring(0, 4);
        }
        return null;
    }
    
    /**
     * Extrair sequencial do número de empenho
     */
    public Integer extrairSequencialEmpenho(String numeroEmpenho) {
        if (validarFormatoEmpenho(numeroEmpenho)) {
            String sequencial = numeroEmpenho.substring(6); // Após "yyyyNE"
            return Integer.parseInt(sequencial);
        }
        return null;
    }
    
    /**
     * Extrair sequencial do número de pagamento
     */
    public Integer extrairSequencialPagamento(String numeroPagamento) {
        if (validarFormatoPagamento(numeroPagamento)) {
            String sequencial = numeroPagamento.substring(6); // Após "yyyyNP"
            return Integer.parseInt(sequencial);
        }
        return null;
    }
}