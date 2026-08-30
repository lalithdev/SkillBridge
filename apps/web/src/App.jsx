import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from '@/context/AuthContext';
import { ToastProvider } from '@/components/ui/Toast';
import ProtectedRoute from '@/routes/ProtectedRoute';
import DashboardLayout from '@/layouts/DashboardLayout';
import { studentNav, companyNav, collegeNav, adminNav } from '@/layouts/navConfig';

import Landing from '@/pages/Landing';
import Unauthorized from '@/pages/Unauthorized';
import NotFound from '@/pages/NotFound';
import Login from '@/pages/auth/Login';
import Register from '@/pages/auth/Register';

import StudentDashboard from '@/pages/student/StudentDashboard';
import StudentProfile from '@/pages/student/StudentProfile';
import StudentSkills from '@/pages/student/StudentSkills';
import Opportunities from '@/pages/student/Opportunities';
import OpportunityDetails from '@/pages/student/OpportunityDetails';
import Recommendations from '@/pages/student/Recommendations';
import Applications from '@/pages/student/Applications';
import SkillGaps from '@/pages/student/SkillGaps';
import StudentInternships from '@/pages/student/StudentInternships';
import StudentSettings from '@/pages/student/StudentSettings';

import CompanyDashboard from '@/pages/company/CompanyDashboard';
import CompanyProfile from '@/pages/company/CompanyProfile';
import CompanyOpportunities from '@/pages/company/CompanyOpportunities';
import CreateOpportunity from '@/pages/company/CreateOpportunity';
import Candidates from '@/pages/company/Candidates';
import CandidateProfile from '@/pages/company/CandidateProfile';
import CompanyApplications from '@/pages/company/CompanyApplications';
import CompanyInternships from '@/pages/company/CompanyInternships';
import CompanySettings from '@/pages/company/CompanySettings';

import CollegeDashboard from '@/pages/college/CollegeDashboard';
import CollegeProfile from '@/pages/college/CollegeProfile';
import CollegeStudents from '@/pages/college/CollegeStudents';
import SkillStatistics from '@/pages/college/SkillStatistics';
import IndustryDemand from '@/pages/college/IndustryDemand';
import SkillGapAnalysis from '@/pages/college/SkillGapAnalysis';
import RecruitmentAnalytics from '@/pages/college/RecruitmentAnalytics';
import CollegeSettings from '@/pages/college/CollegeSettings';

import AdminDashboard from '@/pages/admin/AdminDashboard';
import AdminUsers from '@/pages/admin/AdminUsers';
import AdminVerifications from '@/pages/admin/AdminVerifications';
import AdminSkills from '@/pages/admin/AdminSkills';
import AdminModeration from '@/pages/admin/AdminModeration';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 2,
      gcTime: 1000 * 60 * 10,
      retry: (failureCount, error) => {
        if ([401, 403, 404].includes(error?.status)) return false;
        return failureCount < 2;
      },
      refetchOnWindowFocus: false,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <ToastProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<Landing />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/unauthorized" element={<Unauthorized />} />

              <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
                <Route element={<DashboardLayout navItems={studentNav} roleLabel="Student" />}>
                  <Route path="/student/dashboard" element={<StudentDashboard />} />
                  <Route path="/student/profile" element={<StudentProfile />} />
                  <Route path="/student/skills" element={<StudentSkills />} />
                  <Route path="/student/opportunities" element={<Opportunities />} />
                  <Route path="/student/opportunities/:id" element={<OpportunityDetails />} />
                  <Route path="/student/recommendations" element={<Recommendations />} />
                  <Route path="/student/applications" element={<Applications />} />
                  <Route path="/student/skill-gaps" element={<SkillGaps />} />
                  <Route path="/student/internships" element={<StudentInternships />} />
                  <Route path="/student/settings" element={<StudentSettings />} />
                </Route>
              </Route>

              <Route element={<ProtectedRoute allowedRoles={['COMPANY']} />}>
                <Route element={<DashboardLayout navItems={companyNav} roleLabel="Company" />}>
                  <Route path="/company/dashboard" element={<CompanyDashboard />} />
                  <Route path="/company/profile" element={<CompanyProfile />} />
                  <Route path="/company/opportunities" element={<CompanyOpportunities />} />
                  <Route path="/company/opportunities/create" element={<CreateOpportunity />} />
                  <Route path="/company/candidates" element={<Candidates />} />
                  <Route path="/company/candidates/:id" element={<CandidateProfile />} />
                  <Route path="/company/applications" element={<CompanyApplications />} />
                  <Route path="/company/internships" element={<CompanyInternships />} />
                  <Route path="/company/settings" element={<CompanySettings />} />
                </Route>
              </Route>

              <Route element={<ProtectedRoute allowedRoles={['COLLEGE']} />}>
                <Route element={<DashboardLayout navItems={collegeNav} roleLabel="College" />}>
                  <Route path="/college/dashboard" element={<CollegeDashboard />} />
                  <Route path="/college/profile" element={<CollegeProfile />} />
                  <Route path="/college/students" element={<CollegeStudents />} />
                  <Route path="/college/skills" element={<SkillStatistics />} />
                  <Route path="/college/industry-demand" element={<IndustryDemand />} />
                  <Route path="/college/skill-gaps" element={<SkillGapAnalysis />} />
                  <Route path="/college/recruitment" element={<RecruitmentAnalytics />} />
                  <Route path="/college/settings" element={<CollegeSettings />} />
                </Route>
              </Route>

              <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                <Route element={<DashboardLayout navItems={adminNav} roleLabel="Admin" />}>
                  <Route path="/admin/dashboard" element={<AdminDashboard />} />
                  <Route path="/admin/users" element={<AdminUsers />} />
                  <Route path="/admin/verifications" element={<AdminVerifications />} />
                  <Route path="/admin/skills" element={<AdminSkills />} />
                  <Route path="/admin/opportunities" element={<AdminModeration />} />
                </Route>
              </Route>

              <Route path="*" element={<NotFound />} />
            </Routes>
          </BrowserRouter>
        </ToastProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}
