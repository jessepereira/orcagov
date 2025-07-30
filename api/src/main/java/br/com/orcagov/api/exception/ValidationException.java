package br.com.orcagov.api.exception;

import java.util.List;

/**
 * Exceção para erros de validação
 */
public class ValidationException extends RuntimeException {
    
    private List<String> errors;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}