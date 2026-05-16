package com.mysawit.mysawit_panen.repository;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HarvestReportRepositoryImpl implements HarvestReportRepository {
    @Override
    public HarvestReport save(HarvestReport report) {
        return null;
    }

    @Override
    public HarvestReport findById(UUID id) {
        return null;
    }

    @Override
    public boolean hasReportedToday(UUID buruhId, LocalDate date) {
        return false;
    }

    @Override
    public List<HarvestReport> findAllByBuruhId(UUID buruhId) {
        return List.of();
    }

    @Override
    public List<HarvestReport> findFiltered(UUID buruhId, LocalDate startDate, LocalDate endDate, HarvestStatus status) {
        return List.of();
    }
}