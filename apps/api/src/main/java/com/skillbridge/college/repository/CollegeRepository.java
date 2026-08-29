package com.skillbridge.college.repository;

import com.skillbridge.college.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {

    Optional<College> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
