package com.mysawit.mysawit_panen.state;

import com.mysawit.mysawit_panen.model.HarvestStatus;

public class StateFactory {
    public static HarvestReportState getState(HarvestStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return switch (status) {
            case PENDING -> new PendingState();
            case APPROVED -> new ApprovedState();
            case REJECTED -> new RejectedState();
        };
    }
}
