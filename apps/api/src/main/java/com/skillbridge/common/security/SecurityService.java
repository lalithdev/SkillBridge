package com.skillbridge.common.security;

import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final InternshipRecordRepository internshipRecordRepository;
    private final StudentProfileRepository studentProfileRepository;

    public SecurityService(
            @Lazy OpportunityRepository opportunityRepository,
            @Lazy ApplicationRepository applicationRepository,
            @Lazy InternshipRecordRepository internshipRecordRepository,
            @Lazy StudentProfileRepository studentProfileRepository) {
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.internshipRecordRepository = internshipRecordRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    public CustomUserDetails getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public boolean isCurrentUser(Long userId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        return user != null && user.getUserId() != null && user.getUserId().equals(userId);
    }

    public boolean isProfileOwner(Long studentProfileId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        return user != null && user.getStudentProfileId() != null && user.getStudentProfileId().equals(studentProfileId);
    }

    public boolean isCompanyOwner(Long companyProfileId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        return user != null && user.getCompanyProfileId() != null && user.getCompanyProfileId().equals(companyProfileId);
    }

    public boolean isCollegeOwner(Long collegeId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        return user != null && user.getCollegeId() != null && user.getCollegeId().equals(collegeId);
    }

    public boolean isOpportunityOwner(Long opportunityId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        if (user == null || user.getCompanyProfileId() == null) {
            return false;
        }
        return opportunityRepository.findById(opportunityId)
                .map(opp -> opp.getCompanyProfileId().equals(user.getCompanyProfileId()))
                .orElse(false);
    }

    public boolean isApplicationOwner(Long applicationId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        if (user == null || user.getStudentProfileId() == null) {
            return false;
        }
        return applicationRepository.findById(applicationId)
                .map(app -> app.getStudentProfileId().equals(user.getStudentProfileId()))
                .orElse(false);
    }

    public boolean isApplicationReviewerCompany(Long applicationId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        if (user == null || user.getCompanyProfileId() == null) {
            return false;
        }
        return applicationRepository.findById(applicationId)
                .flatMap(app -> opportunityRepository.findById(app.getOpportunityId()))
                .map(opp -> opp.getCompanyProfileId().equals(user.getCompanyProfileId()))
                .orElse(false);
    }

    public boolean isApplicationAffiliatedCollege(Long applicationId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        if (user == null || user.getCollegeId() == null) {
            return false;
        }
        return applicationRepository.findById(applicationId)
                .flatMap(app -> studentProfileRepository.findById(app.getStudentProfileId()))
                .map(sp -> sp.getCollegeId().equals(user.getCollegeId()))
                .orElse(false);
    }

    public boolean isInternshipParticipant(Long internshipId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        if (user == null || user.getStudentProfileId() == null) {
            return false;
        }
        return internshipRecordRepository.findById(internshipId)
                .flatMap(ir -> applicationRepository.findById(ir.getApplicationId()))
                .map(app -> app.getStudentProfileId().equals(user.getStudentProfileId()))
                .orElse(false);
    }

    public boolean isInternshipEmployerCompany(Long internshipId, Authentication authentication) {
        CustomUserDetails user = getCurrentUser(authentication);
        if (user == null || user.getCompanyProfileId() == null) {
            return false;
        }
        return internshipRecordRepository.findById(internshipId)
                .flatMap(ir -> applicationRepository.findById(ir.getApplicationId()))
                .flatMap(app -> opportunityRepository.findById(app.getOpportunityId()))
                .map(opp -> opp.getCompanyProfileId().equals(user.getCompanyProfileId()))
                .orElse(false);
    }
}
