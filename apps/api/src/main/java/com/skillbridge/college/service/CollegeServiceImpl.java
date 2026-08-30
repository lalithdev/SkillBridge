package com.skillbridge.college.service;

import com.skillbridge.college.dto.CollegeDepartmentSummaryDto;
import com.skillbridge.college.dto.CollegeProfileDto;
import com.skillbridge.college.dto.StudentSummaryDto;
import com.skillbridge.college.dto.UpdateCollegeProfileRequest;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.dto.PageMetadata;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentSkillRepository studentSkillRepository;

    public CollegeServiceImpl(
            CollegeRepository collegeRepository,
            StudentProfileRepository studentProfileRepository,
            DepartmentRepository departmentRepository,
            StudentSkillRepository studentSkillRepository) {
        this.collegeRepository = collegeRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.departmentRepository = departmentRepository;
        this.studentSkillRepository = studentSkillRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeProfileDto getCollegeProfile(CustomUserDetails currentUser) {
        if (currentUser.getCollegeId() == null) {
            throw new ForbiddenException("Only colleges can view their profile");
        }

        College college = collegeRepository.findById(currentUser.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College profile not found for id: " + currentUser.getCollegeId()));

        return CollegeProfileDto.from(college);
    }

    @Override
    public CollegeProfileDto updateCollegeProfile(UpdateCollegeProfileRequest request, CustomUserDetails currentUser) {
        if (currentUser.getCollegeId() == null) {
            throw new ForbiddenException("Only colleges can update their profile");
        }

        College college = collegeRepository.findById(currentUser.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College profile not found for id: " + currentUser.getCollegeId()));

        college.setName(request.getName().trim());
        college.setAddress(request.getAddress());
        college.setWebsite(request.getWebsite());
        college.setContactEmail(request.getContactEmail());
        college.setContactPhone(request.getContactPhone());

        College saved = collegeRepository.save(college);
        return CollegeProfileDto.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentSummaryDto> getCollegeStudents(
            Long collegeIdOverride,
            Long departmentId,
            Short yearOfStudy,
            String search,
            int page,
            int size,
            CustomUserDetails currentUser) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeIdOverride, currentUser);

        Pageable pageable = PageRequest.of(page, size);
        String searchTrimmed = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        Page<StudentProfile> studentsPage = studentProfileRepository.findCollegeStudents(
                effectiveCollegeId, departmentId, yearOfStudy, searchTrimmed, pageable);

        List<StudentSummaryDto> content = studentsPage.getContent().stream()
                .map(this::mapToStudentSummaryDto)
                .collect(Collectors.toList());

        return PageResponse.of(content, PageMetadata.from(studentsPage));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDepartmentSummaryDto> getCollegeDepartmentBreakdown(
            Long collegeIdOverride,
            CustomUserDetails currentUser) {
        Long effectiveCollegeId = resolveEffectiveCollegeId(collegeIdOverride, currentUser);

        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(dept -> {
                    long count = studentProfileRepository.countByCollegeIdAndDepartmentId(effectiveCollegeId, dept.getId());
                    return CollegeDepartmentSummaryDto.builder()
                            .departmentId(dept.getId())
                            .departmentName(dept.getName())
                            .departmentCode(dept.getCode())
                            .studentCount((int) count)
                            .build();
                })
                .collect(Collectors.toList());
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
        throw new ForbiddenException("Only colleges and admins can view college student data");
    }

    private StudentSummaryDto mapToStudentSummaryDto(StudentProfile sp) {
        String departmentName = null;
        if (sp.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(sp.getDepartmentId()).orElse(null);
            if (dept != null) {
                departmentName = dept.getName();
            }
        }

        int skillCount = studentSkillRepository.findByStudentProfileId(sp.getId()).size();

        return StudentSummaryDto.builder()
                .id(sp.getId())
                .firstName(sp.getFirstName())
                .lastName(sp.getLastName())
                .departmentName(departmentName)
                .yearOfStudy(sp.getYearOfStudy())
                .cgpa(sp.getCgpa())
                .skillCount(skillCount)
                .build();
    }
}
