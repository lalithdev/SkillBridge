package com.skillbridge.student.repository;

import com.skillbridge.student.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStudentProfileIdOrderByCreatedAtDesc(Long studentProfileId);

    List<Project> findByStudentProfileId(Long studentProfileId);

    Optional<Project> findByIdAndStudentProfileId(Long id, Long studentProfileId);

    boolean existsByIdAndStudentProfileId(Long id, Long studentProfileId);
}
