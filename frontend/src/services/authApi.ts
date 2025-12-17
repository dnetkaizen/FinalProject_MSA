import http from './http';

// Types
export interface LoginWithGoogleRequest {
  idToken: string;
}

export interface LoginWithGoogleResponse {
  mfaRequired: boolean;
  userId: string;
  email: string;
  // Optional tokens if backend were to support direct login without MFA in future
  accessToken?: string;
  refreshToken?: string;
}

export interface VerifyMfaOtpRequest {
  userId: string;
  otp: string;
}

export interface VerifyMfaOtpResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
}

/**
 * Authentication API service
 */
export const authApi = {
  /**
   * Login with Google ID token
   */
  async loginWithGoogle(idToken: string): Promise<LoginWithGoogleResponse> {
    const response = await http.auth.post<LoginWithGoogleResponse>('/auth/login/google', {
      idToken,
    });
    return response.data;
  },

  /**
   * Verify MFA OTP
   */
  async verifyMfaOtp(
    userId: string,
    otp: string
  ): Promise<VerifyMfaOtpResponse> {
    const response = await http.auth.post<VerifyMfaOtpResponse>('/auth/mfa/verify', {
      userId,
      otp,
    });
    return response.data;
  },

  /**
   * Refresh access token
   */
  async refreshToken(refreshToken: string): Promise<VerifyMfaOtpResponse> {
    const response = await http.auth.post<VerifyMfaOtpResponse>('/auth/refresh', {
      refreshToken,
    });
    return response.data;
  },

  /**
   * Logout
   */
  async logout(): Promise<void> {
    // Clear the auth token from the HTTP client
    http.setAuthToken(null);
  },

  /**
   * Set the authentication token for subsequent requests
   */
  setAuthToken(token: string | null): void {
    http.setAuthToken(token);
  },
};
