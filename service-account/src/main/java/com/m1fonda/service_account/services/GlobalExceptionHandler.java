package com.m1fonda.service_account.services;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.m1fonda.commons_libs.dto.ErrorResponse;
import com.m1fonda.service_account.dto.AccountNotFoundException;
import com.m1fonda.service_account.dto.UserNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex,
            WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);
        return createErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);
        return createErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception ex, WebRequest request, HttpStatus status,
            String errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .path(request.getDescription(false).replace("uri=", ""))
                .errorCode(errorCode)
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
