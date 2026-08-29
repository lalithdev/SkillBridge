package com.skillbridge.college.repository;

import com.skillbridge.college.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    Optional<Department> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Department> findByActiveTrue();

    List<Department> findByActive(boolean active);

    List<Department> findAllByOrderByCodeAsc();
}
