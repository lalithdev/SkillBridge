package com.skillbridge.skill.repository;

import com.skillbridge.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Skill> findByActiveTrue();

    @Query("SELECT s FROM Skill s WHERE " +
           "(:isActive IS NULL OR s.active = :isActive) AND " +
           "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) AND " +
           "(:category IS NULL OR LOWER(s.category) = LOWER(CAST(:category AS string))) " +
           "ORDER BY s.name ASC")
    List<Skill> searchSkills(
            @Param("search") String search,
            @Param("category") String category,
            @Param("isActive") Boolean isActive);
}
