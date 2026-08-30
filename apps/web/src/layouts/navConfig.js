import {
  LayoutDashboard, User, Award, Briefcase, Sparkles, FileText,
  Target, Settings, LogOut, Building2, Users, BarChart3,
  TrendingUp, PieChart, UserPlus, ClipboardList, GraduationCap,
  Shield,
} from 'lucide-react';

export const studentNav = [
  { label: 'Dashboard', path: '/student/dashboard', icon: LayoutDashboard },
  { label: 'My Profile', path: '/student/profile', icon: User },
  { label: 'My Skills', path: '/student/skills', icon: Award },
  { label: 'Opportunities', path: '/student/opportunities', icon: Briefcase },
  { label: 'Recommendations', path: '/student/recommendations', icon: Sparkles },
  { label: 'Applications', path: '/student/applications', icon: FileText },
  { label: 'Internships', path: '/student/internships', icon: GraduationCap },
  { label: 'Skill Gaps', path: '/student/skill-gaps', icon: Target },
  { label: 'Settings', path: '/student/settings', icon: Settings },
  { label: 'Logout', path: '', icon: LogOut, action: 'logout' },
];

export const companyNav = [
  { label: 'Dashboard', path: '/company/dashboard', icon: LayoutDashboard },
  { label: 'Company Profile', path: '/company/profile', icon: Building2 },
  { label: 'Opportunities', path: '/company/opportunities', icon: Briefcase },
  { label: 'Create Opportunity', path: '/company/opportunities/create', icon: UserPlus },
  { label: 'Candidates', path: '/company/candidates', icon: Users },
  { label: 'Applications', path: '/company/applications', icon: ClipboardList },
  { label: 'Internships', path: '/company/internships', icon: GraduationCap },
  { label: 'Settings', path: '/company/settings', icon: Settings },
  { label: 'Logout', path: '', icon: LogOut, action: 'logout' },
];

export const collegeNav = [
  { label: 'Dashboard', path: '/college/dashboard', icon: LayoutDashboard },
  { label: 'College Profile', path: '/college/profile', icon: User },
  { label: 'Students', path: '/college/students', icon: Users },
  { label: 'Skill Statistics', path: '/college/skills', icon: BarChart3 },
  { label: 'Industry Demand', path: '/college/industry-demand', icon: TrendingUp },
  { label: 'Skill Gap Analysis', path: '/college/skill-gaps', icon: Target },
  { label: 'Recruitment Analytics', path: '/college/recruitment', icon: PieChart },
  { label: 'Settings', path: '/college/settings', icon: Settings },
  { label: 'Logout', path: '', icon: LogOut, action: 'logout' },
];

export const adminNav = [
  { label: 'Dashboard', path: '/admin/dashboard', icon: LayoutDashboard },
  { label: 'Users', path: '/admin/users', icon: Users },
  { label: 'Verifications', path: '/admin/verifications', icon: Shield },
  { label: 'Skills Taxonomy', path: '/admin/skills', icon: GraduationCap },
  { label: 'Opportunity Moderation', path: '/admin/opportunities', icon: Briefcase },
  { label: 'Logout', path: '', icon: LogOut, action: 'logout' },
];
