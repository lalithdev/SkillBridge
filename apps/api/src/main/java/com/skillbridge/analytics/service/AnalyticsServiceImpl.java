package com.skillbridge.analytics.service;

import com.skillbridge.analytics.dto.PlacementFunnelDto;
import com.skillbridge.analytics.dto.SkillAvailabilityDto;
import com.skillbridge.analytics.dto.SkillDemandDto;
import com.skillbridge.analytics.dto.SkillGapDashboardDto;
import com.skillbridge.analytics.dto.SkillGapItemDto;
import com.skillbridge.analytics.entity.GapSeverity;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.repository.RequiredSkillRepository;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SkillRepository skillRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final OpportunityRepository opportunityRepository;
    private final RequiredSkillRepository requiredSkillRepository;
    private final ApplicationRepository applicationRepository;

    public AnalyticsServiceImpl(
            SkillRepository skillRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            OpportunityRepository opportunityRepository,
            RequiredSkillRepository requiredSkillRepository,
            ApplicationRepository applicationRepository) {
        this.skillRepository = skillRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.opportunityRepository = opportunityRepository;
        this.requiredSkillRepository = requiredSkillRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<SkillAvailabilityDto> getSkillAvailability(
            Long collegeIdOverride,
            Long departmentId,
            CustomUserDetails currentUser) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeIdOverride, currentUser);

        long totalStudents = (departmentId != null)
                ? studentProfileRepository.countByCollegeIdAndDepartmentId(effectiveCollegeId, departmentId)
                : studentProfileRepository.countByCollegeId(effectiveCollegeId);

        List<Skill> activeSkills = skillRepository.findByActiveTrue();
        List<SkillAvailabilityDto> result = new ArrayList<>();

        for (Skill skill : activeSkills) {
            long studentCount = studentSkillRepository.countStudentsWithSkillInCollege(
                    skill.getId(), effectiveCollegeId, departmentId);

            BigDecimal availabilityPercent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (totalStudents > 0) {
                double percent = ((double) studentCount / totalStudents) * 100.0;
                availabilityPercent = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
            }

            result.add(SkillAvailabilityDto.builder()
                    .skillId(skill.getId())
                    .skillName(skill.getName())
                    .category(skill.getCategory())
                    .availabilityPercent(availabilityPercent)
                    .studentCount((int) studentCount)
                    .build());
        }

        return result;
    }

    @Override
    public List<SkillDemandDto> getSkillDemand(
            OpportunityType type,
            CustomUserDetails currentUser) {
        long totalOpenOpportunities = (type != null)
                ? opportunityRepository.countByTypeAndStatus(type, OpportunityStatus.OPEN)
                : opportunityRepository.countByStatus(OpportunityStatus.OPEN);

        List<Skill> activeSkills = skillRepository.findByActiveTrue();
        List<SkillDemandDto> result = new ArrayList<>();

        for (Skill skill : activeSkills) {
            long oppCount = requiredSkillRepository.countOpenOpportunitiesRequiringSkill(skill.getId(), type);

            BigDecimal demandPercent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (totalOpenOpportunities > 0) {
                double percent = ((double) oppCount / totalOpenOpportunities) * 100.0;
                demandPercent = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
            }

            result.add(SkillDemandDto.builder()
                    .skillId(skill.getId())
                    .skillName(skill.getName())
                    .category(skill.getCategory())
                    .demandPercent(demandPercent)
                    .opportunityCount((int) oppCount)
                    .build());
        }

        return result;
    }

    @Override
    public SkillGapDashboardDto getSkillGapDashboard(
            Long collegeIdOverride,
            GapSeverity severity,
            CustomUserDetails currentUser) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeIdOverride, currentUser);

        long totalStudents = studentProfileRepository.countByCollegeId(effectiveCollegeId);
        long totalOpenOpportunities = opportunityRepository.countByStatus(OpportunityStatus.OPEN);

        List<Skill> activeSkills = skillRepository.findByActiveTrue();
        List<SkillGapItemDto> gaps = new ArrayList<>();

        for (Skill skill : activeSkills) {
            long studentCount = studentSkillRepository.countStudentsWithSkillInCollege(
                    skill.getId(), effectiveCollegeId, null);
            BigDecimal availabilityPercent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (totalStudents > 0) {
                double percent = ((double) studentCount / totalStudents) * 100.0;
                availabilityPercent = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
            }

            long oppCount = requiredSkillRepository.countOpenOpportunitiesRequiringSkill(skill.getId(), null);
            BigDecimal demandPercent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (totalOpenOpportunities > 0) {
                double percent = ((double) oppCount / totalOpenOpportunities) * 100.0;
                demandPercent = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal gapPercent = demandPercent.subtract(availabilityPercent).setScale(2, RoundingMode.HALF_UP);
            GapSeverity gapSeverity = classifySeverity(gapPercent);

            if (severity == null || gapSeverity == severity) {
                gaps.add(SkillGapItemDto.builder()
                        .skillId(skill.getId())
                        .skillName(skill.getName())
                        .category(skill.getCategory())
                        .demandPercent(demandPercent)
                        .availabilityPercent(availabilityPercent)
                        .gapPercent(gapPercent)
                        .severity(gapSeverity)
                        .build());
            }
        }

        return SkillGapDashboardDto.builder()
                .collegeId(effectiveCollegeId)
                .totalStudents((int) totalStudents)
                .totalOpenOpportunities((int) totalOpenOpportunities)
                .gaps(gaps)
                .build();
    }

    @Override
    public PlacementFunnelDto getPlacementFunnel(
            Long collegeIdOverride,
            Long departmentId,
            CustomUserDetails currentUser) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeIdOverride, currentUser);

        int applied = (int) applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(
                effectiveCollegeId, departmentId, ApplicationStatus.APPLIED);
        int underReview = (int) applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(
                effectiveCollegeId, departmentId, ApplicationStatus.UNDER_REVIEW);
        int shortlisted = (int) applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(
                effectiveCollegeId, departmentId, ApplicationStatus.SHORTLISTED);
        int interview = (int) applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(
                effectiveCollegeId, departmentId, ApplicationStatus.INTERVIEW);
        int selected = (int) applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(
                effectiveCollegeId, departmentId, ApplicationStatus.SELECTED);
        int rejected = (int) applicationRepository.countByCollegeIdAndDepartmentIdAndStatus(
                effectiveCollegeId, departmentId, ApplicationStatus.REJECTED);

        int totalApplications = applied + underReview + shortlisted + interview + selected + rejected;

        return PlacementFunnelDto.builder()
                .applied(applied)
                .underReview(underReview)
                .shortlisted(shortlisted)
                .interview(interview)
                .selected(selected)
                .rejected(rejected)
                .totalApplications(totalApplications)
                .build();
    }

    private Long resolveEffectiveCollegeId(Long collegeIdOverride, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            if (collegeIdOverride != null) {
                return collegeIdOverride;
            }
            throw new BadRequestException("Admin must specify collegeId query parameter");
        }
        if (currentUser.getRole() == Role.COLLEGE) {
            if (currentUser.getCollegeId() != null) {
                return currentUser.getCollegeId();
            }
            throw new ForbiddenException("College profile not found for authenticated user");
        }
        throw new ForbiddenException("Only colleges and admins can view analytics");
    }

    private GapSeverity classifySeverity(BigDecimal gapPercent) {
        double gap = gapPercent.doubleValue();
        if (gap >= 30.0) {
            return GapSeverity.HIGH;
        } else if (gap >= 15.0) {
            return GapSeverity.MODERATE;
        } else if (gap > 0.0) {
            return GapSeverity.LOW;
        } else {
            return GapSeverity.SURPLUS;
        }
    }
}
