package com.skillbridge.application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Maps to the `applications` table in the PostgreSQL database.
 */
@Entity
@Table(
    name = "applications",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_app_student_opportunity", columnNames = {"student_profile_id", "opportunity_id"})
    },
    indexes = {
        @Index(name = "idx_applications_student_profile_id", columnList = "student_profile_id"),
        @Index(name = "idx_applications_opportunity_id", columnList = "opportunity_id"),
        @Index(name = "idx_applications_opp_match_desc", columnList = "opportunity_id, match_percent_at_apply DESC"),
        @Index(name = "idx_applications_status", columnList = "status")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_profile_id", nullable = false)
    private Long studentProfileId;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "match_percent_at_apply", nullable = false, precision = 5, scale = 2)
    private BigDecimal matchPercentAtApply;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
