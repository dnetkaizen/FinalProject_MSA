import http from './http';

// Types
export interface LoginWithGoogleRequest {
  idToken: string;
}

export interface LoginWithGoogleResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  requiresMfa: boolean;
  user: {
    id: string;
    email: string;
    name?: string;
    avatar?: string;
  };
}

export interface VerifyMfaOtpRequest {
  userId: string;
  otp: string;
}

export interface VerifyMfaOtpResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: {
    id: string;
    email: string;
    name?: string;
    avatar?: string;
  };
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
  async refreshToken(refreshToken: string): Promise<{ accessToken: string }> {
    const response = await http.auth.post<{ accessToken: string }>('/auth/refresh-token', {
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

    // In a real app, you might want to make an API call to invalidate the token
    // await http.auth.post('/auth/logout');
  },

  /**
   * Set the authentication token for subsequent requests
   */
  setAuthToken(token: string | null): void {
    http.setAuthToken(token);
  },
};
