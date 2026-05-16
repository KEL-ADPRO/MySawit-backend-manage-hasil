package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.dto.ApprovalRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestReportServiceImpl implements HarvestReportService {
    @Override
    public HarvestReportResponse submitReport(UUID buruhId, HarvestReportRequest request) {
        return null;
    }

    @Override
    public HarvestReportResponse approveReport(UUID mandorId, UUID reportId) {
        return null;
    }

    @Override
    public HarvestReportResponse rejectReport(UUID mandorId, UUID reportId, ApprovalRequest request) {
        return null;
    }
}
