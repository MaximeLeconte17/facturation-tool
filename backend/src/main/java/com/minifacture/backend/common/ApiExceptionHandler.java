package com.minifacture.backend.common;

import com.minifacture.backend.invoice.InvalidInvoiceStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidInvoiceStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidState(InvalidInvoiceStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}