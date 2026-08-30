package com.skillbridge.application.controller;

import com.skillbridge.application.dto.*;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.application.service.ApplicationService;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * POST /api/v1/applications
     * Submit an application to an open opportunity. STUDENT role only.
     */
    @PostMapping("/api/v1/applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationDto> submitApplication(
            @Valid @RequestBody SubmitApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ApplicationDto created = applicationService.submitApplication(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/v1/applications/my
     * List all applications submitted by the authenticated student.
     */
    @GetMapping("/api/v1/applications/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<StudentApplicationDto>> getMyApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<StudentApplicationDto> response = applicationService.getMyApplications(
                status, page, size, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/applications/{id}
     * View application details. Accessible by student applicant, company owner, student's college, and Admin.
     */
    @GetMapping("/api/v1/applications/{id}")
    public ResponseEntity<ApplicationDetailDto> getApplicationById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ApplicationDetailDto detail = applicationService.getApplicationById(id, currentUser);
        return ResponseEntity.ok(detail);
    }

    /**
     * GET /api/v1/opportunities/{opportunityId}/applications
     * View applicants to an opportunity, ranked by match percentage descending.
     */
    @GetMapping("/api/v1/opportunities/{opportunityId}/applications")
    @PreAuthorize("hasAnyRole('COMPANY', 'ADMIN')")
    public ResponseEntity<PageResponse<CompanyApplicantDto>> getOpportunityApplications(
            @PathVariable Long opportunityId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false, defaultValue = "true") Boolean eligibleOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<CompanyApplicantDto> response = applicationService.getOpportunityApplications(
                opportunityId, status, eligibleOnly, page, size, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/applications/{id}/status
     * Advance candidate through recruitment stages. Creator company only.
     */
    @PatchMapping("/api/v1/applications/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationDto> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ApplicationDto updated = applicationService.updateApplicationStatus(id, request, currentUser);
        return ResponseEntity.ok(updated);
    }
}
