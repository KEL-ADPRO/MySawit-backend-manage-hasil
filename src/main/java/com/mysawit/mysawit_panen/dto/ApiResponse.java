package com.mysawit.mysawit_panen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> successResponse(final String message, final T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> errorResponse(final String message) {
        return new ApiResponse<>(false, message, null);
    }
}