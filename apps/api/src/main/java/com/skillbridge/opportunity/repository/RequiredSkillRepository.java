package com.skillbridge.opportunity.repository;

import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.entity.RequiredSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequiredSkillRepository extends JpaRepository<RequiredSkill, Long> {

    List<RequiredSkill> findByOpportunityId(Long opportunityId);

    void deleteByOpportunityId(Long opportunityId);

    @Query("SELECT COUNT(DISTINCT rs.opportunityId) FROM RequiredSkill rs " +
           "JOIN com.skillbridge.opportunity.entity.Opportunity o ON rs.opportunityId = o.id " +
           "WHERE rs.skillId = :skillId " +
           "AND o.status = com.skillbridge.opportunity.entity.OpportunityStatus.OPEN " +
           "AND (:type IS NULL OR o.type = :type)")
    long countOpenOpportunitiesRequiringSkill(
            @Param("skillId") Long skillId,
            @Param("type") OpportunityType type
    );
}
