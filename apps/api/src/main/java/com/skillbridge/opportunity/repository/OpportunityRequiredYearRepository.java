package com.skillbridge.opportunity.repository;

import com.skillbridge.opportunity.entity.OpportunityRequiredYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunityRequiredYearRepository extends JpaRepository<OpportunityRequiredYear, Long> {

    List<OpportunityRequiredYear> findByOpportunityId(Long opportunityId);

    void deleteByOpportunityId(Long opportunityId);
}
