package br.com.orcagov.api.service;

import br.com.orcagov.api.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProtocoloGeneratorService {
    
    @Autowired
    private DespesaRepository despesaRepository;
    
    /**
     * Gera número de protocolo no formato: 43022.000001/2025-07
     */
    @Transactional
    public String gerarNumeroProtocolo() {
        LocalDateTime agora = LocalDateTime.now();
        int ano = agora.getYear();
        
        // Busca quantos protocolos já existem no ano atual
        Long count = despesaRepository.contarProtocolosPorAno(String.valueOf(ano));
        long proximoSequencial = (count != null ? count : 0) + 1;
        
        String codigoOrgao = "43022";
        String sequencialFormatado = String.format("%06d", proximoSequencial);
        String digitoVerificador = calcularDigitoVerificador(codigoOrgao + sequencialFormatado + ano);
        
        return String.format("%s.%s/%d-%s", 
                codigoOrgao, 
                sequencialFormatado, 
                ano, 
                digitoVerificador);
    }
    
    /**
     * Calcula dígito verificador usando algoritmo módulo 11
     */
    private String calcularDigitoVerificador(String numero) {
        int soma = 0;
        int peso = 2;
        
        for (int i = numero.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(numero.charAt(i)) * peso;
            peso++;
            if (peso > 9) {
                peso = 2;
            }
        }
        
        int resto = soma % 11;
        int digito = 11 - resto;
        
        if (digito >= 10) {
            digito = 0;
        }
        
        return String.format("%02d", digito);
    }
}