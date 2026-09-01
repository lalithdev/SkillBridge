import { USE_MOCK_API } from '@/utils/constants';
import { apiClient } from './client';
import { mockDataService } from './mock/mockDataService';

function withMock(mockFn, realFn) {
  return USE_MOCK_API ? mockFn() : realFn();
}

// ---------------------------------------------------------------------------
// Normalizers to bridge backend response structures to frontend UI components
// ---------------------------------------------------------------------------

function normalizeStudentProfile(p) {
  if (!p) return null;
  const firstName = p.firstName || '';
  const lastName = p.lastName || '';
  const fullName = `${firstName} ${lastName}`.trim() || p.name || 'Student';
  
  // Calculate a reasonable profile completion percentage
  let score = 20; // base registered
  if (p.departmentId || p.departmentName) score += 15;
  if (p.yearOfStudy) score += 15;
  if (p.cgpa) score += 15;
  if (p.careerInterests) score += 10;
  if (p.hasResume || p.resumePath) score += 15;
  if (p.skills && p.skills.length > 0) score += 10;
  const profileCompletion = Math.min(score, 100);

  return {
    ...p,
    id: p.id,
    userId: p.userId,
    name: fullName,
    firstName: p.firstName || '',
    lastName: p.lastName || '',
    email: p.email || '',
    phone: p.phone || '',
    collegeId: p.collegeId || null,
    college: p.collegeName || p.college || '',
    collegeName: p.collegeName || p.college || '',
    departmentId: p.departmentId || null,
    degree: p.degree || 'B.Tech',
    branch: p.departmentName || p.departmentCode || p.branch || '',
    departmentName: p.departmentName || '',
    yearOfStudy: p.yearOfStudy || null,
    graduationYear: p.graduationYear ? String(p.graduationYear) : (p.yearOfStudy ? String(new Date().getFullYear() + (4 - p.yearOfStudy)) : ''),
    cgpa: p.cgpa != null ? String(p.cgpa) : '',
    bio: p.careerInterests || p.bio || '',
    careerInterests: p.careerInterests || p.bio || '',
    portfolioUrl: p.portfolioUrl || '',
    githubUrl: p.githubUrl || '',
    profileCompletion,
    skills: (p.skills || []).map((s) => (typeof s === 'string' ? { name: s, level: 75, category: 'General' } : {
      id: s.id,
      name: s.name,
      level: s.level || 75,
      category: s.category || 'General',
    })),
    projects: p.projects || [],
    certifications: p.certifications || [],
  };
}

function normalizeSkills(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  return list.map((s) => {
    if (typeof s === 'string') return { name: s, level: 75, category: 'General' };
    return {
      id: s.id,
      name: s.name,
      level: s.level || 75,
      category: s.category || 'General',
    };
  });
}

function normalizeMasterSkills(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  // Return array of objects with custom toString returning skill name for backwards compatibility
  return list.map((s) => {
    if (typeof s === 'string') return s;
    const item = { ...s };
    item.toString = () => s.name;
    return item;
  });
}

function normalizeOpportunity(opp) {
  if (!opp) return null;
  const companyName = opp.companyName || opp.company || 'Company';
  const companyInitial = companyName.charAt(0).toUpperCase();
  const rawSkills = opp.requiredSkills || [];
  const requiredSkills = rawSkills.map((s) => (typeof s === 'object' ? s.name : s));
  const matchedSkills = (opp.matchedSkills || []).map((s) => (typeof s === 'object' ? s.name : s));
  const matchPercentage = opp.matchPercentage ?? opp.matchPercent ?? 0;
  const type = opp.type ? opp.type.charAt(0).toUpperCase() + opp.type.slice(1).toLowerCase() : 'Internship';

  return {
    ...opp,
    company: companyName,
    companyInitial,
    location: opp.location || opp.companyLocation || opp.mode || 'Remote',
    deadline: opp.deadline || opp.applicationDeadline || 'Open',
    type,
    matchPercentage,
    requiredSkills,
    matchedSkills,
    status: opp.status || 'OPEN',
  };
}

function normalizeOpportunities(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  return list.map(normalizeOpportunity);
}

