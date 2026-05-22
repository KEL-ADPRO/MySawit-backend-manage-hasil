package com.mysawit.mysawit_panen.controller;

import com.mysawit.mysawit_panen.dto.ApiResponse;
import com.mysawit.mysawit_panen.dto.ApprovalRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportResponse;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import com.mysawit.mysawit_panen.service.HarvestReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HarvestReportControllerTest {

    @Mock
    private HarvestReportService harvestReportService;

    @InjectMocks
    private HarvestReportController harvestReportController;

    private UUID buruhId;
    private UUID mandorId;
    private UUID reportId;
    private String buruhIdStr;
    private String mandorIdStr;
    private String reportIdStr;

    private HarvestReportRequest validSubmitRequest;
    private ApprovalRequest validRejectRequest;

    private HarvestReportResponse pendingResponse;
    private HarvestReportResponse approvedResponse;
    private HarvestReportResponse rejectedResponse;

    @BeforeEach
    void setUp() {
        buruhId = UUID.fromString("ab558e9f-1c39-460e-8860-71af6af63bd6");
        mandorId = UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6");
        reportId = UUID.fromString("12345678-1c39-460e-8860-71af6af63bd6");

        buruhIdStr = buruhId.toString();
        mandorIdStr = mandorId.toString();
        reportIdStr = reportId.toString();

        validSubmitRequest = HarvestReportRequest.builder()
                .weight(100.0)
                .description("Panen Blok A")
                .photoUrls(List.of("url1"))
                .build();

        validRejectRequest = ApprovalRequest.builder()
                .rejectionReason("Kualitas buruk!")
                .build();

        pendingResponse = HarvestReportResponse.builder()
                .id(reportId)
                .weight(100.0)
                .status(HarvestStatus.PENDING)
                .buruhId(buruhId)
                .date(LocalDate.now())
                .build();

        approvedResponse = HarvestReportResponse.builder()
                .id(reportId)
                .status(HarvestStatus.APPROVED)
                .mandorId(mandorId)
                .build();

        rejectedResponse = HarvestReportResponse.builder()
                .id(reportId)
                .status(HarvestStatus.REJECTED)
                .rejectionReason("Kualitas buruk!")
                .mandorId(mandorId)
                .build();
    }

    @Test
    void submitReport_Success() {
        when(harvestReportService.submitReport(buruhId, validSubmitRequest)).thenReturn(pendingResponse);

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.submitReport(buruhIdStr, validSubmitRequest);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Harvest report submitted successfully", result.getBody().getMessage());

        final HarvestReportResponse data = result.getBody().getData();
        assertEquals(reportId, data.getId());
        assertEquals(HarvestStatus.PENDING, data.getStatus());

        verify(harvestReportService, times(1)).submitReport(buruhId, validSubmitRequest);
    }

    @Test
    void submitReport_Fails_WhenDuplicate() {
        when(harvestReportService.submitReport(buruhId, validSubmitRequest)).thenThrow(new MatchException("Existing report found!", new Exception()));

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.submitReport(buruhIdStr, validSubmitRequest);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Existing report found!", result.getBody().getMessage());
    }

    @Test
    void submitReport_Fails_WhenInvalidUUID() {
        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.submitReport("invalid-uuid", validSubmitRequest);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Invalid User ID format", result.getBody().getMessage());

        verify(harvestReportService, never()).submitReport(any(), any());
    }

    @Test
    void approveReport_Success() {
        when(harvestReportService.approveReport(mandorId, reportId)).thenReturn(approvedResponse);

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.approveReport(mandorIdStr, reportIdStr);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Harvest report approved", result.getBody().getMessage());

        final HarvestReportResponse data = result.getBody().getData();
        assertEquals(HarvestStatus.APPROVED, data.getStatus());
        assertEquals(mandorId, data.getMandorId());
    }

    @Test
    void approveReport_Fails_WhenStatusInvalid() {
        when(harvestReportService.approveReport(mandorId, reportId)).thenThrow(new IllegalArgumentException("Only pending reports can be approved!"));

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.approveReport(mandorIdStr, reportIdStr);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Only pending reports can be approved!", result.getBody().getMessage());
    }

    @Test
    void rejectReport_Success() {
        when(harvestReportService.rejectReport(mandorId, reportId, validRejectRequest)).thenReturn(rejectedResponse);

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.rejectReport(mandorIdStr, reportIdStr, validRejectRequest);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Harvest report rejected", result.getBody().getMessage());

        final HarvestReportResponse data = result.getBody().getData();
        assertEquals(HarvestStatus.REJECTED, data.getStatus());
        assertEquals("Kualitas buruk!", data.getRejectionReason());
    }

    @Test
    void rejectReport_Fails_WhenNotFound() {
        when(harvestReportService.rejectReport(mandorId, reportId, validRejectRequest)).thenThrow(new IllegalArgumentException("Harvest report not found!"));

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.rejectReport(mandorIdStr, reportIdStr, validRejectRequest);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Harvest report not found!", result.getBody().getMessage());
    }

    @Test
    void getReportById_Success() {
        when(harvestReportService.getReportById(reportId)).thenReturn(pendingResponse);

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.getReportById(reportIdStr);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Harvest report retrieved successfully", result.getBody().getMessage());
        assertEquals(reportId, result.getBody().getData().getId());
    }

    @Test
    void getReportById_NotFound() {
        when(harvestReportService.getReportById(reportId)).thenThrow(new IllegalArgumentException("Harvest report not found!"));

        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.getReportById(reportIdStr);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Harvest report not found!", result.getBody().getMessage());
    }

    @Test
    void getReportById_InvalidFormat() {
        final ResponseEntity<ApiResponse<HarvestReportResponse>> result = harvestReportController.getReportById("invalid-uuid");

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Invalid Report ID format", result.getBody().getMessage());
    }

    @Test
    void getBuruhHistory_Success() {
        final List<HarvestReportResponse> history = List.of(pendingResponse);
        when(harvestReportService.getHistory(buruhId, null, null, HarvestStatus.PENDING)).thenReturn(history);

        final ResponseEntity<ApiResponse<List<HarvestReportResponse>>> result = harvestReportController.getBuruhHistory(buruhIdStr, null, null, HarvestStatus.PENDING);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Buruh harvest history retrieved successfully", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void getBuruhHistory_InvalidUUID() {
        final ResponseEntity<ApiResponse<List<HarvestReportResponse>>> result = harvestReportController.getBuruhHistory("invalid-uuid", null, null, HarvestStatus.PENDING);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Invalid User ID format", result.getBody().getMessage());
    }

    @Test
    void getMandorHistory_Success() {
        final List<HarvestReportResponse> history = List.of(pendingResponse);
        when(harvestReportService.getHistory(buruhId, null, null, HarvestStatus.PENDING)).thenReturn(history);

        final ResponseEntity<ApiResponse<List<HarvestReportResponse>>> result = harvestReportController.getMandorHistory(mandorIdStr, buruhIdStr, null, null, HarvestStatus.PENDING);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Mandor harvest history retrieved successfully", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void getMandorHistory_InvalidMandorUUID() {
        final ResponseEntity<ApiResponse<List<HarvestReportResponse>>> result = harvestReportController.getMandorHistory("invalid-uuid", buruhIdStr, null, null, HarvestStatus.PENDING);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Invalid Mandor ID format", result.getBody().getMessage());
    }

    @Test
    void getMandorHistory_InvalidBuruhUUID() {
        final ResponseEntity<ApiResponse<List<HarvestReportResponse>>> result = harvestReportController.getMandorHistory(mandorIdStr, "invalid-uuid", null, null, HarvestStatus.PENDING);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Invalid Buruh ID format", result.getBody().getMessage());
    }
}