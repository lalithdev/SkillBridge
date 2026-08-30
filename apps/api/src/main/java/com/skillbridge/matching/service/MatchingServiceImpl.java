package com.skillbridge.matching.service;

import com.skillbridge.common.dto.PageMetadata;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.matching.dto.EligibilityResultDto;
import com.skillbridge.matching.dto.MatchResultDto;
import com.skillbridge.opportunity.dto.OpportunityDetailDto;
import com.skillbridge.opportunity.dto.OpportunityListItemDto;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.entity.OpportunityRequiredBranch;
import com.skillbridge.opportunity.entity.OpportunityRequiredYear;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.RequiredSkill;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredBranchRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredYearRepository;
import com.skillbridge.opportunity.repository.RequiredSkillRepository;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.entity.StudentSkill;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements skill match computation and eligibility evaluation.
 * Algorithm (FR-MATCH-01):
 *   matched      = studentSkillIds ∩ requiredSkillIds
 *   missing      = requiredSkillIds \ studentSkillIds
 *   matchPercent = (|matched| / |requiredSkillIds|) * 100
 *   Edge case: if |requiredSkillIds| == 0 → matchPercent = 0
 *
 * Eligibility algorithm (FR-MATCH-02):
 *   - Branch: if requiredBranches non-empty and student.departmentId not in set → ineligible
 *   - Year: if requiredYears non-empty and student.yearOfStudy not in set → ineligible
 *   - CGPA: if minCgpa > 0 and (student.cgpa is null or student.cgpa < minCgpa) → ineligible
 */
@Service
@Transactional(readOnly = true)
public class MatchingServiceImpl implements MatchingService {

    private final OpportunityRepository opportunityRepository;
    private final RequiredSkillRepository requiredSkillRepository;
    private final OpportunityRequiredBranchRepository requiredBranchRepository;
    private final OpportunityRequiredYearRepository requiredYearRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SkillRepository skillRepository;

