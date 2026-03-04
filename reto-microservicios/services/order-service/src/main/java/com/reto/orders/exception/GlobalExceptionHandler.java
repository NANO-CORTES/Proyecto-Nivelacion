package com.reto.orders.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());

        HttpStatus status = HttpStatus.BAD_REQUEST; // Default to 400

        // If it's a "not found" error, return 404
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no encontrada")) {
            status = HttpStatus.NOT_FOUND;
        }

        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }
}
