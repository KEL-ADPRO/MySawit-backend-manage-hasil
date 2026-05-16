package com.mysawit.mysawit_panen.repository;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@NoArgsConstructor
public class HarvestReportRepositoryImpl implements HarvestReportRepository {
    private static final String SELECT_REPORT = "SELECT h FROM HarvestReport h ";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public HarvestReport save(final HarvestReport report) {
        return entityManager.merge(report);
    }

    @Override
    public HarvestReport findById(final UUID id) {
        List<HarvestReport> results = entityManager.createQuery(
                        SELECT_REPORT +
                                "WHERE h.id = :id"
                        , HarvestReport.class)
                .setParameter("id", id)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public boolean hasReportedToday(final UUID buruhId, final LocalDate date) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(h) FROM HarvestReport h " +
                                "WHERE h.buruhId = :buruhId AND h.date = :date"
                        , Long.class)
                .setParameter("buruhId", buruhId)
                .setParameter("date", date)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<HarvestReport> findAllByBuruhId(final UUID buruhId) {
        return entityManager.createQuery(
                        SELECT_REPORT +
                                "WHERE h.buruhId = :buruhId"
                        , HarvestReport.class)
                .setParameter("buruhId", buruhId)
                .getResultList();
    }

    @Override
    public List<HarvestReport> findFiltered(final UUID buruhId, final LocalDate startDate, final LocalDate endDate, final HarvestStatus status) {
        StringBuilder queryBuilder = new StringBuilder(SELECT_REPORT + "WHERE h.buruhId = :buruhId");

        if (startDate != null && endDate != null) {
            queryBuilder.append(" AND h.harvestDate BETWEEN :startDate AND :endDate");
        }
        if (status != null) {
            queryBuilder.append(" AND h.status = :status");
        }

        TypedQuery<HarvestReport> query = entityManager.createQuery(queryBuilder.toString(), HarvestReport.class)
                .setParameter("buruhId", buruhId);

        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getResultList();
    }
}