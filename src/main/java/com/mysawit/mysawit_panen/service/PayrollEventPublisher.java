package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.model.HarvestReport;

public interface PayrollEventPublisher {
    void publishHarvestApprovedEvent(HarvestReport report);
}
