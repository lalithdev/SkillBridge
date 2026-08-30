package com.skillbridge.analytics.service;

import com.skillbridge.analytics.dto.PlacementFunnelDto;
import com.skillbridge.analytics.dto.SkillAvailabilityDto;
import com.skillbridge.analytics.dto.SkillDemandDto;
import com.skillbridge.analytics.dto.SkillGapDashboardDto;
import com.skillbridge.analytics.entity.GapSeverity;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.repository.RequiredSkillRepository;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private StudentSkillRepository studentSkillRepository;
    @Mock
    private OpportunityRepository opportunityRepository;
    @Mock
    private RequiredSkillRepository requiredSkillRepository;
    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private CustomUserDetails collegeUser;
    private Skill javaSkill;
    private Skill reactSkill;

    @BeforeEach
    void setUp() {
        collegeUser = CustomUserDetails.builder()
                .userId(10L)
                .email("dean@univ.edu")
                .role(Role.COLLEGE)
                .active(true)
                .collegeId(1L)
                .build();

        javaSkill = Skill.builder().id(1L).name("Java").category("Programming").active(true).build();
        reactSkill = Skill.builder().id(2L).name("React").category("Frontend").active(true).build();
    }

    @Test
    @DisplayName("Compute skill availability % - exact formula (studentsWithSkill / totalStudents) * 100")
    void getSkillAvailability_ExactFormula() {
        when(skillRepository.findByActiveTrue()).thenReturn(List.of(javaSkill, reactSkill));
        when(studentProfileRepository.countByCollegeId(1L)).thenReturn(200L);
        when(studentSkillRepository.countStudentsWithSkillInCollege(1L, 1L, null)).thenReturn(100L); // 50.00%
        when(studentSkillRepository.countStudentsWithSkillInCollege(2L, 1L, null)).thenReturn(40L);  // 20.00%

        List<SkillAvailabilityDto> availability = analyticsService.getSkillAvailability(null, null, collegeUser);

        assertThat(availability).hasSize(2);
        assertThat(availability.get(0).getAvailabilityPercent()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(availability.get(0).getStudentCount()).isEqualTo(100);
        assertThat(availability.get(1).getAvailabilityPercent()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
        assertThat(availability.get(1).getStudentCount()).isEqualTo(40);
    }

    @Test
    @DisplayName("Compute industry demand % - exact formula (openOppsRequiringSkill / totalOpenOpps) * 100")
    void getSkillDemand_ExactFormula() {
        when(skillRepository.findByActiveTrue()).thenReturn(List.of(javaSkill, reactSkill));
        when(opportunityRepository.countByStatus(OpportunityStatus.OPEN)).thenReturn(50L);
        when(requiredSkillRepository.countOpenOpportunitiesRequiringSkill(1L, null)).thenReturn(30L); // 60.00%
        when(requiredSkillRepository.countOpenOpportunitiesRequiringSkill(2L, null)).thenReturn(10L); // 20.00%

        List<SkillDemandDto> demand = analyticsService.getSkillDemand(null, collegeUser);

        assertThat(demand).hasSize(2);
        assertThat(demand.get(0).getDemandPercent()).isEqualByComparingTo(BigDecimal.valueOf(60.00));
        assertThat(demand.get(0).getOpportunityCount()).isEqualTo(30);
        assertThat(demand.get(1).getDemandPercent()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
        assertThat(demand.get(1).getOpportunityCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("Compute skill gap dashboard and classify severity bands (OD-01)")
    void getSkillGapDashboard_SeverityBands() {
        when(skillRepository.findByActiveTrue()).thenReturn(List.of(javaSkill, reactSkill));
        when(studentProfileRepository.countByCollegeId(1L)).thenReturn(100L);
        when(opportunityRepository.countByStatus(OpportunityStatus.OPEN)).thenReturn(100L);

        // Java: demand 80%, availability 40% -> gap = 40% -> HIGH (>= 30)
        when(requiredSkillRepository.countOpenOpportunitiesRequiringSkill(1L, null)).thenReturn(80L);
        when(studentSkillRepository.countStudentsWithSkillInCollege(1L, 1L, null)).thenReturn(40L);

        // React: demand 30%, availability 40% -> gap = -10% -> SURPLUS (<= 0)
        when(requiredSkillRepository.countOpenOpportunitiesRequiringSkill(2L, null)).thenReturn(30L);
        when(studentSkillRepository.countStudentsWithSkillInCollege(2L, 1L, null)).thenReturn(40L);

        SkillGapDashboardDto dashboard = analyticsService.getSkillGapDashboard(null, null, collegeUser);

        assertThat(dashboard).isNotNull();
        assertThat(dashboard.getGaps()).hasSize(2);

        assertThat(dashboard.getGaps().get(0).getGapPercent()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
        assertThat(dashboard.getGaps().get(0).getSeverity()).isEqualTo(GapSeverity.HIGH);

        assertThat(dashboard.getGaps().get(1).getGapPercent()).isEqualByComparingTo(BigDecimal.valueOf(-10.00));
        assertThat(dashboard.getGaps().get(1).getSeverity()).isEqualTo(GapSeverity.SURPLUS);
    }

    @Test
    @DisplayName("Compute placement funnel - correct aggregation of all recruitment stages")
    void getPlacementFunnel_Success() {
        when(applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(1L, null, ApplicationStatus.APPLIED)).thenReturn(15L);
        when(applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(1L, null, ApplicationStatus.UNDER_REVIEW)).thenReturn(10L);
        when(applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(1L, null, ApplicationStatus.SHORTLISTED)).thenReturn(8L);
        when(applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(1L, null, ApplicationStatus.INTERVIEW)).thenReturn(5L);
        when(applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(1L, null, ApplicationStatus.SELECTED)).thenReturn(4L);
        when(applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(1L, null, ApplicationStatus.REJECTED)).thenReturn(6L);

        PlacementFunnelDto funnel = analyticsService.getPlacementFunnel(null, null, collegeUser);

        assertThat(funnel.getApplied()).isEqualTo(15);
        assertThat(funnel.getUnderReview()).isEqualTo(10);
        assertThat(funnel.getShortlisted()).isEqualTo(8);
        assertThat(funnel.getInterview()).isEqualTo(5);
        assertThat(funnel.getSelected()).isEqualTo(4);
        assertThat(funnel.getRejected()).isEqualTo(6);
        assertThat(funnel.getTotalApplications()).isEqualTo(48);
    }
}
