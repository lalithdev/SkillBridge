package com.skillbridge.skill.service;

import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.skill.dto.CreateSkillRequest;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.skill.dto.UpdateSkillRequest;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    private SkillService skillService;

    @BeforeEach
    void setUp() {
        skillService = new SkillServiceImpl(skillRepository);
    }

    @Test
    @DisplayName("listSkills - returns list of active skills matching query")
    void listSkillsSuccess() {
        Skill s1 = Skill.builder().id(1L).name("Java").category("Language").active(true).build();
        Skill s2 = Skill.builder().id(2L).name("Spring Boot").category("Framework").active(true).build();

        when(skillRepository.searchSkills(null, null, true)).thenReturn(List.of(s1, s2));

        List<SkillDto> result = skillService.listSkills(null, null, true);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Java");
        assertThat(result.get(1).getName()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("createSkill - successfully creates new canonical skill")
    void createSkillSuccess() {
        CreateSkillRequest request = CreateSkillRequest.builder()
                .name("PostgreSQL")
                .category("Database")
                .build();

        when(skillRepository.existsByNameIgnoreCase("PostgreSQL")).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation -> {
            Skill s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });

        SkillDto result = skillService.createSkill(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("PostgreSQL");
        assertThat(result.getCategory()).isEqualTo("Database");
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("createSkill - throws DuplicateResourceException if skill name already exists")
    void createSkillDuplicateThrows() {
        CreateSkillRequest request = CreateSkillRequest.builder()
                .name("Java")
                .category("Language")
                .build();

        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThatThrownBy(() -> skillService.createSkill(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Skill name already exists");
    }

    @Test
    @DisplayName("updateSkill - successfully updates existing skill")
    void updateSkillSuccess() {
        Skill existing = Skill.builder().id(1L).name("Java").category("Language").active(true).build();
        when(skillRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skillRepository.existsByNameIgnoreCaseAndIdNot("Java 21", 1L)).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateSkillRequest updateReq = UpdateSkillRequest.builder()
                .name("Java 21")
                .category("Programming Language")
                .active(true)
                .build();

        SkillDto updated = skillService.updateSkill(1L, updateReq);

        assertThat(updated.getName()).isEqualTo("Java 21");
        assertThat(updated.getCategory()).isEqualTo("Programming Language");
    }

    @Test
    @DisplayName("updateSkill - throws ResourceNotFoundException if skill ID does not exist")
    void updateSkillNotFound() {
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateSkillRequest updateReq = UpdateSkillRequest.builder()
                .name("React")
                .active(true)
                .build();

        assertThatThrownBy(() -> skillService.updateSkill(99L, updateReq))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Skill not found");
    }

    @Test
    @DisplayName("deleteSkill - soft-deactivates skill by setting active to false")
    void deleteSkillSoftDeactivates() {
        Skill existing = Skill.builder().id(1L).name("Java").category("Language").active(true).build();
        when(skillRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        skillService.deleteSkill(1L);

        assertThat(existing.isActive()).isFalse();
        verify(skillRepository).save(existing);
    }
}
