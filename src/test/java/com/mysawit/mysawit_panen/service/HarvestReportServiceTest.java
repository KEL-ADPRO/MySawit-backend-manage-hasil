package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.dto.ApprovalRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportResponse;
import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import com.mysawit.mysawit_panen.repository.HarvestReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HarvestReportServiceTest {
    @Mock
    private HarvestReportRepository repository;

    @Mock
    private PayrollEventPublisher payrollEventPublisher;

    @InjectMocks
    private HarvestReportServiceImpl service;

    private UUID buruhId;
    private UUID mandorId;
    private UUID reportId;
    private HarvestReportRequest validRequest;
    private HarvestReport pendingReport;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        buruhId = UUID.randomUUID();
        mandorId = UUID.randomUUID();
        reportId = UUID.randomUUID();
        today = LocalDate.now();

        validRequest = HarvestReportRequest.builder()
                .weight(100.0)
                .description("Panen Blok A")
                .photoUrls(List.of("url1"))
                .build();

        pendingReport = HarvestReport.builder()
                .id(reportId)
                .weight(100.0)
                .status(HarvestStatus.PENDING)
                .buruhId(buruhId)
                .build();
    }

    @Test
    void submitReport_Success() {
        when(repository.hasReportedToday(eq(buruhId), any(LocalDate.class))).thenReturn(false);
        when(repository.save(any(HarvestReport.class))).thenAnswer(invocation -> {
            HarvestReport report = invocation.getArgument(0);
            report.setId(reportId);
            return report;
        });

        final HarvestReportResponse response = service.submitReport(buruhId, validRequest);

        assertNotNull(response);
        assertEquals(100.0, response.getWeight());
        assertEquals(HarvestStatus.PENDING, response.getStatus());
        verify(repository, times(1)).save(any(HarvestReport.class));
    }

    @Test
    void submitReport_Fails_WhenAlreadyReportedToday() {
        when(repository.hasReportedToday(eq(buruhId), any(LocalDate.class))).thenReturn(true);

        assertThrows(MatchException.class, () -> service.submitReport(buruhId, validRequest));

        verify(repository, never()).save(any(HarvestReport.class));
    }

    @Test
    void approveReport_Success_AndTriggersEvent() {
        when(repository.findById(reportId)).thenReturn(pendingReport);
        when(repository.save(any(HarvestReport.class))).thenReturn(pendingReport);

        final HarvestReportResponse response = service.approveReport(mandorId, reportId);

        assertEquals(HarvestStatus.APPROVED, response.getStatus());
        assertEquals(mandorId, response.getMandorId());
        verify(payrollEventPublisher).publishHarvestApprovedEvent(pendingReport);
    }

    @Test
    void rejectReport_Success_WhenReasonProvided() {
        final ApprovalRequest request = new ApprovalRequest("Kualitas buruk!");

        when(repository.findById(reportId)).thenReturn(pendingReport);
        when(repository.save(any(HarvestReport.class))).thenReturn(pendingReport);

        final HarvestReportResponse response = service.rejectReport(mandorId, reportId, request);

        assertEquals(HarvestStatus.REJECTED, response.getStatus());
        assertEquals("Kualitas buruk!", response.getRejectionReason());
    }

    @Test
    void rejectReport_Fails_WhenReasonMissing() {
        final ApprovalRequest request = new ApprovalRequest("");
        when(repository.findById(reportId)).thenReturn(pendingReport);

        final Exception e = assertThrows(IllegalArgumentException.class, () -> service.rejectReport(mandorId, reportId, request));

        assertEquals("Rejection reason is required!", e.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void approveReport_Fails_WhenStatusNotPending() {
        pendingReport.setStatus(HarvestStatus.APPROVED);
        when(repository.findById(reportId)).thenReturn(pendingReport);

        assertThrows(IllegalArgumentException.class, () -> service.approveReport(mandorId, reportId));
    }

    @Test
    void approveReport_Fails_WhenStatusIsRejected() {
        pendingReport.setStatus(HarvestStatus.REJECTED);
        when(repository.findById(reportId)).thenReturn(pendingReport);

        final Exception e = assertThrows(IllegalArgumentException.class, () -> service.approveReport(mandorId, reportId));
        assertEquals("Only pending reports can be approved.", e.getMessage());
    }

    @Test
    void rejectReport_Fails_WhenStatusIsApproved() {
        final ApprovalRequest request = new ApprovalRequest("Salah reject!");
        pendingReport.setStatus(HarvestStatus.APPROVED);
        when(repository.findById(reportId)).thenReturn(pendingReport);

        final Exception e = assertThrows(IllegalArgumentException.class, () -> service.rejectReport(mandorId, reportId, request));
        assertEquals("Only pending reports can be rejected!", e.getMessage());
    }

    @Test
    void rejectReport_Fails_WhenStatusIsRejected() {
        final ApprovalRequest request = new ApprovalRequest("Salah reject!");
        pendingReport.setStatus(HarvestStatus.REJECTED);
        when(repository.findById(reportId)).thenReturn(pendingReport);

        final Exception e = assertThrows(IllegalArgumentException.class, () -> service.rejectReport(mandorId, reportId, request));
        assertEquals("Only pending reports can be rejected!", e.getMessage());
    }

    @Test
    void getReportById_Success() {
        when(repository.findById(reportId)).thenReturn(pendingReport);

        final HarvestReportResponse response = service.getReportById(reportId);

        assertNotNull(response);
        assertEquals(reportId, response.getId());
    }

    @Test
    void getReportById_NotFound() {
        when(repository.findById(reportId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.getReportById(reportId));
    }

    @Test
    void getHistory_Success() {
        when(repository.findFiltered(buruhId, today, today, HarvestStatus.PENDING)).thenReturn(List.of(pendingReport));

        final List<HarvestReportResponse> result = service.getHistory(buruhId, today, today, HarvestStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(reportId, result.getFirst().getId());
    }
}
