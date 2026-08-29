package com.skillbridge.college.service;

import com.skillbridge.college.dto.CreateDepartmentRequest;
import com.skillbridge.college.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDto> listDepartments(Boolean isActive);

    DepartmentDto createDepartment(CreateDepartmentRequest request);
}
