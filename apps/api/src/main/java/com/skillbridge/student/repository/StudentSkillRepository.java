package com.skillbridge.student.repository;

import com.skillbridge.student.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {

    List<StudentSkill> findByStudentProfileId(Long studentProfileId);

    boolean existsByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);

    Optional<StudentSkill> findByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);

    void deleteByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);

    void deleteAllByStudentProfileId(Long studentProfileId);
}
