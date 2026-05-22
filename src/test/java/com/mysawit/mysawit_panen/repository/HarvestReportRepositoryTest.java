package com.mysawit.mysawit_panen.repository;

import com.mysawit.mysawit_panen.model.HarvestReport;
import com.mysawit.mysawit_panen.model.HarvestStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HarvestReportRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<HarvestReport> typedQuery;

    @Mock
    private TypedQuery<Long> countQuery;

    @InjectMocks
    private HarvestReportRepositoryImpl repository;

    private HarvestReport pendingReport;
    private HarvestReport approvedReport;
    private UUID buruhId;
    private UUID reportId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        buruhId = UUID.fromString("ab558e9f-1c39-460e-8860-71af6af63bd6");
        reportId = UUID.fromString("12345678-1c39-460e-8860-71af6af63bd6");
        today = LocalDate.now();

        pendingReport = HarvestReport.builder()
                .id(reportId)
                .weight(150.0)
                .description("Panen Blok A")
                .status(HarvestStatus.PENDING)
                .date(today)
                .buruhId(buruhId)
                .photoUrls(List.of("url1", "url2"))
                .build();

        approvedReport = HarvestReport.builder()
                .id(UUID.randomUUID())
                .weight(200.0)
                .description("Panen Blok B")
                .status(HarvestStatus.APPROVED)
                .date(today.minusDays(1))
                .buruhId(buruhId)
                .mandorId(UUID.randomUUID())
                .build();
    }

    @Test
    void saveCheck() {
        when(entityManager.merge(pendingReport)).thenReturn(pendingReport);

        final HarvestReport result = repository.save(pendingReport);

        assertEquals(pendingReport, result);
        verify(entityManager, times(1)).merge(pendingReport);
    }

    @Test
    void findById_ExistingId() {
        when(entityManager.createQuery(any(String.class), eq(HarvestReport.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", reportId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(pendingReport));

        final HarvestReport result = repository.findById(reportId);

        assertEquals(pendingReport, result);
    }

    @Test
    void findById_UnknownId() {
        when(entityManager.createQuery(any(String.class), eq(HarvestReport.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", reportId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final HarvestReport result = repository.findById(reportId);

        assertNull(result);
    }

    @Test
    void hasReportedToday_ReturnsTrue() {
        when(entityManager.createQuery(any(String.class), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter("buruhId", buruhId)).thenReturn(countQuery);
        when(countQuery.setParameter("date", today)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        final boolean result = repository.hasReportedToday(buruhId, today);

        assertTrue(result);
    }

    @Test
    void hasReportedToday_ReturnsFalse() {
        when(entityManager.createQuery(any(String.class), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter("buruhId", buruhId)).thenReturn(countQuery);
        when(countQuery.setParameter("date", today)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        final boolean result = repository.hasReportedToday(buruhId, today);

        assertFalse(result);
    }

    @Test
    void findAllByBuruhId_ExistingData() {
        when(entityManager.createQuery(any(String.class), eq(HarvestReport.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("buruhId", buruhId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(pendingReport, approvedReport));

        final List<HarvestReport> result = repository.findAllByBuruhId(buruhId);

        assertEquals(2, result.size());
        assertTrue(result.contains(pendingReport));
    }

    @Test
    void findAllByBuruhId_NoData() {
        when(entityManager.createQuery(any(String.class), eq(HarvestReport.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("buruhId", buruhId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final List<HarvestReport> result = repository.findAllByBuruhId(buruhId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findFiltered_ReturnsFilteredData() {
        when(entityManager.createQuery(any(String.class), eq(HarvestReport.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("buruhId", buruhId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", HarvestStatus.PENDING)).thenReturn(typedQuery);
        when(typedQuery.setParameter("startDate", today)).thenReturn(typedQuery);
        when(typedQuery.setParameter("endDate", today)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(pendingReport));

        final List<HarvestReport> result = repository.findFiltered(buruhId, today, today, HarvestStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(HarvestStatus.PENDING, result.getFirst().getStatus());
    }

    @Test
    void findFiltered_MandorGeneralView() {
        when(entityManager.createQuery(any(String.class), eq(HarvestReport.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", HarvestStatus.PENDING)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(pendingReport));

        final List<HarvestReport> result = repository.findFiltered(null, null, null, HarvestStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(HarvestStatus.PENDING, result.getFirst().getStatus());
    }
}