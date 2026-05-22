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
import com.mysawit.mysawit_panen.state.HarvestContext;
import com.mysawit.mysawit_panen.state.HarvestReportState;
import com.mysawit.mysawit_panen.state.StateFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestReportServiceImpl implements HarvestReportService {
    private final HarvestReportRepository repository;
    private final PayrollEventPublisher payrollEventPublisher;

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

        final HarvestReportState state = StateFactory.getState(report.getStatus());
        state.approve(new HarvestContext(report, payrollEventPublisher), mandorId);

        final HarvestReport savedReport = repository.save(report);
        return mapToResponse(savedReport);
    }

    @Override
    @Transactional
    public HarvestReportResponse rejectReport(final UUID mandorId, final UUID reportId, final ApprovalRequest request) {
        final HarvestReport report = getReportOrThrow(reportId);

        final HarvestReportState state = StateFactory.getState(report.getStatus());
        state.reject(new HarvestContext(report, payrollEventPublisher), mandorId, request.getRejectionReason());

        final HarvestReport savedReport = repository.save(report);
        return mapToResponse(savedReport);
    }

    @Override
    public HarvestReportResponse getReportById(final UUID id) {
        return mapToResponse(getReportOrThrow(id));
    }

    @Override
    public List<HarvestReportResponse> getHistory(final UUID buruhId, final LocalDate startDate, final LocalDate endDate, final HarvestStatus status) {
        final List<HarvestReport> reports = repository.findFiltered(buruhId, startDate, endDate, status);
        return reports.stream().map(this::mapToResponse).toList();
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
