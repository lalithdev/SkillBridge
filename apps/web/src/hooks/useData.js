import { useQuery } from '@tanstack/react-query';
import { queryKeys } from '@/utils/constants';
import { studentApi, opportunityApi, companyApi, collegeApi, platformApi, skillsApi, adminApi } from '@/api';

export function usePlatformStats() {
  return useQuery({ queryKey: queryKeys.platform.stats, queryFn: platformApi.getStats });
}

export function useStudentProfile() {
  return useQuery({ queryKey: queryKeys.students.profile, queryFn: studentApi.getProfile });
}

export function useStudentSkills() {
  return useQuery({ queryKey: queryKeys.students.skills, queryFn: studentApi.getSkills });
}

export function useStudentApplications(status) {
  return useQuery({
    queryKey: queryKeys.students.applications(status),
    queryFn: () => studentApi.getApplications({ status }),
  });
}

export function useStudentSkillGaps() {
  return useQuery({ queryKey: queryKeys.students.skillGaps, queryFn: studentApi.getSkillGaps });
}

export function useStudentInternships() {
  return useQuery({ queryKey: queryKeys.students.internships, queryFn: studentApi.getInternships });
}

export function useOpportunities(filters = {}) {
  return useQuery({
    queryKey: queryKeys.opportunities.all(filters),
    queryFn: () => opportunityApi.getAll(filters),
  });
}

export function useOpportunity(id) {
  return useQuery({
    queryKey: queryKeys.opportunities.detail(id),
    queryFn: () => opportunityApi.getById(id),
    enabled: Boolean(id),
  });
}

export function useRecommendations() {
  return useQuery({ queryKey: queryKeys.opportunities.recommendations, queryFn: opportunityApi.getRecommendations });
}

export function useCompanyProfile() {
  return useQuery({ queryKey: queryKeys.company.profile, queryFn: companyApi.getProfile });
}

export function useCompanyOpportunities() {
  return useQuery({ queryKey: queryKeys.company.opportunities, queryFn: companyApi.getOpportunities });
}

export function useCandidates(filters = {}) {
  return useQuery({
    queryKey: queryKeys.company.candidates(filters),
    queryFn: () => companyApi.getCandidates(filters),
  });
}

export function useCandidate(id) {
  return useQuery({
    queryKey: queryKeys.company.candidate(id),
    queryFn: () => companyApi.getCandidate(id),
    enabled: Boolean(id),
  });
}

export function useCompanyApplications(status) {
  return useQuery({
    queryKey: queryKeys.company.applications(status),
    queryFn: () => companyApi.getApplications({ status }),
  });
}

export function useCompanyRecruitmentOverview() {
  return useQuery({
    queryKey: ['company', 'recruitmentOverview'],
    queryFn: companyApi.getRecruitmentOverview,
  });
}

export function useCompanyInternships() {
  return useQuery({ queryKey: queryKeys.company.internships, queryFn: companyApi.getInternships });
}

export function useCollegeProfile() {
  return useQuery({ queryKey: queryKeys.college.profile, queryFn: collegeApi.getProfile });
}

export function useCollegeStats() {
  return useQuery({ queryKey: queryKeys.college.stats, queryFn: collegeApi.getStats });
}

export function useCollegeStudents(filters = {}) {
  return useQuery({
    queryKey: queryKeys.college.students(filters),
    queryFn: () => collegeApi.getStudents(filters),
  });
}

export function useSkillStatistics() {
  return useQuery({ queryKey: queryKeys.college.skillStatistics, queryFn: collegeApi.getSkillStatistics });
}

export function useIndustryDemand() {
  return useQuery({ queryKey: queryKeys.college.industryDemand, queryFn: collegeApi.getIndustryDemand });
}

export function useCollegeSkillGaps() {
  return useQuery({ queryKey: queryKeys.college.skillGaps, queryFn: collegeApi.getSkillGaps });
}

export function useRecruitmentAnalytics() {
  return useQuery({ queryKey: queryKeys.college.recruitment, queryFn: collegeApi.getRecruitmentAnalytics });
}

export function useAdminUsers() {
  return useQuery({ queryKey: queryKeys.admin.users, queryFn: adminApi.getUsers });
}

export function useAdminVerifications() {
  return useQuery({ queryKey: queryKeys.admin.verifications, queryFn: adminApi.getVerifications });
}

export function useAdminSkills() {
  return useQuery({ queryKey: queryKeys.admin.skills, queryFn: adminApi.getSkills });
}

export function useAdminModeration() {
  return useQuery({ queryKey: queryKeys.admin.opportunities, queryFn: adminApi.getModeration });
}

export function useMasterSkills() {
  return useQuery({ queryKey: queryKeys.skills.master, queryFn: skillsApi.getMaster });
}