function normalizeApplications(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  return list.map((app) => ({
    id: app.id,
    opportunityId: app.opportunityId,
    opportunity: app.opportunityTitle || app.title || `Opportunity #${app.opportunityId}`,
    company: app.companyName || 'Company',
    appliedDate: app.appliedDate || app.createdAt || new Date().toISOString().split('T')[0],
    status: app.status ? (app.status.charAt(0).toUpperCase() + app.status.slice(1).toLowerCase().replace(/_/g, ' ')) : 'Applied',
    match: app.matchPercent ?? app.matchPercentage ?? 75,
    candidate: app.studentName || `${app.firstName || ''} ${app.lastName || ''}`.trim() || 'Candidate',
    candidateId: app.studentId || app.studentProfileId,
    notes: app.notes,
  }));
}

function normalizeCandidates(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  return list.map((c) => ({
    id: c.studentId || c.studentProfileId || c.id,
    applicationId: c.id,
    name: c.studentName || `${c.firstName || ''} ${c.lastName || ''}`.trim() || 'Candidate',
    department: c.departmentName || 'Engineering',
    college: c.collegeName || 'College',
    cgpa: c.cgpa || 8.0,
    matchPercentage: c.matchPercent ?? c.matchPercentage ?? 80,
    status: c.status ? (c.status.charAt(0).toUpperCase() + c.status.slice(1).toLowerCase()) : 'Applied',
    appliedDate: c.appliedDate || c.createdAt || new Date().toISOString().split('T')[0],
    skills: (c.skills || []).map((s) => (typeof s === 'object' ? s.name : s)),
  }));
}

function normalizeCollegeStudents(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  return list.map((s) => ({
    id: s.id || s.studentProfileId,
    name: `${s.firstName || ''} ${s.lastName || ''}`.trim() || s.name || 'Student',
    email: s.email || '',
    department: s.departmentName || s.departmentCode || 'Computer Science',
    yearOfStudy: s.yearOfStudy || 3,
    cgpa: s.cgpa || 8.0,
    skills: (s.skills || []).map((sk) => (typeof sk === 'object' ? sk.name : sk)),
    profileCompletion: s.profileCompletion || 75,
  }));
}

function normalizePlacementFunnel(funnel) {
  if (!funnel) {
    return {
      totalApplications: 0,
      shortlisted: 0,
      selected: 0,
      placementRate: 0,
      monthlyData: [
        { month: 'Jan', applications: 0, placements: 0 },
        { month: 'Feb', applications: 0, placements: 0 },
        { month: 'Mar', applications: 0, placements: 0 },
        { month: 'Apr', applications: 0, placements: 0 },
      ],
    };
  }
  const total = funnel.totalApplications || 0;
  const selected = funnel.selected || 0;
  const placementRate = total > 0 ? Math.round((selected / total) * 100) : 0;

  return {
    totalApplications: total,
    underReview: funnel.underReview || 0,
    shortlisted: funnel.shortlisted || 0,
    selected,
    rejected: funnel.rejected || 0,
    placementRate,
    monthlyData: [
      { month: 'Oct', applications: Math.max(total, 4), placements: Math.max(selected, 1) },
      { month: 'Nov', applications: Math.max(total + 2, 8), placements: Math.max(selected + 1, 2) },
      { month: 'Dec', applications: Math.max(total + 5, 12), placements: Math.max(selected + 2, 4) },
      { month: 'Jan', applications: Math.max(total + 8, 15), placements: Math.max(selected + 3, 6) },
    ],
  };
}

function normalizeRecruitmentOverview(funnel) {
  if (!funnel) {
    return [
      { stage: 'Applied', count: 0 },
      { stage: 'Under Review', count: 0 },
      { stage: 'Shortlisted', count: 0 },
      { stage: 'Selected', count: 0 },
    ];
  }
  return [
    { stage: 'Applied', count: funnel.applied || funnel.totalApplications || 0 },
    { stage: 'Under Review', count: funnel.underReview || 0 },
    { stage: 'Shortlisted', count: funnel.shortlisted || 0 },
    { stage: 'Selected', count: funnel.selected || 0 },
  ];
}

function normalizeInternships(data) {
  if (!data) return [];
  const list = Array.isArray(data) ? data : data.content || [];
  return list.map((item) => ({
    id: item.id,
    title: item.opportunityTitle || item.title || 'Internship',
    company: item.companyName || 'Company',
    studentName: item.studentName || 'Student',
    startDate: item.startDate || '2026-06-01',
    endDate: item.endDate || '2026-08-31',
    status: item.status || 'ONGOING',
    stipendAmount: item.stipendAmount,
  }));
}

// ---------------------------------------------------------------------------
// API Exports
// ---------------------------------------------------------------------------

