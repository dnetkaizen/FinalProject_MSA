/**
 * Verification script for Firebase and HTTP client setup
 * This file tests that all configurations are working correctly
 */

import { firebaseAuth, googleProvider } from '../config/firebase';
import http from '../services/http';
import { AUTH_API_URL, IAM_API_URL, ENROLLMENT_API_URL } from '../config/env';

/**
 * Verify Firebase initialization
 */
export function verifyFirebaseSetup(): boolean {
    try {
        console.log('🔍 Verifying Firebase setup...');

        // Check if auth is initialized
        if (!firebaseAuth) {
            console.error('❌ Firebase Auth is not initialized');
            return false;
        }
        console.log('✅ Firebase Auth initialized');

        // Check if Google provider is configured
        if (!googleProvider) {
            console.error('❌ Google Auth Provider is not configured');
            return false;
        }
        console.log('✅ Google Auth Provider configured');

        // Check Firebase app name
        console.log('✅ Firebase app name:', firebaseAuth.app.name);

        return true;
    } catch (error) {
        console.error('❌ Firebase verification failed:', error);
        return false;
    }
}

/**
 * Verify HTTP client configuration
 */
export function verifyHttpClientSetup(): boolean {
    try {
        console.log('🔍 Verifying HTTP client setup...');

        // Check if http object exists
        if (!http) {
            console.error('❌ HTTP client is not initialized');
            return false;
        }
        console.log('✅ HTTP client initialized');

        // Check auth client
        if (!http.auth) {
            console.error('❌ Auth HTTP client is not configured');
            return false;
        }
        console.log('✅ Auth HTTP client configured:', AUTH_API_URL);

        // Check IAM client
        if (!http.iam) {
            console.error('❌ IAM HTTP client is not configured');
            return false;
        }
        console.log('✅ IAM HTTP client configured:', IAM_API_URL);

        // Check enrollment client
        if (!http.enrollment) {
            console.error('❌ Enrollment HTTP client is not configured');
            return false;
        }
        console.log('✅ Enrollment HTTP client configured:', ENROLLMENT_API_URL);

        // Check token management functions
        if (typeof http.setAuthToken !== 'function') {
            console.error('❌ setAuthToken function is not available');
            return false;
        }
        console.log('✅ setAuthToken function available');

        if (typeof http.getAuthToken !== 'function') {
            console.error('❌ getAuthToken function is not available');
            return false;
        }
        console.log('✅ getAuthToken function available');

        return true;
    } catch (error) {
        console.error('❌ HTTP client verification failed:', error);
        return false;
    }
}

/**
 * Verify environment variables
 */
export function verifyEnvironmentVariables(): boolean {
    try {
        console.log('🔍 Verifying environment variables...');

        const requiredVars = [
            'VITE_FIREBASE_API_KEY',
            'VITE_FIREBASE_AUTH_DOMAIN',
            'VITE_FIREBASE_PROJECT_ID',
            'VITE_FIREBASE_STORAGE_BUCKET',
            'VITE_FIREBASE_MESSAGING_SENDER_ID',
            'VITE_FIREBASE_APP_ID',
            'VITE_AUTH_API_URL',
            'VITE_IAM_API_URL',
            'VITE_ENROLLMENT_API_URL',
        ];

        let allPresent = true;

        for (const varName of requiredVars) {
            const value = import.meta.env[varName];
            if (!value || value === '') {
                console.error(`❌ Missing environment variable: ${varName}`);
                allPresent = false;
            } else {
                console.log(`✅ ${varName}: ${value.substring(0, 20)}...`);
            }
        }

        return allPresent;
    } catch (error) {
        console.error('❌ Environment variables verification failed:', error);
        return false;
    }
}

/**
 * Test token management
 */
export function testTokenManagement(): boolean {
    try {
        console.log('🔍 Testing token management...');

        // Test setting token
        const testToken = 'test-jwt-token-123';
        http.setAuthToken(testToken);

        // Test getting token
        const retrievedToken = http.getAuthToken();
        if (retrievedToken !== testToken) {
            console.error('❌ Token management failed: tokens do not match');
            return false;
        }
        console.log('✅ Token set and retrieved successfully');

        // Test clearing token
        http.setAuthToken(null);
        const clearedToken = http.getAuthToken();
        if (clearedToken !== null) {
            console.error('❌ Token management failed: token not cleared');
            return false;
        }
        console.log('✅ Token cleared successfully');

        return true;
    } catch (error) {
        console.error('❌ Token management test failed:', error);
        return false;
    }
}

/**
 * Run all verifications
 */
export function runAllVerifications(): void {
    console.log('\n🚀 Starting Frontend Setup Verification...\n');

    const results = {
        environment: verifyEnvironmentVariables(),
        firebase: verifyFirebaseSetup(),
        httpClient: verifyHttpClientSetup(),
        tokenManagement: testTokenManagement(),
    };

    console.log('\n📊 Verification Results:');
    console.log('========================');
    console.log(`Environment Variables: ${results.environment ? '✅ PASS' : '❌ FAIL'}`);
    console.log(`Firebase Setup: ${results.firebase ? '✅ PASS' : '❌ FAIL'}`);
    console.log(`HTTP Client: ${results.httpClient ? '✅ PASS' : '❌ FAIL'}`);
    console.log(`Token Management: ${results.tokenManagement ? '✅ PASS' : '❌ FAIL'}`);

    const allPassed = Object.values(results).every(result => result === true);

    if (allPassed) {
        console.log('\n✅ All verifications passed! Frontend setup is complete.\n');
    } else {
        console.log('\n❌ Some verifications failed. Please check the errors above.\n');
    }
}

// Export for use in components
export default {
    verifyFirebaseSetup,
    verifyHttpClientSetup,
    verifyEnvironmentVariables,
    testTokenManagement,
    runAllVerifications,
};
