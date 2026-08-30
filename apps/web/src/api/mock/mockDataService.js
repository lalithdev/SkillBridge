/**
 * TEMPORARY mock data fetchers for pre-backend development.
 * All demo data is sourced from src/data/mockData.js and returned with simulated latency.
 */
import {
  platformStats,
  studentProfile,
  studentSkills,
  industryDemandedSkills,
  skillGapAnalysis,
  opportunities,
  studentApplications,
  companyProfile,
  companyOpportunities,
  candidates,
  candidateProfile,
  companyApplications,
  recruitmentOverview,
  collegeStudents,
  collegeStats,
  skillDemandByIndustry,
  skillGapAnalytics,
  recruitmentAnalytics,
  allSkills,
  studentInternships,
  companyInternships,
} from '@/data/mockData';
import { mockDelay, mockReject } from './mockDelay';

function buildMatchFromOpportunity(opp) {
  if (!opp) return null;
  return {
    matchedSkills: (opp.matchedSkills || []).map((name) => ({ name })),
    missingSkills: (opp.missingSkills || []).map((name) => ({ name })),
    matchPercent: opp.matchPercentage ?? 0,
    isEligible: opp.isEligible ?? true,
    ineligibilityReasons: opp.ineligibilityReasons || [],
  };
}

export const mockDataService = {
  platform: {
    getStats: () => mockDelay(platformStats),
  },
  student: {
    getProfile: () => mockDelay(studentProfile),
    getSkills: () => mockDelay(studentSkills),
    getSkillGaps: () => mockDelay(skillGapAnalysis),
    getApplications: () => mockDelay(studentApplications),
    getInternships: () => mockDelay(studentInternships),
  },
  opportunities: {
    getAll: () => mockDelay(opportunities),
    getById: (id) => {
      const item = opportunities.find((o) => o.id === id);
      if (!item) return Promise.reject({ status: 404, message: 'Opportunity not found.' });
      return mockDelay(item);
    },
    getRecommendations: () => mockDelay([...opportunities].sort((a, b) => b.matchPercentage - a.matchPercentage)),
  },
  company: {
    getProfile: () => mockDelay(companyProfile),
    getOpportunities: () => mockDelay(companyOpportunities),
    getCandidates: () => mockDelay(candidates),
    getCandidate: (id) => {
      if (id === candidateProfile.id) return mockDelay(candidateProfile);
      const item = candidates.find((c) => c.id === id);
      if (!item) return Promise.reject({ status: 404, message: 'Candidate not found.' });
      return mockDelay({ ...candidateProfile, ...item, id });
    },
    getApplications: () => mockDelay(companyApplications),
    getRecruitmentOverview: () => mockDelay(recruitmentOverview),
    getInternships: () => mockDelay(companyInternships),
  },
  college: {
    getProfile: () => mockDelay({
      id: 'clg-001',
      name: 'Indian Institute of Technology, Delhi',
      email: 'placement@iitd.ac.in',
      website: 'https://iitd.ac.in',
      phone: '+91 11 2659 1000',
      location: 'New Delhi, India',
      description: 'A premier engineering institution focused on research, innovation, and strong industry collaborations for student employability.',
      isVerified: true,
      studentCount: 12480,
    }),
    getStats: () => mockDelay(collegeStats),
    getStudents: () => mockDelay(collegeStudents),
    getSkillStatistics: () => mockDelay({ studentSkills, industryDemandedSkills }),
    getIndustryDemand: () => mockDelay({ industryDemandedSkills, skillDemandByIndustry }),
    getSkillGaps: () => mockDelay(skillGapAnalytics),
    getRecruitmentAnalytics: () => mockDelay(recruitmentAnalytics),
  },
  admin: {
    getUsers: () => mockDelay([
      { id: 'user-1', name: 'Arjun Sharma', email: 'arjun.sharma@example.com', role: 'STUDENT', isActive: true },
      { id: 'user-2', name: 'TechVista Solutions', email: 'hr@techvista.com', role: 'COMPANY', isActive: true },
      { id: 'user-3', name: 'IIT Delhi', email: 'placement@iitd.ac.in', role: 'COLLEGE', isActive: true },
      { id: 'user-4', name: 'Ayesha Khan', email: 'ayesha.khan@example.com', role: 'STUDENT', isActive: false },
    ]),
    getVerifications: () => mockDelay([
      { id: 'verify-1', entity: 'IIT Delhi', details: 'Institution profile verification pending review', status: 'pending' },
      { id: 'verify-2', entity: 'Aarav Mehta', details: 'Resume and profile verification submitted', status: 'approved' },
      { id: 'verify-3', entity: 'Nova Labs', details: 'Company verification documents under review', status: 'rejected' },
    ]),
    getSkills: () => mockDelay([
      { id: 'skill-1', name: 'Java', category: 'Programming', description: 'Core backend programming language for enterprise systems.', isActive: true },
      { id: 'skill-2', name: 'React', category: 'Frontend', description: 'Component-driven UI library for interactive web interfaces.', isActive: true },
      { id: 'skill-3', name: 'AWS', category: 'Cloud', description: 'Cloud computing platform for scalable deployments.', isActive: true },
      { id: 'skill-4', name: 'Machine Learning', category: 'AI/ML', description: 'Model training and data science workflows.', isActive: false },
    ]),
    getModeration: () => mockDelay([
      { id: 'mod-1', title: 'Backend Engineering Intern', company: 'TechVista', location: 'Bangalore', status: 'approved' },
      { id: 'mod-2', title: 'AI Research Associate', company: 'Nexa Labs', location: 'Remote', status: 'flagged' },
      { id: 'mod-3', title: 'Data Analyst', company: 'Mosaic Analytics', location: 'Pune', status: 'needs-review' },
    ]),
  },
  skills: {
    getMaster: () => mockDelay(allSkills),
  },
};
