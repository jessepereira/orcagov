package br.com.orcagov.api.exception;

/**
 * Exceção para regras de negócio violadas
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}