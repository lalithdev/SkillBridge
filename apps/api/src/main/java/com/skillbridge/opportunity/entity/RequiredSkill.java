package com.skillbridge.opportunity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Maps to the `required_skills` table.
 * Each row links an opportunity to a required skill from the master taxonomy.
 */
@Entity
@Table(
    name = "required_skills",
    indexes = {
        @Index(name = "idx_required_skills_opportunity_id", columnList = "opportunity_id"),
        @Index(name = "idx_required_skills_skill_id", columnList = "skill_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_rs_opp_skill", columnNames = {"opportunity_id", "skill_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
