package com.mysawit.mysawit_panen.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestReportRequest {
    @NotNull(message = "Weight is mandatory")
    @Min(value = 0, message = "Weight must be positive")
    private Double weightInKg;

    private String description;

    @NotEmpty(message = "Minimum 1 photo URL")
    private List<String> photoUrls;
}
