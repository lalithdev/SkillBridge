package com.skillbridge.college.service;

import com.skillbridge.college.dto.CreateDepartmentRequest;
import com.skillbridge.college.dto.DepartmentDto;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        departmentService = new DepartmentServiceImpl(departmentRepository);
    }

    @Test
    @DisplayName("listDepartments - returns active departments by default")
    void listDepartmentsSuccess() {
        Department d1 = Department.builder().id(1L).name("Computer Science").code("CSE").active(true).build();
        Department d2 = Department.builder().id(2L).name("Information Technology").code("IT").active(true).build();

        when(departmentRepository.findByActiveTrue()).thenReturn(List.of(d1, d2));

        List<DepartmentDto> result = departmentService.listDepartments(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("CSE");
        assertThat(result.get(1).getCode()).isEqualTo("IT");
    }

    @Test
    @DisplayName("createDepartment - successfully creates department")
    void createDepartmentSuccess() {
        CreateDepartmentRequest req = CreateDepartmentRequest.builder()
                .name("Mechanical Engineering")
                .code("MECH")
                .build();

        when(departmentRepository.existsByName("Mechanical Engineering")).thenReturn(false);
        when(departmentRepository.existsByCode("MECH")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            d.setId(5L);
            return d;
        });

        DepartmentDto result = departmentService.createDepartment(req);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Mechanical Engineering");
        assertThat(result.getCode()).isEqualTo("MECH");
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("createDepartment - throws DuplicateResourceException if name or code exists")
    void createDepartmentDuplicateThrows() {
        CreateDepartmentRequest req = CreateDepartmentRequest.builder()
                .name("Computer Science")
                .code("CSE")
                .build();

        when(departmentRepository.existsByName("Computer Science")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Department name already exists");
    }
}
