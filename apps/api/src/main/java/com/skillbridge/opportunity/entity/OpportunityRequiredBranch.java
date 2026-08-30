package com.skillbridge.opportunity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Maps to the `opportunity_required_branches` table.
 * Each row links an opportunity to an eligible department.
 * An empty set means all departments are eligible.
 */
@Entity
@Table(
    name = "opportunity_required_branches",
    indexes = {
        @Index(name = "idx_orb_opportunity_id", columnList = "opportunity_id"),
        @Index(name = "idx_orb_department_id", columnList = "department_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_orb_opp_dept", columnNames = {"opportunity_id", "department_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityRequiredBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
