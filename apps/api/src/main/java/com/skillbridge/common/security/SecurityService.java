package com.skillbridge.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

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
}
