package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.client.PayrollClient;
import com.mysawit.mysawit_panen.model.HarvestReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollEventPublisherImpl implements PayrollEventPublisher{
    private final PayrollClient pembayaranClient;

    @Override
    public void publishHarvestApprovedEvent(HarvestReport report) {
        log.info("Publishing harvest approved event for buruh={}, weight={}kg", report.getBuruhId(), report.getWeight());
        pembayaranClient.createPayroll(report.getBuruhId(), report.getWeight());
    }
}
