import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { AuthState } from '../types/auth';
import { firebaseAuth, googleProvider } from '../config/firebase';
import { signInWithPopup, signOut as firebaseSignOut } from 'firebase/auth';
import { authApi } from '../services/authApi';

// Action types
type AuthAction =
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'LOGIN_SUCCESS'; payload: { userId: string; email: string; accessToken: string; refreshToken: string } }
  | { type: 'MFA_REQUIRED'; payload: { userId: string; email: string } }
  | { type: 'MFA_SUCCESS'; payload: { accessToken: string; refreshToken: string } }
  | { type: 'LOGOUT' }
  | { type: 'SET_ERROR'; payload: string };

// Initial state - all tokens in memory only
const initialState: AuthState = {
  userId: null,
  email: null,
  accessToken: null,
  refreshToken: null,
  mfaRequired: false,
  isLoading: false,
  isAuthenticated: false,
};

// Reducer
const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, isLoading: action.payload };

    case 'LOGIN_SUCCESS':
      return {
        ...state,
        userId: action.payload.userId,
        email: action.payload.email,
        accessToken: action.payload.accessToken,
        refreshToken: action.payload.refreshToken,
        mfaRequired: false,
        isAuthenticated: true,
        isLoading: false,
      };

    case 'MFA_REQUIRED':
      return {
        ...state,
        userId: action.payload.userId,
        email: action.payload.email,
        mfaRequired: true,
        isAuthenticated: false,
        isLoading: false,
      };

    case 'MFA_SUCCESS':
      return {
        ...state,
        accessToken: action.payload.accessToken,
        refreshToken: action.payload.refreshToken,
        mfaRequired: false,
        isAuthenticated: true,
        isLoading: false,
      };

    case 'LOGOUT':
      return {
        ...initialState,
        isLoading: false,
      };

    case 'SET_ERROR':
      return { ...state, isLoading: false };

    default:
      return state;
  }
};

// Context type
interface AuthContextType {
  state: AuthState;
  loginWithGoogle: () => Promise<void>;
  verifyOtp: (otp: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Hook to use auth context
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

// Provider props
interface AuthProviderProps {
  children: ReactNode;
}

// Provider component
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  /**
   * Login with Google
   * 1. Sign in with Firebase
   * 2. Get Firebase ID token
   * 3. Send to backend
   * 4. Handle response (MFA required or direct login)
   */
  const loginWithGoogle = async () => {
    try {
      dispatch({ type: 'SET_LOADING', payload: true });

      // Step 1: Firebase authentication
      const result = await signInWithPopup(firebaseAuth, googleProvider);
      const firebaseUser = result.user;
      const idToken = await firebaseUser.getIdToken();

      // Step 2: Send to backend
      const response = await authApi.loginWithGoogle(idToken);

      // Step 3: Handle response
      if (response.requiresMfa) {
        // MFA is required
        dispatch({
          type: 'MFA_REQUIRED',
          payload: {
            userId: response.user.id,
            email: response.user.email,
          },
        });
      } else {
        // Direct login success
        // Set token in HTTP client
        authApi.setAuthToken(response.accessToken);

        dispatch({
          type: 'LOGIN_SUCCESS',
          payload: {
            userId: response.user.id,
            email: response.user.email,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
          },
        });
      }
    } catch (error) {
      console.error('Google sign in error:', error);
      dispatch({ type: 'SET_ERROR', payload: 'Failed to sign in with Google' });
      throw error;
    }
  };

  /**
   * Verify OTP for MFA
   * @param otp - One-time password
   */
  const verifyOtp = async (otp: string) => {
    try {
      if (!state.userId) {
        throw new Error('No user ID available for MFA verification');
      }

      dispatch({ type: 'SET_LOADING', payload: true });

      // Call backend to verify OTP
      const response = await authApi.verifyMfaOtp(state.userId, otp);

      // Set token in HTTP client
      authApi.setAuthToken(response.accessToken);

      // Update state with tokens
      dispatch({
        type: 'MFA_SUCCESS',
        payload: {
          accessToken: response.accessToken,
          refreshToken: response.refreshToken,
        },
      });
    } catch (error) {
      console.error('MFA verification error:', error);
      dispatch({ type: 'SET_ERROR', payload: 'Invalid OTP code' });
      throw error;
    }
  };

  /**
   * Logout
   * 1. Sign out from Firebase
   * 2. Clear tokens from HTTP client
   * 3. Reset state
   */
  const logout = async () => {
    try {
      // Sign out from Firebase
      await firebaseSignOut(firebaseAuth);

      // Clear token from HTTP client
      authApi.setAuthToken(null);

      // Reset state
      dispatch({ type: 'LOGOUT' });
    } catch (error) {
      console.error('Sign out error:', error);
      // Even if Firebase sign out fails, clear local state
      authApi.setAuthToken(null);
      dispatch({ type: 'LOGOUT' });
    }
  };

  const value: AuthContextType = {
    state,
    loginWithGoogle,
    verifyOtp,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
