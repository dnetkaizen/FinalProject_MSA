// Type for environment variable names
type EnvVarName =
  | 'VITE_FIREBASE_API_KEY'
  | 'VITE_FIREBASE_AUTH_DOMAIN'
  | 'VITE_FIREBASE_PROJECT_ID'
  | 'VITE_FIREBASE_STORAGE_BUCKET'
  | 'VITE_FIREBASE_MESSAGING_SENDER_ID'
  | 'VITE_FIREBASE_APP_ID'
  | 'VITE_AUTH_API_URL'
  | 'VITE_IAM_API_URL'
  | 'VITE_ENROLLMENT_API_URL';

/**
 * Safely gets an environment variable, throwing a descriptive error if it's missing
 * @param name The name of the environment variable
 * @returns The value of the environment variable
 * @throws {Error} If the environment variable is not defined
 */
function getEnvVar(name: EnvVarName): string {
  const value = import.meta.env[name];
  
  if (value === undefined || value === '') {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  
  return value;
}

// Firebase Configuration
export const FIREBASE_CONFIG = {
  apiKey: getEnvVar('VITE_FIREBASE_API_KEY'),
  authDomain: getEnvVar('VITE_FIREBASE_AUTH_DOMAIN'),
  projectId: getEnvVar('VITE_FIREBASE_PROJECT_ID'),
  storageBucket: getEnvVar('VITE_FIREBASE_STORAGE_BUCKET'),
  messagingSenderId: getEnvVar('VITE_FIREBASE_MESSAGING_SENDER_ID'),
  appId: getEnvVar('VITE_FIREBASE_APP_ID')
} as const;

// API URLs
export const AUTH_API_URL = getEnvVar('VITE_AUTH_API_URL');
export const IAM_API_URL = getEnvVar('VITE_IAM_API_URL');
export const ENROLLMENT_API_URL = getEnvVar('VITE_ENROLLMENT_API_URL');

// Export type for environment variables
export type EnvConfig = {
  FIREBASE_CONFIG: typeof FIREBASE_CONFIG;
  AUTH_API_URL: string;
  IAM_API_URL: string;
  ENROLLMENT_API_URL: string;
};

// Export all environment variables as a single object
export const env: EnvConfig = {
  FIREBASE_CONFIG,
  AUTH_API_URL,
  IAM_API_URL,
  ENROLLMENT_API_URL,
} as const;
