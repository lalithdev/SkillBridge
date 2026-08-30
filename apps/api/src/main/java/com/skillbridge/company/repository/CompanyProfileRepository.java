package com.skillbridge.company.repository;

import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.company.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    Optional<CompanyProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<CompanyProfile> findByVerificationStatus(VerificationStatus status);
}
