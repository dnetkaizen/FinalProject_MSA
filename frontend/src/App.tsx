import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import LoginPage from './pages/LoginPage';
import MfaPage from './pages/MfaPage';
import CoursesPage from './pages/CoursesPage';

/**
 * Protected Route Component
 * Requires user to be authenticated (have accessToken)
 */
function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { state } = useAuth();
  const location = useLocation();

  if (state.isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  return state.isAuthenticated ? (
    <>{children}</>
  ) : (
    <Navigate to="/login" state={{ from: location }} replace />
  );
}

/**
 * MFA Route Component
 * Requires mfaRequired === true
 */
function MfaRoute({ children }: { children: React.ReactNode }) {
  const { state } = useAuth();

  // If already authenticated, redirect to courses
  if (state.isAuthenticated) {
    return <Navigate to="/courses" replace />;
  }

  // If MFA not required, redirect to login
  if (!state.mfaRequired) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

/**
 * App Routes
 */
function AppRoutes() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<LoginPage />} />

      {/* MFA Route - Protected by mfaRequired */}
      <Route
        path="/mfa"
        element={
          <MfaRoute>
            <MfaPage />
          </MfaRoute>
        }
      />

      {/* Protected Routes - Require accessToken */}
      <Route
        path="/courses"
        element={
          <PrivateRoute>
            <CoursesPage />
          </PrivateRoute>
        }
      />

      {/* Default Route */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Catch All - Redirect to login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

/**
 * Main App Component
 * Wraps everything with Router and AuthProvider
 */
// ... imports
import { Toaster } from 'react-hot-toast';

// ... (Rest of the file)

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <AppRoutes />
        <Toaster position="top-right" />
      </AuthProvider>
    </Router>
  );
}