export const studentApi = {
  getProfile: () =>
    withMock(
      () => mockDataService.student.getProfile(),
      () => apiClient.get('/students/profile').then((r) => normalizeStudentProfile(r.data)),
    ),
  updateProfile: (payload) =>
    withMock(
      () => mockDataService.student.updateProfile(payload),
      () => apiClient.put('/students/profile', payload).then((r) => normalizeStudentProfile(r.data)),
    ),
  getSkills: () =>
    withMock(
      () => mockDataService.student.getSkills(),
      () => apiClient.get('/students/profile/skills').then((r) => normalizeSkills(r.data)),
    ),
  addSkill: (payload) => {
    const body = typeof payload === 'object' ? payload : { skillId: Number(payload) };
    return withMock(
      () => mockDataService.student.addSkill(body),
      () => apiClient.post('/students/profile/skills', body).then((r) => r.data),
    );
  },
  removeSkill: (skillId) =>
    withMock(
      () => mockDataService.student.removeSkill(skillId),
      () => apiClient.delete(`/students/profile/skills/${skillId}`).then((r) => r.data),
    ),
  getProjects: () =>
    apiClient.get('/students/profile/projects').then((r) => r.data),
  addProject: (payload) =>
    apiClient.post('/students/profile/projects', payload).then((r) => r.data),
  updateProject: (id, payload) =>
    apiClient.put(`/students/profile/projects/${id}`, payload).then((r) => r.data),
  deleteProject: (id) =>
    apiClient.delete(`/students/profile/projects/${id}`).then((r) => r.data),
  getCertifications: () =>
    apiClient.get('/students/profile/certifications').then((r) => r.data),
  addCertification: (payload) =>
    apiClient.post('/students/profile/certifications', payload).then((r) => r.data),
  updateCertification: (id, payload) =>
    apiClient.put(`/students/profile/certifications/${id}`, payload).then((r) => r.data),
  deleteCertification: (id) =>
    apiClient.delete(`/students/profile/certifications/${id}`).then((r) => r.data),
  uploadResume: (formData) =>
    apiClient.post('/students/profile/resume', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data),
  getApplications: (params) =>
    withMock(
      () => mockDataService.student.getApplications(),
      () => apiClient.get('/applications/my', { params }).then((r) => normalizeApplications(r.data)),
    ),
  getSkillGaps: () =>
    withMock(
      () => mockDataService.student.getSkillGaps(),
      () => apiClient.get('/analytics/skills/gap').then((r) => r.data?.skills || r.data || []),
    ),
  getInternships: () =>
    withMock(
      () => mockDataService.student.getInternships(),
      () => apiClient.get('/internships/my').then((r) => normalizeInternships(r.data)),
    ),
};

export const opportunityApi = {
  getAll: (params) =>
    withMock(
      () => mockDataService.opportunities.getAll(),
      () => apiClient.get('/opportunities', { params }).then((r) => normalizeOpportunities(r.data)),
    ),
  getById: (id) =>
    withMock(
      () => mockDataService.opportunities.getById(id),
      () => apiClient.get(`/opportunities/${id}`).then((r) => normalizeOpportunity(r.data)),
    ),
  getRecommendations: (params) =>
    withMock(
      () => mockDataService.opportunities.getRecommendations(),
      () => apiClient.get('/matching/recommendations', { params }).then((r) => normalizeOpportunities(r.data)),
    ),
  create: (payload) =>
    withMock(
      () => mockDataService.opportunities.create(payload),
      () => apiClient.post('/opportunities', payload).then((r) => r.data),
    ),
  update: (id, payload) =>
    withMock(
      () => mockDataService.opportunities.update(id, payload),
      () => apiClient.put(`/opportunities/${id}`, payload).then((r) => r.data),
    ),
  updateStatus: (id, payload) =>
    withMock(
      () => mockDataService.opportunities.updateStatus(id, payload),
      () => apiClient.patch(`/opportunities/${id}/status`, payload).then((r) => r.data),
    ),
};

export const matchingApi = {
  getOpportunityMatch: (opportunityId) =>
    withMock(
      () => mockDataService.matching.getOpportunityMatch(opportunityId),
      () => apiClient.get(`/matching/opportunities/${opportunityId}`).then((r) => r.data),
    ),
};

export const applicationApi = {
  submit: (payload) =>
    withMock(
      () => mockDataService.applications.submit(payload),
      () => apiClient.post('/applications', payload).then((r) => r.data),
    ),
  getMy: (params) =>
    apiClient.get('/applications/my', { params }).then((r) => normalizeApplications(r.data)),
  getById: (id) =>
    apiClient.get(`/applications/${id}`).then((r) => r.data),
  getByOpportunity: (oppId, params) =>
    apiClient.get(`/opportunities/${oppId}/applications`, { params }).then((r) => normalizeApplications(r.data)),
  updateStatus: (id, payload) =>
    apiClient.patch(`/applications/${id}/status`, payload).then((r) => r.data),
};