    public MatchingServiceImpl(
            OpportunityRepository opportunityRepository,
            RequiredSkillRepository requiredSkillRepository,
            OpportunityRequiredBranchRepository requiredBranchRepository,
            OpportunityRequiredYearRepository requiredYearRepository,
            StudentSkillRepository studentSkillRepository,
            StudentProfileRepository studentProfileRepository,
            CompanyProfileRepository companyProfileRepository,
            SkillRepository skillRepository) {
        this.opportunityRepository = opportunityRepository;
        this.requiredSkillRepository = requiredSkillRepository;
        this.requiredBranchRepository = requiredBranchRepository;
        this.requiredYearRepository = requiredYearRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public MatchResultDto evaluateOpportunityMatch(Long opportunityId, CustomUserDetails currentUser) {
        StudentProfile studentProfile = getStudentProfile(currentUser);
        return computeMatchForStudent(studentProfile, opportunityId);
    }

    @Override
    public MatchResultDto computeMatch(Long studentProfileId, Long opportunityId) {
        StudentProfile studentProfile = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + studentProfileId));
        return computeMatchForStudent(studentProfile, opportunityId);
    }

    /**
     * Dynamic match evaluation for a loaded student profile and opportunity.
     */
    public MatchResultDto computeMatchForStudent(StudentProfile studentProfile, Long opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + opportunityId));

        List<RequiredSkill> requiredSkillLinks = requiredSkillRepository.findByOpportunityId(opportunityId);
        Set<Long> requiredSkillIds = requiredSkillLinks.stream()
                .map(RequiredSkill::getSkillId)
                .collect(Collectors.toSet());

        List<StudentSkill> studentSkillLinks = studentSkillRepository.findByStudentProfileId(studentProfile.getId());
        Set<Long> studentSkillIds = studentSkillLinks.stream()
                .map(StudentSkill::getSkillId)
                .collect(Collectors.toSet());

        Map<Long, Skill> requiredSkillMap = skillRepository.findAllById(requiredSkillIds)
                .stream().collect(Collectors.toMap(Skill::getId, s -> s));

        Set<Long> matchedIds = new HashSet<>(studentSkillIds);
        matchedIds.retainAll(requiredSkillIds);

        Set<Long> missingIds = new HashSet<>(requiredSkillIds);
        missingIds.removeAll(studentSkillIds);

        List<SkillDto> matchedSkills = matchedIds.stream()
                .map(id -> toSkillDto(requiredSkillMap.get(id)))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SkillDto::getName))
                .collect(Collectors.toList());

        List<SkillDto> missingSkills = missingIds.stream()
                .map(id -> toSkillDto(requiredSkillMap.get(id)))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SkillDto::getName))
                .collect(Collectors.toList());

        double matchPercent = computeMatchPercent(studentSkillIds, requiredSkillIds);

        List<OpportunityRequiredBranch> requiredBranches = requiredBranchRepository.findByOpportunityId(opportunityId);
        List<OpportunityRequiredYear> requiredYears = requiredYearRepository.findByOpportunityId(opportunityId);

        EligibilityResultDto eligibility = checkEligibilityInternal(
                opportunity,
                studentProfile.getDepartmentId(),
                studentProfile.getYearOfStudy() != null ? studentProfile.getYearOfStudy().intValue() : null,
                studentProfile.getCgpa(),
                requiredBranches,
                requiredYears);

        return MatchResultDto.builder()
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .matchPercent(matchPercent)
                .eligible(eligibility.isEligible())
                .ineligibilityReasons(eligibility.getFailureReasons())
                .build();
    }

    @Override
    public PageResponse<OpportunityListItemDto> getRecommendations(int page, int size, CustomUserDetails currentUser) {
        StudentProfile studentProfile = getStudentProfile(currentUser);

        // Fetch all OPEN opportunities
        List<Opportunity> openOpportunities = opportunityRepository.findAll().stream()
                .filter(o -> o.getStatus() == OpportunityStatus.OPEN)
                .collect(Collectors.toList());

        // Transform and enrich with match scores
        List<OpportunityListItemDto> scoredItems = openOpportunities.stream()
                .map(opp -> {
                    OpportunityListItemDto item = buildListItemDto(opp);
                    enrichListItem(item, studentProfile);
                    return item;
                })
                .sorted((a, b) -> {
                    double scoreA = a.getMatchPercent() != null ? a.getMatchPercent() : 0.0;
                    double scoreB = b.getMatchPercent() != null ? b.getMatchPercent() : 0.0;
                    int cmp = Double.compare(scoreB, scoreA); // DESC
                    if (cmp != 0) return cmp;
                    return Long.compare(b.getId(), a.getId()); // tie break
                })
                .collect(Collectors.toList());

        // Apply pagination
        int start = Math.min(page * size, scoredItems.size());
        int end = Math.min(start + size, scoredItems.size());
        List<OpportunityListItemDto> pageContent = scoredItems.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<OpportunityListItemDto> pagedResult = new PageImpl<>(pageContent, pageable, scoredItems.size());

        return PageResponse.of(pageContent, PageMetadata.from(pagedResult));
    }

    @Override
    public void enrichListItem(OpportunityListItemDto item, StudentProfile studentProfile) {
        Long opportunityId = item.getId();

        List<RequiredSkill> requiredSkillLinks = requiredSkillRepository.findByOpportunityId(opportunityId);
        Set<Long> requiredSkillIds = requiredSkillLinks.stream()
                .map(RequiredSkill::getSkillId)
                .collect(Collectors.toSet());

        List<StudentSkill> studentSkillLinks = studentSkillRepository.findByStudentProfileId(studentProfile.getId());
        Set<Long> studentSkillIds = studentSkillLinks.stream()
                .map(StudentSkill::getSkillId)
                .collect(Collectors.toSet());

        double matchPercent = computeMatchPercent(studentSkillIds, requiredSkillIds);
        item.setMatchPercent(matchPercent);

        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);
        if (opportunity != null) {
            List<OpportunityRequiredBranch> requiredBranches = requiredBranchRepository.findByOpportunityId(opportunityId);
            List<OpportunityRequiredYear> requiredYears = requiredYearRepository.findByOpportunityId(opportunityId);
            EligibilityResultDto eligibility = checkEligibilityInternal(
                    opportunity,
                    studentProfile.getDepartmentId(),
                    studentProfile.getYearOfStudy() != null ? studentProfile.getYearOfStudy().intValue() : null,
                    studentProfile.getCgpa(),
                    requiredBranches,
                    requiredYears);
            item.setEligible(eligibility.isEligible());
        }
    }

    @Override
    public void enrichDetailItem(OpportunityDetailDto detail, StudentProfile studentProfile) {
        MatchResultDto matchResult = computeMatchForStudent(studentProfile, detail.getId());
        detail.setMatchBreakdown(matchResult);
    }

    @Override
    public double computeMatchPercent(Set<Long> studentSkillIds, Set<Long> requiredSkillIds) {
        if (requiredSkillIds == null || requiredSkillIds.isEmpty()) {
            return 0.0;
        }
        Set<Long> matched = new HashSet<>(studentSkillIds != null ? studentSkillIds : Collections.emptySet());
        matched.retainAll(requiredSkillIds);
        return ((double) matched.size() / requiredSkillIds.size()) * 100.0;
    }

    private EligibilityResultDto checkEligibilityInternal(
            Opportunity opportunity,
            Long studentDepartmentId,
            Integer studentYear,
            BigDecimal studentCgpa,
            List<OpportunityRequiredBranch> requiredBranches,
            List<OpportunityRequiredYear> requiredYears) {

        boolean eligible = true;
        List<String> failureReasons = new ArrayList<>();

        // Branch check
        if (requiredBranches != null && !requiredBranches.isEmpty()) {
            Set<Long> eligibleDeptIds = requiredBranches.stream()
                    .map(OpportunityRequiredBranch::getDepartmentId)
                    .collect(Collectors.toSet());
            if (studentDepartmentId == null || !eligibleDeptIds.contains(studentDepartmentId)) {
                eligible = false;
                failureReasons.add("Branch not eligible");
            }
        }

        // Year of study check
        if (requiredYears != null && !requiredYears.isEmpty()) {
            Set<Integer> eligibleYears = requiredYears.stream()
                    .map(y -> y.getYearOfStudy().intValue())
                    .collect(Collectors.toSet());
            if (studentYear == null || !eligibleYears.contains(studentYear)) {
                eligible = false;
                failureReasons.add("Year not eligible");
            }
        }

        // CGPA check
        BigDecimal minCgpa = opportunity.getMinCgpa();
        if (minCgpa != null && minCgpa.compareTo(BigDecimal.ZERO) > 0) {
            if (studentCgpa == null || studentCgpa.compareTo(minCgpa) < 0) {
                eligible = false;
                failureReasons.add("CGPA below required minimum (required: " + minCgpa.toPlainString() + ")");
            }
        }

        return EligibilityResultDto.builder()
                .eligible(eligible)
                .failureReasons(failureReasons)
                .build();
    }

    private StudentProfile getStudentProfile(CustomUserDetails currentUser) {
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new ForbiddenException("Authentication required");
        }
        if (currentUser.getStudentProfileId() != null) {
            return studentProfileRepository.findById(currentUser.getStudentProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        }
        return studentProfileRepository.findByUserId(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + currentUser.getEmail()));
    }

    private OpportunityListItemDto buildListItemDto(Opportunity opp) {
        CompanyProfile company = companyProfileRepository.findById(opp.getCompanyProfileId()).orElse(null);

        List<RequiredSkill> requiredSkillLinks = requiredSkillRepository.findByOpportunityId(opp.getId());
        List<Long> skillIds = requiredSkillLinks.stream().map(RequiredSkill::getSkillId).collect(Collectors.toList());
        List<SkillDto> skillDtos = skillIds.isEmpty() ? Collections.emptyList()
                : skillRepository.findAllById(skillIds).stream().map(this::toSkillDto).collect(Collectors.toList());

        return OpportunityListItemDto.builder()
                .id(opp.getId())
                .companyId(company != null ? company.getId() : opp.getCompanyProfileId())
                .companyName(company != null ? company.getName() : null)
                .companyLocation(company != null ? company.getLocation() : null)
                .companyVerificationStatus(company != null ? company.getVerificationStatus() : null)
                .title(opp.getTitle())
                .type(opp.getType())
                .mode(opp.getMode())
                .stipendAmount(opp.getStipendAmount())
                .applicationDeadline(opp.getApplicationDeadline())
                .status(opp.getStatus())
                .requiredSkills(skillDtos)
                .build();
    }

    private SkillDto toSkillDto(Skill skill) {
        if (skill == null) return null;
        return SkillDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .active(skill.isActive())
                .build();
    }
}
