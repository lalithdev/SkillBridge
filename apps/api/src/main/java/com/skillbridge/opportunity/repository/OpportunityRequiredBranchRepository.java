package com.skillbridge.opportunity.repository;

import com.skillbridge.opportunity.entity.OpportunityRequiredBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunityRequiredBranchRepository extends JpaRepository<OpportunityRequiredBranch, Long> {

    List<OpportunityRequiredBranch> findByOpportunityId(Long opportunityId);

    void deleteByOpportunityId(Long opportunityId);
}
