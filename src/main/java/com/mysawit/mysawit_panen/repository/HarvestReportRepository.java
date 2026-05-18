package com.mysawit.mysawit_panen.repository;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestReportRepository {
    HarvestReport save(HarvestReport report);
    HarvestReport findById(UUID id);
    boolean hasReportedToday(UUID buruhId, LocalDate date);
    List<HarvestReport> findAllByBuruhId(UUID buruhId);
    List<HarvestReport> findFiltered(UUID buruhId, LocalDate startDate, LocalDate endDate, HarvestStatus status);
}