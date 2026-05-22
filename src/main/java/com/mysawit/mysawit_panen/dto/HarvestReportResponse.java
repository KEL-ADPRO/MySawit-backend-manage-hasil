package com.mysawit.mysawit_panen.dto;

import com.mysawit.mysawit_panen.model.HarvestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestReportResponse {
    private UUID id;
    private Double weight;
    private String description;
    private HarvestStatus status;
    private String rejectionReason;
    private LocalDate date;
    private List<String> photoUrls;
    private UUID buruhId;
    private UUID mandorId;
}
