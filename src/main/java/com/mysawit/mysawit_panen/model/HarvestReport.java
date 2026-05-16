package com.mysawit.mysawit_panen.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "harvest_reports")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HarvestReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double weight;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HarvestStatus status;

    @Column()
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDate date;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "harvest_report_photos", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "photo_url", nullable = false)
    private List<String> photoUrls;

    @Column(nullable = false)
    private UUID buruhId;

    @Column()
    private UUID mandorId;
}