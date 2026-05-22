package com.mysawit.mysawit_panen.exception;

import com.mysawit.mysawit_panen.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(final IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ApiResponse.errorResponse(e.getMessage()));
    }

    @ExceptionHandler(MatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMatchException(final MatchException e) {
        return ResponseEntity.badRequest().body(ApiResponse.errorResponse(e.getMessage()));
    }
}
