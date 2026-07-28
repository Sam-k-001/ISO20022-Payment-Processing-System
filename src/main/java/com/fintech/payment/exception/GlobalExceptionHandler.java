// GlobalExceptionHandler.java
package com.fintech.payment.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global Exception Handler — Centralized error handling for all REST endpoints.
 *
 * <p>Transforms exceptions into consistent, structured JSON error responses
 * following RFC 7807 Problem Details for HTTP APIs.</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(
        PaymentNotFoundException ex,
        HttpServletRequest request
    ) {
        log.warn("Payment not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(
        PaymentProcessingException ex,
        HttpServletRequest request
    ) {
        log.error("Payment processing error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(),
                request.getRequestURI(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> new ErrorResponse.FieldError(
                err.getField(),
                err.getDefaultMessage(),
                err.getRejectedValue()))
            .toList();

        log.warn("Validation failed with {} errors", fieldErrors.size());
        ErrorResponse error = buildError(
            HttpStatus.BAD_REQUEST,
            "Request validation failed",
            request.getRequestURI(),
            fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        log.error("Unhandled exception on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred. Please contact support.",
                request.getRequestURI(), null));
    }

    private ErrorResponse buildError(
        HttpStatus status,
        String message,
        String path,
        List<ErrorResponse.FieldError> fieldErrors
    ) {
        return ErrorResponse.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(path)
            .timestamp(LocalDateTime.now())
            .fieldErrors(fieldErrors)
            .build();
    }
}
