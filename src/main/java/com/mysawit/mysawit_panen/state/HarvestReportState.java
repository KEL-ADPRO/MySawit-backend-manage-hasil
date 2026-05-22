package com.mysawit.mysawit_panen.state;

import java.util.UUID;

public interface HarvestReportState {
    void approve(HarvestContext context, UUID mandorId);
    void reject(HarvestContext context, UUID mandorId, String reason);
}
