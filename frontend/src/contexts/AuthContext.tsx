import React, { createContext, useContext, useReducer, ReactNode, useEffect } from 'react';
import { AuthState } from '../types/auth';
import { firebaseAuth, googleProvider } from '../config/firebase';
import { signInWithPopup, signOut as firebaseSignOut } from 'firebase/auth';
import { authApi } from '../services/authApi';
import { iamApi } from '../services/iamApi';
import { jwtDecode } from 'jwt-decode';

// --- UTILS ---
const normalizeArray = (input: any): string[] => {
  if (Array.isArray(input)) return input;
  if (typeof input === 'string') return [input];
  if (input && typeof input === 'object' && input !== null) return [input.toString()]; // Safety for objects
  return [];
};

// --- ACTION TYPES ---
type AuthAction =
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'LOGIN_SUCCESS'; payload: { userId: string; email: string; accessToken: string; refreshToken: string } }
  | { type: 'RESTORE_SESSION'; payload: { userId: string; email: string; accessToken: string; refreshToken: string; roles: string[]; permissions: string[] } }
  | { type: 'SET_ROLES_PERMISSIONS'; payload: { roles: string[]; permissions: string[] } }
  | { type: 'MFA_REQUIRED'; payload: { userId: string; email: string } }
  | { type: 'MFA_SUCCESS'; payload: { accessToken: string; refreshToken: string } }
  | { type: 'LOGOUT' }
  | { type: 'SET_ERROR'; payload: string };

// --- INITIAL STATE ---
const initialState: AuthState = {
  userId: null,
  email: null,
  accessToken: null,
  refreshToken: null,
  mfaRequired: false,
  isLoading: true,
  isAuthenticated: false,
  roles: [],
  permissions: []
};

