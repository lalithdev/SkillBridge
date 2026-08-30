package com.skillbridge.internship.repository;

import com.skillbridge.internship.entity.CompanyFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyFeedbackRepository extends JpaRepository<CompanyFeedback, Long> {

    Optional<CompanyFeedback> findByInternshipRecordId(Long internshipRecordId);

    boolean existsByInternshipRecordId(Long internshipRecordId);

    @Query("SELECT cf FROM CompanyFeedback cf " +
           "JOIN com.skillbridge.internship.entity.InternshipRecord ir ON cf.internshipRecordId = ir.id " +
           "JOIN com.skillbridge.application.entity.Application a ON ir.applicationId = a.id " +
           "JOIN com.skillbridge.student.entity.StudentProfile sp ON a.studentProfileId = sp.id " +
           "WHERE sp.collegeId = :collegeId " +
           "ORDER BY cf.submittedAt DESC")
    Page<CompanyFeedback> findByCollegeId(@Param("collegeId") Long collegeId, Pageable pageable);
}
