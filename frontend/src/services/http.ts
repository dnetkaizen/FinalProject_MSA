import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
  type AxiosError,
} from 'axios';
import { AUTH_API_URL, IAM_API_URL, ENROLLMENT_API_URL } from '../config/env';

interface ApiResponse<T = any> {
  data: T;
  status: number;
  statusText: string;
  headers: any;
  config: AxiosRequestConfig;
}

interface ApiError extends Error {
  status?: number;
  code?: string;
  response?: {
    data?: any;
    status: number;
    statusText: string;
    headers: any;
  };
  config: AxiosRequestConfig;
  isAxiosError: boolean;
}

// Create a map to store API instances
const httpInstances = new Map<string, AxiosInstance>();

// Token management (in-memory)
let authToken: string | null = null;

/**
 * Set the authentication token
 */
const setAuthToken = (token: string | null): void => {
  authToken = token;
};

/**
 * Get the current authentication token
 */
const getAuthToken = (): string | null => {
  return authToken;
};

/**
 * Create a configured HTTP client instance
 * @param baseURL - Base URL for the API
 * @returns Configured Axios instance
 */
const createHttpClient = (baseURL: string): AxiosInstance => {
  // Return existing instance if it exists
  const existingInstance = httpInstances.get(baseURL);
  if (existingInstance) {
    return existingInstance;
  }

  // Create new instance with default config
  const instance = axios.create({
    baseURL,
    timeout: 30000, // 30 seconds
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
    withCredentials: true,
  });

  // Request interceptor for auth token
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = getAuthToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response interceptor for error handling
  instance.interceptors.response.use(
    (response: AxiosResponse) => response,
    (error: AxiosError) => {
      const errorResponse = error.response || {} as any;
      const responseData = errorResponse.data || {};
      const errorMessage = 
        responseData.message ||
        error.message ||
        'An unexpected error occurred';
      
      const normalizedError: ApiError = {
        name: 'ApiError',
        message: errorMessage,
        status: errorResponse.status,
        code: error.code,
        response: {
          data: responseData,
          status: errorResponse.status || 0,
          statusText: errorResponse.statusText || '',
          headers: errorResponse.headers || {},
        },
        config: error.config || {} as AxiosRequestConfig,
        isAxiosError: true,
      };

      // Handle specific status codes
      if (errorResponse.status === 401) {
        console.warn('Authentication required');
      } else if (errorResponse.status === 403) {
        console.warn('Forbidden: You do not have permission to access this resource');
      } else if (errorResponse.status === 404) {
        console.warn('Requested resource not found');
      } else if (errorResponse.status && errorResponse.status >= 500) {
        console.error('Server error:', errorMessage);
      }

      return Promise.reject(normalizedError);
    }
  );

  // Store the instance for future use
  httpInstances.set(baseURL, instance);
  return instance;
};

// Pre-configured API clients
const http = {
  // Auth API client
  auth: createHttpClient(AUTH_API_URL),
  
  // IAM API client
  iam: createHttpClient(IAM_API_URL),
  
  // Enrollment API client
  enrollment: createHttpClient(ENROLLMENT_API_URL),
  
  // Set authentication token
  setAuthToken,
  
  // Get authentication token
  getAuthToken,
  
  // Create a custom client
  create: createHttpClient,
};

export type { ApiResponse, ApiError };
export default http;