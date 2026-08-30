package com.skillbridge.opportunity.repository;

import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    Page<Opportunity> findByCompanyProfileId(Long companyProfileId, Pageable pageable);

    Page<Opportunity> findByCompanyProfileIdAndStatus(Long companyProfileId, OpportunityStatus status, Pageable pageable);

    Page<Opportunity> findByCompanyProfileIdAndTypeAndStatus(
            Long companyProfileId, OpportunityType type, OpportunityStatus status, Pageable pageable);

    Page<Opportunity> findByCompanyProfileIdAndType(Long companyProfileId, OpportunityType type, Pageable pageable);

    long countByStatus(OpportunityStatus status);

    long countByTypeAndStatus(OpportunityType type, OpportunityStatus status);

    /**
     * Search/filter opportunities for the browse endpoint.
     * Supports filtering by search term (title/description), type, mode, min CGPA, status.
     * departmentId filter: includes opportunities where either no department restriction exists,
     * or the given departmentId is in the required branches.
     */
    @Query("""
            SELECT DISTINCT o FROM Opportunity o
            WHERE (:search IS NULL OR LOWER(o.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                   OR LOWER(o.description) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
              AND (:type IS NULL OR o.type = :type)
              AND (:mode IS NULL OR o.mode = :mode)
              AND (:status IS NULL OR o.status = :status)
              AND (:minCgpa IS NULL OR o.minCgpa IS NULL OR o.minCgpa <= :minCgpa)
              AND (:departmentId IS NULL OR NOT EXISTS (
                    SELECT rb FROM OpportunityRequiredBranch rb WHERE rb.opportunityId = o.id
                   ) OR EXISTS (
                    SELECT rb FROM OpportunityRequiredBranch rb
                    WHERE rb.opportunityId = o.id AND rb.departmentId = :departmentId
                   ))
            ORDER BY o.createdAt DESC
            """)
    Page<Opportunity> searchOpportunities(
            @Param("search") String search,
            @Param("type") com.skillbridge.opportunity.entity.OpportunityType type,
            @Param("mode") com.skillbridge.opportunity.entity.OpportunityMode mode,
            @Param("status") OpportunityStatus status,
            @Param("minCgpa") java.math.BigDecimal minCgpa,
            @Param("departmentId") Long departmentId,
            Pageable pageable);
}
