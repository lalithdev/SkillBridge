package com.skillbridge.student.repository;

import com.skillbridge.student.entity.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Page<StudentProfile> findByCollegeId(Long collegeId, Pageable pageable);

    List<StudentProfile> findByCollegeId(Long collegeId);

    List<StudentProfile> findByCollegeIdAndDepartmentId(Long collegeId, Long departmentId);

    long countByCollegeId(Long collegeId);

    long countByCollegeIdAndDepartmentId(Long collegeId, Long departmentId);

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.collegeId = :collegeId " +
           "AND (:departmentId IS NULL OR sp.departmentId = :departmentId) " +
           "AND (:yearOfStudy IS NULL OR sp.yearOfStudy = :yearOfStudy) " +
           "AND (CAST(:search AS string) IS NULL OR LOWER(sp.firstName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "     OR LOWER(sp.lastName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "     OR LOWER(CONCAT(sp.firstName, ' ', sp.lastName)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<StudentProfile> findCollegeStudents(
            @Param("collegeId") Long collegeId,
            @Param("departmentId") Long departmentId,
            @Param("yearOfStudy") Short yearOfStudy,
            @Param("search") String search,
            Pageable pageable
    );
}
