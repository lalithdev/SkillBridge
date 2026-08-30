package com.skillbridge.college.controller;

import com.skillbridge.college.dto.CollegeDepartmentSummaryDto;
import com.skillbridge.college.dto.CollegeProfileDto;
import com.skillbridge.college.dto.StudentSummaryDto;
import com.skillbridge.college.dto.UpdateCollegeProfileRequest;
import com.skillbridge.college.service.CollegeService;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    /**
     * GET /api/v1/colleges/profile
     * Get profile for authenticated college.
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('COLLEGE')")
    public ResponseEntity<CollegeProfileDto> getCollegeProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CollegeProfileDto profile = collegeService.getCollegeProfile(currentUser);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/v1/colleges/profile
     * Update college profile.
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('COLLEGE')")
    public ResponseEntity<CollegeProfileDto> updateCollegeProfile(
            @Valid @RequestBody UpdateCollegeProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CollegeProfileDto updated = collegeService.updateCollegeProfile(request, currentUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/v1/colleges/students
     * List paginated college students roster.
     */
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('COLLEGE', 'ADMIN')")
    public ResponseEntity<PageResponse<StudentSummaryDto>> getCollegeStudents(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Short yearOfStudy,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<StudentSummaryDto> response = collegeService.getCollegeStudents(
                collegeId, departmentId, yearOfStudy, search, page, size, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/colleges/departments
     * Get college student enrollment breakdown per department.
     */
    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('COLLEGE', 'ADMIN')")
    public ResponseEntity<List<CollegeDepartmentSummaryDto>> getCollegeDepartmentBreakdown(
            @RequestParam(required = false) Long collegeId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<CollegeDepartmentSummaryDto> response = collegeService.getCollegeDepartmentBreakdown(
                collegeId, currentUser);
        return ResponseEntity.ok(response);
    }
}
