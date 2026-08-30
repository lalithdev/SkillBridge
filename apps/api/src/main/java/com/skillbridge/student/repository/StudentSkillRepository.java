package com.skillbridge.student.repository;

import com.skillbridge.student.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {

    List<StudentSkill> findByStudentProfileId(Long studentProfileId);

    boolean existsByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);

    Optional<StudentSkill> findByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);

    void deleteByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);

    void deleteAllByStudentProfileId(Long studentProfileId);

    @Query("SELECT COUNT(DISTINCT ss.studentProfileId) FROM StudentSkill ss " +
           "JOIN com.skillbridge.student.entity.StudentProfile sp ON ss.studentProfileId = sp.id " +
           "WHERE ss.skillId = :skillId " +
           "AND sp.collegeId = :collegeId " +
           "AND (:departmentId IS NULL OR sp.departmentId = :departmentId)")
    long countStudentsWithSkillInCollege(
            @Param("skillId") Long skillId,
            @Param("collegeId") Long collegeId,
            @Param("departmentId") Long departmentId
    );
}