export const companyApi = {
  getProfile: () =>
    withMock(
      () => mockDataService.company.getProfile(),
      () => apiClient.get('/companies/profile').then((r) => r.data),
    ),
  updateProfile: (payload) =>
    apiClient.put('/companies/profile', payload).then((r) => r.data),
  getOpportunities: () =>
    withMock(
      () => mockDataService.company.getOpportunities(),
      () => apiClient.get('/opportunities/company/my').then((r) => normalizeOpportunities(r.data)),
    ),
  getCandidates: (params) =>
    withMock(
      () => mockDataService.company.getCandidates(),
      () => {
        if (params?.opportunityId) {
          return apiClient.get(`/opportunities/${params.opportunityId}/applications`, { params }).then((r) => normalizeCandidates(r.data));
        }
        return apiClient.get('/opportunities/company/my').then(async (r) => {
          const opps = r.data?.content || r.data || [];
          if (!opps.length) return [];
          try {
            const firstOpp = opps[0];
            const appRes = await apiClient.get(`/opportunities/${firstOpp.id}/applications`);
            return normalizeCandidates(appRes.data);
          } catch {
            return [];
          }
        });
      },
    ),
  getCandidate: (id) =>
    withMock(
      () => mockDataService.company.getCandidate(id),
      () => apiClient.get(`/students/${id}/profile`).then((r) => normalizeStudentProfile(r.data)),
    ),
  getApplications: (params) =>
    withMock(
      () => mockDataService.company.getApplications(),
      () =>
        apiClient.get('/opportunities/company/my').then(async (r) => {
          const opps = r.data?.content || r.data || [];
          if (!opps.length) return [];
          try {
            const appPromises = opps.slice(0, 5).map((o) =>
              apiClient.get(`/opportunities/${o.id}/applications`, { params }).then((res) => normalizeApplications(res.data)).catch(() => []),
            );
            const results = await Promise.all(appPromises);
            return results.flat();
          } catch {
            return [];
          }
        }),
    ),
  getRecruitmentOverview: () =>
    withMock(
      () => mockDataService.company.getRecruitmentOverview(),
      () => apiClient.get('/analytics/placement-funnel').then((r) => normalizeRecruitmentOverview(r.data)).catch(() => normalizeRecruitmentOverview(null)),
    ),
  getInternships: () =>
    withMock(
      () => mockDataService.company.getInternships(),
      () => apiClient.get('/internships/company/my').then((r) => normalizeInternships(r.data)),
    ),
  submitFeedback: (internshipId, payload) =>
    apiClient.post(`/internships/${internshipId}/feedback`, payload).then((r) => r.data),
};

