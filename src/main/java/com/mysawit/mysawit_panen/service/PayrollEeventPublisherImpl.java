package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.model.HarvestReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollEeventPublisherImpl implements PayrollEventPublisher{
    @Override
    public void publishHarvestApprovedEvent(HarvestReport report) {

    }
}
