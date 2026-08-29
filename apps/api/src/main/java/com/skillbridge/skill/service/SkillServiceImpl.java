package com.skillbridge.skill.service;

import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.skill.dto.CreateSkillRequest;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.skill.dto.UpdateSkillRequest;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> listSkills(String search, String category, Boolean isActive) {
        Boolean activeFilter = (isActive != null) ? isActive : true;
        String searchFilter = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String categoryFilter = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        List<Skill> skills = skillRepository.searchSkills(searchFilter, categoryFilter, activeFilter);
        return skills.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillDto createSkill(CreateSkillRequest request) {
        String trimmedName = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Skill name already exists: " + trimmedName);
        }

        Skill skill = Skill.builder()
                .name(trimmedName)
                .category(request.getCategory() != null ? request.getCategory().trim() : null)
                .active(true)
                .build();

        Skill saved = skillRepository.save(skill);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public SkillDto updateSkill(Long id, UpdateSkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        String trimmedName = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Skill name already exists: " + trimmedName);
        }

        skill.setName(trimmedName);
        skill.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        skill.setActive(request.getActive());

        Skill updated = skillRepository.save(skill);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        // Soft-delete
        skill.setActive(false);
        skillRepository.save(skill);
    }

    private SkillDto mapToDto(Skill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .active(skill.isActive())
                .build();
    }
}
