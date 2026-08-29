package com.skillbridge.college.controller;

import com.skillbridge.college.dto.CreateDepartmentRequest;
import com.skillbridge.college.dto.DepartmentDto;
import com.skillbridge.college.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> listDepartments(
            @RequestParam(required = false, defaultValue = "true") Boolean isActive) {
        List<DepartmentDto> departments = departmentService.listDepartments(isActive);
        return ResponseEntity.ok(departments);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDto> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDto created = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
