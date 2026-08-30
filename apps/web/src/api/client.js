import axios from 'axios';
import { API_BASE_URL, STORAGE_KEYS } from '@/utils/constants';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  timeout: 15000,
});

apiClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem(STORAGE_KEYS.JWT);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status ?? null;

    if (status === 401) {
      sessionStorage.removeItem(STORAGE_KEYS.JWT);
      sessionStorage.removeItem(STORAGE_KEYS.USER);
      if (window.location.pathname !== '/login') {
        const redirect = encodeURIComponent(window.location.pathname);
        window.location.href = `/login?expired=true&redirect=${redirect}`;
      }
    }

    const standardError = {
      status: status || 500,
      message: error.response?.data?.message || error.message || 'An unexpected error occurred',
      fieldErrors: error.response?.data?.fieldErrors || {},
      raw: error,
    };

    return Promise.reject(standardError);
  },
);
