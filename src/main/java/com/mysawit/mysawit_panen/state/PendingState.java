package com.mysawit.mysawit_panen.state;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;

import java.util.UUID;

public class PendingState implements HarvestReportState {
    @Override
    public void approve(HarvestContext context, UUID mandorId) {
        HarvestReport report = context.getReport();
        report.setStatus(HarvestStatus.APPROVED);
        report.setMandorId(mandorId);
        context.getPayrollEventPublisher().publishHarvestApprovedEvent(report);
    }

    @Override
    public void reject(HarvestContext context, UUID mandorId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required!");
        }
        HarvestReport report = context.getReport();
        report.setStatus(HarvestStatus.REJECTED);
        report.setMandorId(mandorId);
        report.setRejectionReason(reason);
    }
}
