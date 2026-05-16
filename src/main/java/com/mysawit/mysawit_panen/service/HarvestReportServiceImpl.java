package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.dto.ApprovalRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportResponse;
import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import com.mysawit.mysawit_panen.repository.HarvestReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestReportServiceImpl implements HarvestReportService {
    private final HarvestReportRepository repository;

    @Override
    @Transactional
    public HarvestReportResponse submitReport(final UUID buruhId, final HarvestReportRequest request) {
        final LocalDate today = LocalDate.now();

        if (repository.hasReportedToday(buruhId, today)) {
            throw new MatchException("Existing report found!", new Exception());
        }

        final HarvestReport report = HarvestReport.builder()
                .weight(request.getWeight())
                .description(request.getDescription())
                .photoUrls(request.getPhotoUrls())
                .buruhId(buruhId)
                .date(today)
                .status(HarvestStatus.PENDING)
                .build();

        final HarvestReport savedReport = repository.save(report);
        return mapToResponse(savedReport);
    }

    @Override
    @Transactional
    public HarvestReportResponse approveReport(final UUID mandorId, final UUID reportId) {
        final HarvestReport report = getReportOrThrow(reportId);

        if (report.getStatus() != HarvestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending reports can be approved.");
        }

        report.setStatus(HarvestStatus.APPROVED);
        report.setMandorId(mandorId);

        final HarvestReport savedReport = repository.save(report);

        return mapToResponse(savedReport);
    }

    @Override
    @Transactional
    public HarvestReportResponse rejectReport(final UUID mandorId, final UUID reportId, final ApprovalRequest request) {
        final HarvestReport report = getReportOrThrow(reportId);

        if (report.getStatus() != HarvestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending reports can be rejected!");
        }

        if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required!");
        }

        report.setStatus(HarvestStatus.REJECTED);
        report.setRejectionReason(request.getRejectionReason());
        report.setMandorId(mandorId);

        final HarvestReport savedReport = repository.save(report);
        return mapToResponse(savedReport);
    }

    private HarvestReport getReportOrThrow(final UUID reportId) {
        final HarvestReport report = repository.findById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("Harvest report not found!");
        }
        return report;
    }

    private HarvestReportResponse mapToResponse(final HarvestReport report) {
        return HarvestReportResponse.builder()
                .id(report.getId())
                .weight(report.getWeight())
                .description(report.getDescription())
                .status(report.getStatus())
                .rejectionReason(report.getRejectionReason())
                .date(report.getDate())
                .photoUrls(report.getPhotoUrls())
                .buruhId(report.getBuruhId())
                .mandorId(report.getMandorId())
                .build();
    }
}
