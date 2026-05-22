package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.dto.ApprovalRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportResponse;

import com.mysawit.mysawit_panen.model.HarvestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestReportService {
    HarvestReportResponse submitReport(UUID buruhId, HarvestReportRequest request);
    HarvestReportResponse approveReport(UUID mandorId, UUID reportId);
    HarvestReportResponse rejectReport(UUID mandorId, UUID reportId, ApprovalRequest request);
    HarvestReportResponse getReportById(UUID id);
    List<HarvestReportResponse> getHistory(UUID buruhId, LocalDate startDate, LocalDate endDate, HarvestStatus status);
}
