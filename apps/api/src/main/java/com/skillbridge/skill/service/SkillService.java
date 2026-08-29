package com.skillbridge.skill.service;

import com.skillbridge.skill.dto.CreateSkillRequest;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.skill.dto.UpdateSkillRequest;

import java.util.List;

public interface SkillService {

    List<SkillDto> listSkills(String search, String category, Boolean isActive);

    SkillDto createSkill(CreateSkillRequest request);

    SkillDto updateSkill(Long id, UpdateSkillRequest request);

    void deleteSkill(Long id);
}
