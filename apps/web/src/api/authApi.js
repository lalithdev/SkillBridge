import { USE_MOCK_API } from '@/utils/constants';
import { apiClient } from './client';
import * as mockAuth from './mock/mockAuthService';

export const authApi = {
  login: (credentials) =>
    USE_MOCK_API
      ? mockAuth.mockLogin(credentials)
      : apiClient.post('/auth/login', credentials).then((res) => res.data),

  register: (payload) =>
    USE_MOCK_API
      ? mockAuth.mockRegister(payload)
      : apiClient.post('/auth/register', payload).then((res) => res.data),

  getMe: () =>
    USE_MOCK_API
      ? mockAuth.mockGetMe()
      : apiClient.get('/auth/me').then((res) => res.data),

  logout: () =>
    USE_MOCK_API
      ? mockAuth.mockLogout()
      : apiClient.post('/auth/logout').then(() => null),
};
