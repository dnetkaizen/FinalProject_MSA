export interface User {
  id: string;
  email: string;
  name: string;
  avatar?: string;
}

export interface AuthState {
  userId: string | null;
  email: string | null;
  accessToken: string | null;
  refreshToken: string | null;
  mfaRequired: boolean;
  isLoading: boolean;
  isAuthenticated: boolean;
}

export interface GoogleSignInResult {
  user: User;
  idToken: string;
}

export interface MFARequest {
  email: string;
  otp: string;
}

export interface MFAVerification {
  email: string;
  otp: string;
  firebaseToken: string;
}

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface Course {
  id: string;
  code: string;
  name: string;
  active: boolean;
}

export interface Enrollment {
  id: string;
  userId: string;
  courseId: string;
  enrolledAt: string;
  course: Course;
}
