package com.skillbridge.opportunity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Maps to the `opportunities` table in the DB schema.
 * Status starts as OPEN (per OpenAPI contract) when created via the API.
 * The DB also allows DRAFT/CLOSED, preserved for DB-level integrity.
 */
@Entity
@Table(
    name = "opportunities",
    indexes = {
        @Index(name = "idx_opportunities_company_profile_id", columnList = "company_profile_id"),
        @Index(name = "idx_opportunities_status", columnList = "status"),
        @Index(name = "idx_opportunities_type", columnList = "type"),
        @Index(name = "idx_opportunities_application_deadline", columnList = "application_deadline")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_profile_id", nullable = false)
    private Long companyProfileId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OpportunityType type;

    @Column(length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OpportunityMode mode = OpportunityMode.ONSITE;

    @Column(name = "duration_weeks")
    private Short durationWeeks;

    @Column(name = "stipend_amount", precision = 12, scale = 2)
    private BigDecimal stipendAmount;

    @Column(name = "stipend_currency", nullable = false, length = 10)
    @Builder.Default
    private String stipendCurrency = "INR";

    @Column(name = "min_cgpa", precision = 4, scale = 2)
    private BigDecimal minCgpa;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OpportunityStatus status = OpportunityStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
