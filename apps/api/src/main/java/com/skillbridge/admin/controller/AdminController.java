package com.skillbridge.admin.controller;

import com.skillbridge.admin.dto.*;
import com.skillbridge.admin.service.AdminService;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.opportunity.dto.OpportunityDetailDto;
import com.skillbridge.user.entity.Role;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * GET /api/v1/admin/users
     * Paginated list of all users.
     */
    @GetMapping("/users")
    public ResponseEntity<PageResponse<AdminUserDto>> listUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDto> response = adminService.listUsers(role, isActive, search, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/admin/users/{id}/status
     * Activate or deactivate user account.
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<AdminUserDto> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        AdminUserDto response = adminService.updateUserStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/admin/verifications
     * List organization verification queue.
     */
    @GetMapping("/verifications")
    public ResponseEntity<List<VerificationItemDto>> getPendingVerifications(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) VerificationStatus status) {
        List<VerificationItemDto> response = adminService.getPendingVerifications(type, status);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/admin/verifications/{type}/{id}
     * Approve or reject organization verification.
     */
    @PatchMapping("/verifications/{type}/{id}")
    public ResponseEntity<VerificationItemDto> updateVerificationStatus(
            @PathVariable String type,
            @PathVariable Long id,
            @Valid @RequestBody UpdateVerificationStatusRequest request) {
        VerificationItemDto response = adminService.updateVerificationStatus(type, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/admin/opportunities/{id}/status
     * Moderate and close inappropriate opportunity.
     */
    @PatchMapping("/opportunities/{id}/status")
    public ResponseEntity<OpportunityDetailDto> moderateOpportunity(
            @PathVariable Long id,
            @Valid @RequestBody ModerateOpportunityRequest request) {
        OpportunityDetailDto response = adminService.moderateOpportunity(id, request);
        return ResponseEntity.ok(response);
    }
}
