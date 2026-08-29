package com.skillbridge.common.security;

import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CollegeRepository collegeRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            CompanyProfileRepository companyProfileRepository,
            CollegeRepository collegeRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.collegeRepository = collegeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Long studentProfileId = null;
        Long companyProfileId = null;
        Long collegeId = null;

        switch (user.getRole()) {
            case STUDENT -> {
                var studentOpt = studentProfileRepository.findByUserId(user.getId());
                if (studentOpt.isPresent()) {
                    studentProfileId = studentOpt.get().getId();
                    collegeId = studentOpt.get().getCollegeId();
                }
            }
            case COMPANY -> {
                var companyOpt = companyProfileRepository.findByUserId(user.getId());
                if (companyOpt.isPresent()) {
                    companyProfileId = companyOpt.get().getId();
                }
            }
            case COLLEGE -> {
                var collegeOpt = collegeRepository.findByUserId(user.getId());
                if (collegeOpt.isPresent()) {
                    collegeId = collegeOpt.get().getId();
                }
            }
            case ADMIN -> {}
        }

        return CustomUserDetails.create(user, collegeId, companyProfileId, studentProfileId);
    }
}

