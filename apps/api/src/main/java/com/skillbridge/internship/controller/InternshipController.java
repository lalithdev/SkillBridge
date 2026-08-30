package com.skillbridge.internship.controller;

import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.internship.dto.*;
import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.internship.service.CompanyFeedbackService;
import com.skillbridge.internship.service.InternshipRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InternshipController {

    private final InternshipRecordService internshipRecordService;
    private final CompanyFeedbackService companyFeedbackService;

    public InternshipController(
            InternshipRecordService internshipRecordService,
            CompanyFeedbackService companyFeedbackService) {
        this.internshipRecordService = internshipRecordService;
        this.companyFeedbackService = companyFeedbackService;
    }

    /**
     * GET /api/v1/internships/my
     * Student's confirmed internships / placements.
     */
    @GetMapping("/api/v1/internships/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<InternshipRecordDto>> getMyInternships(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<InternshipRecordDto> response = internshipRecordService.getMyInternships(currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/internships/company/my
     * Company's active interns & placements.
     */
    @GetMapping("/api/v1/internships/company/my")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<InternshipRecordDto>> getCompanyInternships(
            @RequestParam(required = false) InternshipStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<InternshipRecordDto> response = internshipRecordService.getCompanyInternships(
                status, page, size, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/internships/{id}
     * View specific internship/placement record.
     */
    @GetMapping("/api/v1/internships/{id}")
    public ResponseEntity<InternshipRecordDto> getInternshipById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        InternshipRecordDto detail = internshipRecordService.getInternshipById(id, currentUser);
        return ResponseEntity.ok(detail);
    }

    /**
     * PATCH /api/v1/internships/{id}/status
     * Update internship progress lifecycle. Employer company only.
     */
    @PatchMapping("/api/v1/internships/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<InternshipRecordDto> updateInternshipStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInternshipStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        InternshipRecordDto updated = internshipRecordService.updateInternshipStatus(id, request, currentUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/v1/internships/{internshipId}/feedback
     * Submit company feedback following internship completion.
     */
    @PostMapping("/api/v1/internships/{internshipId}/feedback")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<FeedbackDto> submitCompanyFeedback(
            @PathVariable Long internshipId,
            @Valid @RequestBody SubmitFeedbackRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        FeedbackDto created = companyFeedbackService.submitCompanyFeedback(internshipId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/v1/internships/{internshipId}/feedback
     * View feedback for an internship.
     */
    @GetMapping("/api/v1/internships/{internshipId}/feedback")
    public ResponseEntity<FeedbackDto> getInternshipFeedback(
            @PathVariable Long internshipId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        FeedbackDto feedback = companyFeedbackService.getInternshipFeedback(internshipId, currentUser);
        return ResponseEntity.ok(feedback);
    }

    /**
     * GET /api/v1/colleges/feedback
     * College aggregated company feedback.
     */
    @GetMapping("/api/v1/colleges/feedback")
    @PreAuthorize("hasAnyRole('COLLEGE', 'ADMIN')")
    public ResponseEntity<PageResponse<CollegeFeedbackItemDto>> getCollegeAggregatedFeedback(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<CollegeFeedbackItemDto> response = companyFeedbackService.getCollegeAggregatedFeedback(
                collegeId, page, size, currentUser);
        return ResponseEntity.ok(response);
    }
}