// --- REDUCER ---
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

    case 'RESTORE_SESSION':
      return {
        ...state,
        userId: action.payload.userId,
        email: action.payload.email,
        accessToken: action.payload.accessToken,
        refreshToken: action.payload.refreshToken,
        roles: action.payload.roles,
        permissions: action.payload.permissions,
        mfaRequired: false,
        isAuthenticated: true,
        isLoading: false,
      };

    case 'SET_ROLES_PERMISSIONS':
      return {
        ...state,
        roles: action.payload.roles,
        permissions: action.payload.permissions
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

// --- CONTEXT ---
interface AuthContextType {
  state: AuthState;
  loginWithGoogle: () => Promise<void>;
  verifyOtp: (otp: string) => Promise<void>;
  logout: () => Promise<void>;
  hasRole: (role: string) => boolean;
  hasPermission: (permission: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

interface JwtPayload {
  sub: string;
  email: string;
  exp: number;
  [key: string]: any;
}

// --- PROVIDER ---
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Initialize Session on Mount (Bootstrap)
  useEffect(() => {
    const bootstrapSession = async () => {
      // 1. Read Token
      const accessToken = localStorage.getItem('accessToken');
      const refreshToken = localStorage.getItem('refreshToken');

      if (!accessToken) {
        dispatch({ type: 'SET_LOADING', payload: false });
        return;
      }

      try {
        // 2. Decode & Validate
        const decoded = jwtDecode<JwtPayload>(accessToken);
        const currentTime = Date.now() / 1000;

        if (decoded.exp < currentTime) {
          console.warn('Token expired during bootstrap');
          // Try to refresh if we have a refresh token
          if (refreshToken) {
             try {
               const newTokens = await authApi.refreshToken(refreshToken);
               // If successful, proceed with new token
               await handleSuccess(decoded.sub, decoded.email, newTokens.accessToken, newTokens.refreshToken);
               return;
             } catch (refreshErr) {
               console.warn('Bootstrap refresh failed:', refreshErr);
               await logout();
               return;
             }
          } else {
             await logout();
             return;
          }
        }

        const userId = decoded.sub;
        const email = decoded.email;

        // 3. Set Base Session (Token in API)
        authApi.setAuthToken(accessToken);

        // 4. Consult IAM
        let roles: string[] = [];
        let permissions: string[] = [];

        try {
          const [fetchedRoles, fetchedPermissions] = await Promise.all([
            iamApi.getUserRoles(userId).catch(e => {
              console.warn("IAM getRoles failed:", e);
              return [];
            }),
            iamApi.getUserPermissions(userId).catch(e => {
              console.warn("IAM getPermissions failed:", e);
              return [];
            })
          ]);

          roles = normalizeArray(fetchedRoles);
          permissions = normalizeArray(fetchedPermissions);

        } catch (iamError) {
          console.error("Critical error contacting IAM during bootstrap:", iamError);
          // Graceful degradation
          roles = [];
          permissions = [];
        }

        // 5. Apply Rules (Default Student)
        if (roles.length === 0) {
          roles = ['student'];
        }

        // 6. Update Storage & State
        localStorage.setItem('userRoles', JSON.stringify(roles));
        localStorage.setItem('userPermissions', JSON.stringify(permissions));

        dispatch({
          type: 'RESTORE_SESSION',
          payload: {
            userId,
            email,
            accessToken,
            refreshToken: refreshToken || '',
            roles,
            permissions
          }
        });

      } catch (error) {
        console.error('Session bootstrap failed:', error);
        await logout();
      }
    };

    bootstrapSession();
  }, []);

  // --- INACTIVITY & SESSION RENEWAL ---
  const lastActivityRef = React.useRef<number>(Date.now());
  const checkIntervalRef = React.useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    // 1. Setup Activity Listeners
    const events = ['mousedown', 'keydown', 'touchstart', 'scroll'];
    const handleActivity = () => {
      lastActivityRef.current = Date.now();
    };

    events.forEach(event => window.addEventListener(event, handleActivity));

    // 2. Setup Interval Check (every 1 min)
    checkIntervalRef.current = setInterval(async () => {
      const now = Date.now();
      const timeSinceLastActivity = now - lastActivityRef.current;
      const INACTIVITY_LIMIT = 30 * 60 * 1000; // 30 mins

      // A. Check Inactivity
      if (state.isAuthenticated && timeSinceLastActivity > INACTIVITY_LIMIT) {
        console.warn('User inactive for > 30 mins. Logging out.');
        await logout();
        return;
      }

      // B. Check Token Expiration & Refresh
      if (state.isAuthenticated && state.accessToken) {
        try {
          const decoded = jwtDecode<JwtPayload>(state.accessToken);
          const expTime = decoded.exp * 1000;
          const timeLeft = expTime - now;

          // If < 5 mins left, and user is active (implied by not hitting A), Refresh
          if (timeLeft < 5 * 60 * 1000) {
             console.log('Token expiring soon. Refreshing...');
             if (state.refreshToken) {
                try {
                  const newTokens = await authApi.refreshToken(state.refreshToken);
                  // We reuse handleSuccess to update state/storage
                  // But handleSuccess fetches roles again, which is fine but maybe redundant. 
                  // Let's just update tokens directly here or call a lighter version?
                  // handleSuccess is fine.
                  await handleSuccess(state.userId!, state.email!, newTokens.accessToken, newTokens.refreshToken);
                  console.log('Session refreshed.');
                } catch (err) {
                  console.error('Failed to refresh session:', err);
                }
             }
          }
        } catch (e) {
           console.error('Token check failed:', e);
        }
      }

    }, 60 * 1000); // Check every minute

    return () => {
      events.forEach(event => window.removeEventListener(event, handleActivity));
      if (checkIntervalRef.current) clearInterval(checkIntervalRef.current);
    };
  }, [state.isAuthenticated, state.accessToken, state.refreshToken]);

  // Helper to fetch roles post-login (called manually)
  const fetchRolesAndPermissions = async (userId: string) => {
    try {
      const [fetchedRoles, fetchedPermissions] = await Promise.all([
        iamApi.getUserRoles(userId).catch(e => {
          console.warn("IAM getRoles failed", e);
          return [];
        }),
        iamApi.getUserPermissions(userId).catch(e => {
          console.warn("IAM getPermissions failed", e);
          return [];
        })
      ]);

      let roles = normalizeArray(fetchedRoles);
      let permissions = normalizeArray(fetchedPermissions);

      if (roles.length === 0) {
        roles = ['student'];
      }

      localStorage.setItem('userRoles', JSON.stringify(roles));
      localStorage.setItem('userPermissions', JSON.stringify(permissions));

      dispatch({
        type: 'SET_ROLES_PERMISSIONS',
        payload: { roles, permissions }
      });

    } catch (err) {
      console.error("Error fetching roles after login:", err);
      // Default to student on error
      const roles = ['student'];
      dispatch({
        type: 'SET_ROLES_PERMISSIONS',
        payload: { roles, permissions: [] }
      });
    }
  };

  const loginWithGoogle = async () => {
    try {
      dispatch({ type: 'SET_LOADING', payload: true });
      const result = await signInWithPopup(firebaseAuth, googleProvider);
      const firebaseUser = result.user;
      const idToken = await firebaseUser.getIdToken();

      const response = await authApi.loginWithGoogle(idToken);

      if (response.mfaRequired) {
        dispatch({
          type: 'MFA_REQUIRED',
          payload: { userId: response.userId, email: response.email },
        });
      } else if (response.accessToken) {
        // Success
        handleSuccess(response.userId, response.email, response.accessToken, response.refreshToken || '');
      } else {
        console.warn("Login successful but no tokens received.");
        dispatch({ type: 'SET_LOADING', payload: false });
      }
    } catch (error) {
      console.error('Google sign in error:', error);
      dispatch({ type: 'SET_ERROR', payload: 'Failed to sign in with Google' });
      throw error;
    }
  };

  const verifyOtp = async (otp: string) => {
    try {
      if (!state.userId) throw new Error('No user ID');
      dispatch({ type: 'SET_LOADING', payload: true });

      const response = await authApi.verifyMfaOtp(state.userId, otp);
      await handleSuccess(state.userId, state.email || '', response.accessToken, response.refreshToken);

    } catch (error) {
      console.error('MFA error:', error);
      dispatch({ type: 'SET_ERROR', payload: 'Invalid OTP code' });
      throw error;
    }
  };

  const handleSuccess = async (userId: string, email: string, accessToken: string, refreshToken: string) => {
    authApi.setAuthToken(accessToken);
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    dispatch({
      type: 'LOGIN_SUCCESS',
      payload: { userId, email, accessToken, refreshToken },
    });

    // Fetch Roles IMMEDIATELY after login to populate state
    await fetchRolesAndPermissions(userId);
  };

  const logout = async () => {
    try {
      await firebaseSignOut(firebaseAuth);
    } catch (err) {
      console.error("Firebase logout error (ignoring)", err);
    }

    authApi.setAuthToken(null);
    localStorage.clear();
    dispatch({ type: 'LOGOUT' });
    window.location.href = '/login'; // Force clear
  };

  const hasRole = (role: string) => {
    const roles = normalizeArray(state.roles);
    return roles.includes(role);
  };

  const hasPermission = (permission: string) => {
    const permissions = normalizeArray(state.permissions);
    return permissions.includes(permission);
  };

  const value: AuthContextType = {
    state,
    loginWithGoogle,
    verifyOtp,
    logout,
    hasRole,
    hasPermission
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
