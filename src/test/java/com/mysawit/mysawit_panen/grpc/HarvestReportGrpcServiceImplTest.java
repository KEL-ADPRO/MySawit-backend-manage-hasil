package com.mysawit.mysawit_panen.grpc;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import com.mysawit.mysawit_panen.repository.HarvestReportRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HarvestReportGrpcServiceImplTest {

    @Mock
    private HarvestReportRepository repository;

    @InjectMocks
    private HarvestReportGrpcServiceImpl service;

    private UUID reportId;
    private UUID buruhId;
    private HarvestReport report;

    @BeforeEach
    void setUp() {
        reportId = UUID.fromString("aa558a9a-1a39-460a-8860-71aa6aa63aa6");
        buruhId = UUID.fromString("bb558b9b-1b39-460b-8860-71bb6bb63bb6");
        report = HarvestReport.builder()
                .id(reportId)
                .buruhId(buruhId)
                .status(HarvestStatus.PENDING)
                .weight(120.5)
                .build();
    }

    private static final class RecordingObserver<T> implements StreamObserver<T> {
        private T value;
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(T value) {
            this.value = value;
        }

        @Override
        public void onError(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }

    @Test
    void getReportStatus_Success() {
        when(repository.findById(reportId)).thenReturn(report);

        RecordingObserver<ReportStatusResponse> observer = new RecordingObserver<>();
        service.getReportStatus(
                ReportStatusRequest.newBuilder().setReportId(reportId.toString()).build(),
                observer
        );

        assertTrue(observer.completed);
        assertNull(observer.error);
        assertNotNull(observer.value);
        assertEquals(reportId.toString(), observer.value.getId());
        assertEquals("PENDING", observer.value.getStatus());
        assertEquals(120.5, observer.value.getWeight(), 0.01);
        assertEquals(buruhId.toString(), observer.value.getBuruhId());
    }

    @Test
    void getReportStatus_NotFound() {
        when(repository.findById(reportId)).thenReturn(null);

        RecordingObserver<ReportStatusResponse> observer = new RecordingObserver<>();
        service.getReportStatus(
                ReportStatusRequest.newBuilder().setReportId(reportId.toString()).build(),
                observer
        );

        assertFalse(observer.completed);
        assertNotNull(observer.error);
        assertEquals(Status.NOT_FOUND.getCode(), Status.fromThrowable(observer.error).getCode());
        assertEquals("Harvest report not found.", Status.fromThrowable(observer.error).getDescription());
    }

    @Test
    void getReportStatus_InvalidUUID() {
        RecordingObserver<ReportStatusResponse> observer = new RecordingObserver<>();
        service.getReportStatus(
                ReportStatusRequest.newBuilder().setReportId("not-a-uuid").build(),
                observer
        );

        assertFalse(observer.completed);
        assertNotNull(observer.error);
        assertEquals(Status.INVALID_ARGUMENT.getCode(), Status.fromThrowable(observer.error).getCode());
        assertEquals("Invalid Report ID format.", Status.fromThrowable(observer.error).getDescription());
    }

    @Test
    void getReportStatus_MissingReportId() {
        RecordingObserver<ReportStatusResponse> observer = new RecordingObserver<>();
        service.getReportStatus(
                ReportStatusRequest.newBuilder().setReportId("").build(),
                observer
        );

        assertFalse(observer.completed);
        assertNotNull(observer.error);
        assertEquals(Status.INVALID_ARGUMENT.getCode(), Status.fromThrowable(observer.error).getCode());
        assertEquals("Report ID is required.", Status.fromThrowable(observer.error).getDescription());
    }

    @Test
    void getReportStatus_InternalError() {
        when(repository.findById(reportId)).thenThrow(new RuntimeException("Database down"));

        RecordingObserver<ReportStatusResponse> observer = new RecordingObserver<>();
        service.getReportStatus(
                ReportStatusRequest.newBuilder().setReportId(reportId.toString()).build(),
                observer
        );

        assertFalse(observer.completed);
        assertNotNull(observer.error);
        assertEquals(Status.INTERNAL.getCode(), Status.fromThrowable(observer.error).getCode());
        assertEquals("Internal error while fetching report status.", Status.fromThrowable(observer.error).getDescription());
    }
}
