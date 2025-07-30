package br.com.orcagov.api.controller.handler;

import br.com.orcagov.api.dto.Common.ApiResponseDTO;
import br.com.orcagov.api.dto.Common.ErrorResponseDTO;
import br.com.orcagov.api.exception.BusinessException;
import br.com.orcagov.api.exception.DuplicateResourceException;
import br.com.orcagov.api.exception.ResourceNotFoundException;
import br.com.orcagov.api.exception.UnauthorizedException;
import br.com.orcagov.api.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Recurso não encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        logger.warn("Recurso não encontrado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Recurso duplicado
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        logger.warn("Recurso duplicado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Regra de negócio violada
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        logger.warn("Regra de negócio violada: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Acesso não autorizado
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        logger.warn("Acesso não autorizado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Erro de validação customizado
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        logger.warn("Erro de validação: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .error("Validation Error")
                .message(ex.getMessage())
                .details(ex.getErrors())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Erro de validação do Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Erro de validação de campos: {}", ex.getMessage());
        
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .error("Validation Error")
                .message("Campos inválidos")
                .details(details)
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Erro de tipo de argumento
     */
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ApiResponseDTO<Void>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex, WebRequest request) {
    
    logger.warn("Erro de tipo de argumento: {}", ex.getMessage());

    // Evita NullPointerException caso getRequiredType() seja null
    String expectedType = ex.getRequiredType() != null
            ? ex.getRequiredType().getSimpleName()
            : "tipo desconhecido";

    String message = String.format(
            "Valor inválido '%s' para o parâmetro '%s'. Esperado: %s",
            ex.getValue(), ex.getName(), expectedType);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseDTO.error(message));
}


    /**
     * Erro de JSON malformado
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        logger.warn("JSON malformado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Formato JSON inválido"));
    }

    /**
     * Erro genérico não tratado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Erro interno do servidor: ", ex);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .error("Internal Server Error")
                .message("Ocorreu um erro interno. Tente novamente mais tarde.")
                .details(List.of(ex.getMessage()))
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Erro de argumento ilegal
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Argumento ilegal: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }
}