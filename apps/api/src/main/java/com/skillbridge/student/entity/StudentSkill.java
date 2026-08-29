package com.skillbridge.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "student_skills",
    indexes = {
        @Index(name = "idx_student_skills_student_profile_id", columnList = "student_profile_id"),
        @Index(name = "idx_student_skills_skill_id", columnList = "skill_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_ss_student_skill", columnNames = {"student_profile_id", "skill_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_profile_id", nullable = false)
    private Long studentProfileId;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
