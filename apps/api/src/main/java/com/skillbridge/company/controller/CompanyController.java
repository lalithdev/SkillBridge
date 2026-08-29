package com.skillbridge.company.controller;

import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.dto.CompanyProfileDto;
import com.skillbridge.company.dto.UpdateCompanyProfileRequest;
import com.skillbridge.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileDto> getCompanyProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CompanyProfileDto profile = companyService.getCompanyProfile(currentUser);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileDto> updateCompanyProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateCompanyProfileRequest request) {
        CompanyProfileDto profile = companyService.updateCompanyProfile(currentUser, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyProfileDto> getCompanyById(@PathVariable Long id) {
        CompanyProfileDto profile = companyService.getCompanyById(id);
        return ResponseEntity.ok(profile);
    }
}
