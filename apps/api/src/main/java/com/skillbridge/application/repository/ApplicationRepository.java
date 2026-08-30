package com.skillbridge.application.repository;

import com.skillbridge.application.entity.Application;
import com.skillbridge.application.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByStudentProfileIdAndOpportunityId(Long studentProfileId, Long opportunityId);

    Optional<Application> findByStudentProfileIdAndOpportunityId(Long studentProfileId, Long opportunityId);

    Page<Application> findByStudentProfileId(Long studentProfileId, Pageable pageable);

    Page<Application> findByStudentProfileIdAndStatus(Long studentProfileId, ApplicationStatus status, Pageable pageable);

    Page<Application> findByOpportunityId(Long opportunityId, Pageable pageable);

    Page<Application> findByOpportunityIdAndStatus(Long opportunityId, ApplicationStatus status, Pageable pageable);

    List<Application> findByOpportunityId(Long opportunityId);

    List<Application> findByStudentProfileIdIn(List<Long> studentProfileIds);

    @Query("SELECT a FROM Application a WHERE a.opportunityId = :opportunityId " +
           "AND (:status IS NULL OR a.status = :status) " +
           "ORDER BY a.matchPercentAtApply DESC, a.appliedAt ASC")
    Page<Application> findOpportunityApplicants(
            @Param("opportunityId") Long opportunityId,
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );

    long countByOpportunityId(Long opportunityId);

    long countByStudentProfileId(Long studentProfileId);

    long countByOpportunityIdAndStatus(Long opportunityId, ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a JOIN com.skillbridge.student.entity.StudentProfile sp ON a.studentProfileId = sp.id " +
           "WHERE sp.collegeId = :collegeId AND (:departmentId IS NULL OR sp.departmentId = :departmentId) AND a.status = :status")
    long countByCollegeIdAndDepartmentIdAndStatus(
            @Param("collegeId") Long collegeId,
            @Param("departmentId") Long departmentId,
            @Param("status") ApplicationStatus status
    );

    @Query("SELECT COUNT(a) FROM Application a JOIN com.skillbridge.student.entity.StudentProfile sp ON a.studentProfileId = sp.id " +
           "WHERE sp.collegeId = :collegeId AND (:departmentId IS NULL OR sp.departmentId = :departmentId)")
    long countTotalByCollegeIdAndDepartmentId(
            @Param("collegeId") Long collegeId,
            @Param("departmentId") Long departmentId
    );
}
