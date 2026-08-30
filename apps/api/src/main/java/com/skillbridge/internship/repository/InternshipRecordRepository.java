package com.skillbridge.internship.repository;

import com.skillbridge.internship.entity.InternshipRecord;
import com.skillbridge.internship.entity.InternshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipRecordRepository extends JpaRepository<InternshipRecord, Long> {

    Optional<InternshipRecord> findByApplicationId(Long applicationId);

    boolean existsByApplicationId(Long applicationId);

    @Query("SELECT ir FROM InternshipRecord ir " +
           "JOIN com.skillbridge.application.entity.Application a ON ir.applicationId = a.id " +
           "WHERE a.studentProfileId = :studentProfileId " +
           "ORDER BY ir.createdAt DESC")
    List<InternshipRecord> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT ir FROM InternshipRecord ir " +
           "JOIN com.skillbridge.application.entity.Application a ON ir.applicationId = a.id " +
           "JOIN com.skillbridge.opportunity.entity.Opportunity o ON a.opportunityId = o.id " +
           "WHERE o.companyProfileId = :companyProfileId " +
           "AND (:status IS NULL OR ir.status = :status) " +
           "ORDER BY ir.createdAt DESC")
    Page<InternshipRecord> findByCompanyProfileId(
            @Param("companyProfileId") Long companyProfileId,
            @Param("status") InternshipStatus status,
            Pageable pageable
    );
}
