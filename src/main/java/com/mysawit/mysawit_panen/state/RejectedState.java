package com.mysawit.mysawit_panen.state;

import java.util.UUID;

public class RejectedState implements HarvestReportState {
    @Override
    public void approve(HarvestContext context, UUID mandorId) {
        throw new IllegalArgumentException("Only pending reports can be approved.");
    }

    @Override
    public void reject(HarvestContext context, UUID mandorId, String reason) {
        throw new IllegalArgumentException("Only pending reports can be rejected!");
    }
}
