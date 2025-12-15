import { initializeApp, FirebaseApp, getApps } from 'firebase/app';
import { getAuth, GoogleAuthProvider, Auth } from 'firebase/auth';
import { FIREBASE_CONFIG } from './env';

// Singleton pattern - ensure Firebase initializes only once
let app: FirebaseApp;
let auth: Auth;
let googleAuthProvider: GoogleAuthProvider;

/**
 * Initialize Firebase app (singleton)
 * Only initializes if not already initialized
 */
function initializeFirebase(): FirebaseApp {
  if (!app) {
    // Check if Firebase is already initialized
    const existingApps = getApps();
    if (existingApps.length > 0) {
      app = existingApps[0];
    } else {
      app = initializeApp(FIREBASE_CONFIG);
    }
  }
  return app;
}

/**
 * Get Firebase Auth instance (singleton)
 */
function getFirebaseAuth(): Auth {
  if (!auth) {
    const firebaseApp = initializeFirebase();
    auth = getAuth(firebaseApp);
  }
  return auth;
}

/**
 * Get Google Auth Provider (singleton)
 */
function getGoogleAuthProvider(): GoogleAuthProvider {
  if (!googleAuthProvider) {
    googleAuthProvider = new GoogleAuthProvider();
    // Configure Google Provider
    googleAuthProvider.setCustomParameters({
      prompt: 'select_account',
    });
  }
  return googleAuthProvider;
}

// Initialize and export
const firebaseApp = initializeFirebase();
export const firebaseAuth = getFirebaseAuth();
export const googleProvider = getGoogleAuthProvider();

// Export for compatibility
export { firebaseAuth as auth };

export default firebaseApp;
