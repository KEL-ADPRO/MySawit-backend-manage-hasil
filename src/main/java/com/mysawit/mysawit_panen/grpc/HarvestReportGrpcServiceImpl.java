package com.mysawit.mysawit_panen.grpc;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.repository.HarvestReportRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@GrpcService
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HarvestReportGrpcServiceImpl extends HarvestReportGrpcServiceGrpc.HarvestReportGrpcServiceImplBase {
    private final HarvestReportRepository repository;

    @Override
    public void getReportStatus(ReportStatusRequest request, StreamObserver<ReportStatusResponse> responseObserver) {
        try {
            String reportIdStr = request.getReportId();
            if (reportIdStr == null || reportIdStr.trim().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Report ID is required.")
                        .asRuntimeException());
                return;
            }

            UUID reportId;
            try {
                reportId = UUID.fromString(reportIdStr);
            } catch (IllegalArgumentException e) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Invalid Report ID format.")
                        .asRuntimeException());
                return;
            }

            HarvestReport report = repository.findById(reportId);
            if (report == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Harvest report not found.")
                        .asRuntimeException());
                return;
            }

            ReportStatusResponse response = ReportStatusResponse.newBuilder()
                    .setId(report.getId().toString())
                    .setStatus(report.getStatus().name())
                    .setWeight(report.getWeight())
                    .setBuruhId(report.getBuruhId().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in getReportStatus: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error while fetching report status.")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
