package com.skillbridge.matching.service;

import com.skillbridge.matching.dto.EligibilityResultDto;
import com.skillbridge.matching.dto.MatchResultDto;
import com.skillbridge.opportunity.entity.*;
import com.skillbridge.opportunity.repository.*;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.entity.StudentSkill;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private RequiredSkillRepository requiredSkillRepository;

    @Mock
    private OpportunityRequiredBranchRepository requiredBranchRepository;

    @Mock
    private OpportunityRequiredYearRepository requiredYearRepository;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private MatchingServiceImpl matchingService;

    private StudentProfile studentProfile;
    private Opportunity opportunity;
    private Skill javaSkill;
    private Skill springSkill;
    private Skill dockerSkill;
    private Skill awsSkill;

    @BeforeEach
    void setUp() {
        studentProfile = StudentProfile.builder()
                .id(100L)
                .userId(10L)
                .collegeId(1L)
                .departmentId(2L)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .firstName("John")
                .lastName("Doe")
                .build();

        opportunity = Opportunity.builder()
                .id(200L)
                .companyProfileId(5L)
                .title("Backend Engineer Intern")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.HYBRID)
                .minCgpa(BigDecimal.valueOf(7.50))
                .status(OpportunityStatus.OPEN)
                .build();

        javaSkill = Skill.builder().id(1L).name("Java").category("Language").active(true).build();
        springSkill = Skill.builder().id(2L).name("Spring Boot").category("Framework").active(true).build();
        dockerSkill = Skill.builder().id(3L).name("Docker").category("DevOps").active(true).build();
        awsSkill = Skill.builder().id(4L).name("AWS").category("Cloud").active(true).build();
    }

    @Nested
    @DisplayName("FR-MATCH-01: Skill Coverage Calculation")
    class SkillCoverageTests {

        @Test
        @DisplayName("Should return 50.0% when student has 2 of 4 required skills")
        void testPartialSkillMatch() {
            // Student has Java(1) and Spring(2)
            when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(List.of(
                    StudentSkill.builder().id(1L).studentProfileId(100L).skillId(1L).build(),
                    StudentSkill.builder().id(2L).studentProfileId(100L).skillId(2L).build()
            ));

            // Opportunity requires Java(1), Spring(2), Docker(3), AWS(4)
            when(requiredSkillRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    RequiredSkill.builder().opportunityId(200L).skillId(1L).build(),
                    RequiredSkill.builder().opportunityId(200L).skillId(2L).build(),
                    RequiredSkill.builder().opportunityId(200L).skillId(3L).build(),
                    RequiredSkill.builder().opportunityId(200L).skillId(4L).build()
            ));

            when(opportunityRepository.findById(200L)).thenReturn(Optional.of(opportunity));
            when(skillRepository.findAllById(Set.of(1L, 2L, 3L, 4L)))
                    .thenReturn(List.of(javaSkill, springSkill, dockerSkill, awsSkill));
            when(requiredBranchRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(requiredYearRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());

            MatchResultDto result = matchingService.computeMatchForStudent(studentProfile, 200L);

            assertThat(result.getMatchPercent()).isEqualTo(50.0);
            assertThat(result.getMatchedSkills()).extracting(s -> s.getName())
                    .containsExactlyInAnyOrder("Java", "Spring Boot");
            assertThat(result.getMissingSkills()).extracting(s -> s.getName())
                    .containsExactlyInAnyOrder("Docker", "AWS");
            assertThat(result.isEligible()).isTrue();
        }

        @Test
        @DisplayName("Should return 100.0% when student has all required skills (and extra skills)")
        void testPerfectSkillMatch() {
            when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(List.of(
                    StudentSkill.builder().id(1L).studentProfileId(100L).skillId(1L).build(),
                    StudentSkill.builder().id(2L).studentProfileId(100L).skillId(2L).build(),
                    StudentSkill.builder().id(3L).studentProfileId(100L).skillId(3L).build()
            ));

            when(requiredSkillRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    RequiredSkill.builder().opportunityId(200L).skillId(1L).build(),
                    RequiredSkill.builder().opportunityId(200L).skillId(2L).build()
            ));

            when(opportunityRepository.findById(200L)).thenReturn(Optional.of(opportunity));
            when(skillRepository.findAllById(Set.of(1L, 2L)))
                    .thenReturn(List.of(javaSkill, springSkill));
            when(requiredBranchRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(requiredYearRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());

            MatchResultDto result = matchingService.computeMatchForStudent(studentProfile, 200L);

            assertThat(result.getMatchPercent()).isEqualTo(100.0);
            assertThat(result.getMatchedSkills()).hasSize(2);
            assertThat(result.getMissingSkills()).isEmpty();
        }

        @Test
        @DisplayName("Should return 0.0% when student has none of the required skills")
        void testZeroSkillMatch() {
            when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(Collections.emptyList());

            when(requiredSkillRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    RequiredSkill.builder().opportunityId(200L).skillId(1L).build()
            ));

            when(opportunityRepository.findById(200L)).thenReturn(Optional.of(opportunity));
            when(skillRepository.findAllById(Set.of(1L))).thenReturn(List.of(javaSkill));
            when(requiredBranchRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(requiredYearRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());

            MatchResultDto result = matchingService.computeMatchForStudent(studentProfile, 200L);

            assertThat(result.getMatchPercent()).isEqualTo(0.0);
            assertThat(result.getMatchedSkills()).isEmpty();
            assertThat(result.getMissingSkills()).hasSize(1);
        }

        @Test
        @DisplayName("Edge Case: 0 required skills should yield matchPercent = 0.0 without division by zero")
        void testZeroRequiredSkills() {
            double percent = matchingService.computeMatchPercent(Set.of(1L, 2L), Collections.emptySet());
            assertThat(percent).isEqualTo(0.0);

            double percentNull = matchingService.computeMatchPercent(Set.of(1L), null);
            assertThat(percentNull).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("FR-MATCH-02: Eligibility Evaluation")
    class EligibilityTests {

        @Test
        @DisplayName("Should be eligible when student meets branch, year, and CGPA requirements")
        void testAllCriteriaMet() {
            when(opportunityRepository.findById(200L)).thenReturn(Optional.of(opportunity));
            when(requiredSkillRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(Collections.emptyList());
            when(skillRepository.findAllById(any())).thenReturn(Collections.emptyList());

            // Opportunity requires branch 2 (CSE) and year 3
            when(requiredBranchRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    OpportunityRequiredBranch.builder().opportunityId(200L).departmentId(2L).build()
            ));
            when(requiredYearRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    OpportunityRequiredYear.builder().opportunityId(200L).yearOfStudy((short) 3).build()
            ));

            MatchResultDto result = matchingService.computeMatchForStudent(studentProfile, 200L);

            assertThat(result.isEligible()).isTrue();
            assertThat(result.getIneligibilityReasons()).isEmpty();
        }

        @Test
        @DisplayName("Should be ineligible when student CGPA is below required minimum")
        void testCgpaIneligible() {
            studentProfile.setCgpa(BigDecimal.valueOf(6.50)); // Below required 7.50

            when(opportunityRepository.findById(200L)).thenReturn(Optional.of(opportunity));
            when(requiredSkillRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(Collections.emptyList());
            when(skillRepository.findAllById(any())).thenReturn(Collections.emptyList());
            when(requiredBranchRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(requiredYearRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());

            MatchResultDto result = matchingService.computeMatchForStudent(studentProfile, 200L);

            assertThat(result.isEligible()).isFalse();
            assertThat(result.getIneligibilityReasons()).anyMatch(r -> r.contains("CGPA below required minimum"));
        }

        @Test
        @DisplayName("Should be ineligible with multiple failure reasons when branch and year do not match")
        void testMultipleIneligibilityReasons() {
            studentProfile.setDepartmentId(99L); // Not in [2]
            studentProfile.setYearOfStudy((short) 1); // Not in [3, 4]
            studentProfile.setCgpa(BigDecimal.valueOf(6.00)); // Below 7.50

            when(opportunityRepository.findById(200L)).thenReturn(Optional.of(opportunity));
            when(requiredSkillRepository.findByOpportunityId(200L)).thenReturn(Collections.emptyList());
            when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(Collections.emptyList());
            when(skillRepository.findAllById(any())).thenReturn(Collections.emptyList());

            when(requiredBranchRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    OpportunityRequiredBranch.builder().opportunityId(200L).departmentId(2L).build()
            ));
            when(requiredYearRepository.findByOpportunityId(200L)).thenReturn(List.of(
                    OpportunityRequiredYear.builder().opportunityId(200L).yearOfStudy((short) 3).build(),
                    OpportunityRequiredYear.builder().opportunityId(200L).yearOfStudy((short) 4).build()
            ));

            MatchResultDto result = matchingService.computeMatchForStudent(studentProfile, 200L);

            assertThat(result.isEligible()).isFalse();
            assertThat(result.getIneligibilityReasons()).hasSize(3);
            assertThat(result.getIneligibilityReasons()).contains(
                    "Branch not eligible",
                    "Year not eligible"
            );
            assertThat(result.getIneligibilityReasons()).anyMatch(r -> r.contains("CGPA"));
        }
    }
}
