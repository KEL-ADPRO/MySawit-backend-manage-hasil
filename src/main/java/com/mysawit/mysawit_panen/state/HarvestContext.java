package com.mysawit.mysawit_panen.state;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.service.PayrollEventPublisher;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HarvestContext {
    private final HarvestReport report;
    private final PayrollEventPublisher payrollEventPublisher;
}
