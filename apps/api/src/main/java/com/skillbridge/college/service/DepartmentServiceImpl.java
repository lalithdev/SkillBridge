package com.skillbridge.college.service;

import com.skillbridge.college.dto.CreateDepartmentRequest;
import com.skillbridge.college.dto.DepartmentDto;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> listDepartments(Boolean isActive) {
        List<Department> departments;
        if (isActive == null) {
            departments = departmentRepository.findByActiveTrue();
        } else {
            departments = departmentRepository.findByActive(isActive);
        }

        return departments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentDto createDepartment(CreateDepartmentRequest request) {
        String trimmedName = request.getName().trim();
        String trimmedCode = request.getCode().trim().toUpperCase();

        if (departmentRepository.existsByName(trimmedName)) {
            throw new DuplicateResourceException("Department name already exists: " + trimmedName);
        }
        if (departmentRepository.existsByCode(trimmedCode)) {
            throw new DuplicateResourceException("Department code already exists: " + trimmedCode);
        }

        Department department = Department.builder()
                .name(trimmedName)
                .code(trimmedCode)
                .active(true)
                .build();

        Department saved = departmentRepository.save(department);
        return mapToDto(saved);
    }

    private DepartmentDto mapToDto(Department department) {
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .active(department.isActive())
                .build();
    }
}
