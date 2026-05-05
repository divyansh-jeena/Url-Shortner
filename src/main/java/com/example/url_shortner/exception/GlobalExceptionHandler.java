package com.example.url_shortner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if ("URL not found".equals(message)) {
            status = HttpStatus.NOT_FOUND;
        } else if ("Too many requests".equals(message)) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if ("Link expired".equals(message)) {
            status = HttpStatus.GONE;
        } else if ("Invalid URL".equals(message)) {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(Map.of("error", message != null ? message : "An error occurred"));
    }
}
