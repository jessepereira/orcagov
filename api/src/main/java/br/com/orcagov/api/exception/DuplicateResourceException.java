package br.com.orcagov.api.exception;

/**
 * Exceção para recursos duplicados
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: %s", 
              resourceName, fieldName, fieldValue));
    }
}