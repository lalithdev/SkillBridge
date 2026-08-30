package com.skillbridge.opportunity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Maps to the `opportunity_required_years` table.
 * Each row stores one eligible year_of_study value for an opportunity.
 * An empty set means all years are eligible.
 */
@Entity
@Table(
    name = "opportunity_required_years",
    indexes = {
        @Index(name = "idx_ory_opportunity_id", columnList = "opportunity_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_ory_opp_year", columnNames = {"opportunity_id", "year_of_study"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityRequiredYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Column(name = "year_of_study", nullable = false)
    private Short yearOfStudy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
