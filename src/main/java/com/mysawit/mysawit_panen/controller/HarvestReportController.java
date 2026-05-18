package com.mysawit.mysawit_panen.controller;

import com.mysawit.mysawit_panen.dto.ApiResponse;
import com.mysawit.mysawit_panen.dto.ApprovalRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportRequest;
import com.mysawit.mysawit_panen.dto.HarvestReportResponse;
import com.mysawit.mysawit_panen.service.HarvestReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/report")
public class HarvestReportController {
    private final HarvestReportService harvestReportService;

    public HarvestReportController(HarvestReportService harvestReportService) {
        this.harvestReportService = harvestReportService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HarvestReportResponse>> submitReport(@RequestHeader("X-User-Id") String buruhIdStr, @RequestBody HarvestReportRequest request) {
        try {
            UUID buruhId = UUID.fromString(buruhIdStr);
            HarvestReportResponse response = harvestReportService.submitReport(buruhId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.successResponse("Harvest report submitted successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.errorResponse("Invalid User ID format"));
        } catch (MatchException e) {
            return ResponseEntity.badRequest().body(ApiResponse.errorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<HarvestReportResponse>> approveReport(@RequestHeader("X-User-Id") String mandorIdStr, @PathVariable("id") String reportIdStr) {
        try {
            UUID mandorId = UUID.fromString(mandorIdStr);
            UUID reportId = UUID.fromString(reportIdStr);
            HarvestReportResponse response = harvestReportService.approveReport(mandorId, reportId);
            return ResponseEntity.ok(ApiResponse.successResponse("Harvest report approved", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.errorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<HarvestReportResponse>> rejectReport(@RequestHeader("X-User-Id") String mandorIdStr, @PathVariable("id") String reportIdStr, @RequestBody ApprovalRequest request) {
        try {
            UUID mandorId = UUID.fromString(mandorIdStr);
            UUID reportId = UUID.fromString(reportIdStr);
            HarvestReportResponse response = harvestReportService.rejectReport(mandorId, reportId, request);
            return ResponseEntity.ok(ApiResponse.successResponse("Harvest report rejected", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.errorResponse(e.getMessage()));
        }
    }
}