package com.skillbridge.student.repository;

import com.skillbridge.student.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByStudentProfileIdOrderByCreatedAtDesc(Long studentProfileId);

    List<Certification> findByStudentProfileId(Long studentProfileId);

    Optional<Certification> findByIdAndStudentProfileId(Long id, Long studentProfileId);

    boolean existsByIdAndStudentProfileId(Long id, Long studentProfileId);
}
