import axios from 'axios';

// API URLs from environment variables
export const API_URLS = {
  AUTH: import.meta.env.VITE_AUTH_API_URL || 'http://localhost:8081',
  IAM: import.meta.env.VITE_IAM_API_URL || 'http://localhost:8082',
  ENROLLMENT: import.meta.env.VITE_ENROLLMENT_API_URL || 'http://localhost:8083',
};

// Create axios instances for each service
export const authApi = axios.create({
  baseURL: API_URLS.AUTH,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const iamApi = axios.create({
  baseURL: API_URLS.IAM,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const enrollmentApi = axios.create({
  baseURL: API_URLS.ENROLLMENT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
const addAuthInterceptor = (apiInstance: any) => {
  apiInstance.interceptors.request.use((config: any) => {
    const token = getAuthToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });
};

// Response interceptor for error handling
const addErrorInterceptor = (apiInstance: any) => {
  apiInstance.interceptors.response.use(
    (response: any) => response,
    (error: any) => {
      if (error.response?.status === 401) {
        // Token expired or invalid
        clearAuthToken();
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );
};

// Token management (stored in memory only)
let authToken: string | null = null;

export const setAuthToken = (token: string) => {
  authToken = token;
};

export const getAuthToken = (): string | null => {
  return authToken;
};

export const clearAuthToken = () => {
  authToken = null;
};

// Apply interceptors to API instances
addAuthInterceptor(authApi);
addAuthInterceptor(iamApi);
addAuthInterceptor(enrollmentApi);
addErrorInterceptor(authApi);
addErrorInterceptor(iamApi);
addErrorInterceptor(enrollmentApi);

export default { authApi, iamApi, enrollmentApi };