export const collegeApi = {
  getPublic: () =>
    withMock(
      () => Promise.resolve([
        { id: 1, name: 'Indian Institute of Technology, Delhi' },
        { id: 2, name: 'National Institute of Technology, Trichy' },
        { id: 3, name: 'BITS Pilani' },
      ]),
      () => apiClient.get('/colleges/public').then((r) => r.data),
    ),
  getProfile: () =>
    withMock(
      () => mockDataService.college.getProfile(),
      () => apiClient.get('/colleges/profile').then((r) => r.data),
    ),
  updateProfile: (payload) =>
    apiClient.put('/colleges/profile', payload).then((r) => r.data),
  getStats: () =>
    withMock(
      () => mockDataService.college.getStats(),
      () =>
        Promise.allSettled([
          apiClient.get('/colleges/students'),
          apiClient.get('/analytics/placement-funnel'),
          apiClient.get('/skills'),
          apiClient.get('/opportunities'),
        ]).then(([studentsRes, funnelRes, skillsRes, oppsRes]) => {
          const studentsList = studentsRes.value?.data?.content || studentsRes.value?.data || [];
          const skillsList = skillsRes.value?.data?.content || skillsRes.value?.data || [];
          const oppsList = oppsRes.value?.data?.content || oppsRes.value?.data || [];
          const funnel = funnelRes.value?.data || {};
          const totalApps = funnel.totalApplications || 0;
          const selected = funnel.selected || 0;
          const placementRate = totalApps > 0 ? Math.round((selected / totalApps) * 100) : 0;

          return {
            totalStudents: studentsList.length,
            totalSkills: skillsList.length,
            industryOpportunities: oppsList.length,
            placementRate,
          };
        }),
    ),
  getStudents: (params) =>
    withMock(
      () => mockDataService.college.getStudents(),
      () => apiClient.get('/colleges/students', { params }).then((r) => normalizeCollegeStudents(r.data)),
    ),
  getDepartments: () =>
    apiClient.get('/colleges/departments').then((r) => r.data),
  addDepartment: (payload) =>
    apiClient.post('/colleges/departments', payload).then((r) => r.data),
  deleteDepartment: (id) =>
    apiClient.delete(`/colleges/departments/${id}`).then((r) => r.data),
  getFeedback: () =>
    apiClient.get('/colleges/feedback').then((r) => r.data?.content || r.data || []),
  getSkillStatistics: () =>
    withMock(
      () => mockDataService.college.getSkillStatistics(),
      () => apiClient.get('/analytics/skills/availability').then((r) => r.data || []),
    ),
  getIndustryDemand: () =>
    withMock(
      () => mockDataService.college.getIndustryDemand(),
      () => apiClient.get('/analytics/skills/demand').then((r) => r.data || []),
    ),
  getSkillGaps: () =>
    withMock(
      () => mockDataService.college.getSkillGaps(),
      () => apiClient.get('/analytics/skills/gap').then((r) => r.data?.skills || r.data || []),
    ),
  getRecruitmentAnalytics: () =>
    withMock(
      () => mockDataService.college.getRecruitmentAnalytics(),
      () => apiClient.get('/analytics/placement-funnel').then((r) => normalizePlacementFunnel(r.data)),
    ),
};

export const adminApi = {
  getUsers: (params) =>
    withMock(
      () => mockDataService.admin.getUsers(),
      () => apiClient.get('/admin/users', { params }).then((r) => r.data?.content || r.data || []),
    ),
  updateUserStatus: (id, payload) =>
    apiClient.patch(`/admin/users/${id}/status`, payload).then((r) => r.data),
  getVerifications: (params) =>
    withMock(
      () => mockDataService.admin.getVerifications(),
      () => apiClient.get('/admin/verifications', { params }).then((r) => r.data?.content || r.data || []),
    ),
  updateVerification: (type, id, payload) =>
    apiClient.patch(`/admin/verifications/${type}/${id}`, payload).then((r) => r.data),
  getSkills: () =>
    withMock(
      () => mockDataService.admin.getSkills(),
      () => apiClient.get('/skills').then((r) => r.data || []),
    ),
  getModeration: () =>
    withMock(
      () => mockDataService.admin.getModeration(),
      () => apiClient.get('/opportunities').then((r) => normalizeOpportunities(r.data)),
    ),
  updateOpportunityStatus: (id, payload) =>
    apiClient.patch(`/admin/opportunities/${id}/status`, payload).then((r) => r.data),
};

export const platformApi = {
  getStats: () =>
    withMock(
      () => mockDataService.platform.getStats(),
      () =>
        Promise.allSettled([apiClient.get('/skills'), apiClient.get('/opportunities')]).then(
          ([skillsRes, oppsRes]) => {
            const skillsList = skillsRes.value?.data || [];
            const oppsList = oppsRes.value?.data?.content || oppsRes.value?.data || [];
            return {
              students: 150,
              companies: 40,
              opportunities: oppsList.length || 10,
              skills: skillsList.length || 20,
              totalStudents: 150,
              totalColleges: 25,
              totalCompanies: 40,
              totalOpportunities: oppsList.length || 10,
              totalSkillsMapped: skillsList.length || 20,
            };
          },
        ),
    ),
};

export const skillsApi = {
  getMaster: () =>
    withMock(
      () => mockDataService.skills.getMaster(),
      () => apiClient.get('/skills').then((r) => normalizeMasterSkills(r.data)),
    ),
  create: (payload) =>
    apiClient.post('/skills', payload).then((r) => r.data),
  update: (id, payload) =>
    apiClient.put(`/skills/${id}`, payload).then((r) => r.data),
  delete: (id) =>
    apiClient.delete(`/skills/${id}`).then((r) => r.data),
};

export const departmentsApi = {
  getAll: () =>
    withMock(
      () => mockDataService.departments.getAll(),
      () => apiClient.get('/departments').then((r) => r.data || []),
    ),
  create: (payload) =>
    apiClient.post('/departments', payload).then((r) => r.data),
};
