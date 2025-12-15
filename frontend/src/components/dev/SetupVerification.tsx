import { useEffect, useState } from 'react';
import verifySetup from '../../utils/verifySetup';

/**
 * Setup Verification Component
 * This component runs verification checks on mount
 * Use this during development to ensure everything is configured correctly
 */
export default function SetupVerification() {
    const [results, setResults] = useState({
        environment: false,
        firebase: false,
        httpClient: false,
        tokenManagement: false,
    });
    const [isRunning, setIsRunning] = useState(true);

    useEffect(() => {
        // Run verifications on mount
        const runVerifications = async () => {
            console.log('🔍 Running setup verifications...');

            const newResults = {
                environment: verifySetup.verifyEnvironmentVariables(),
                firebase: verifySetup.verifyFirebaseSetup(),
                httpClient: verifySetup.verifyHttpClientSetup(),
                tokenManagement: verifySetup.testTokenManagement(),
            };

            setResults(newResults);
            setIsRunning(false);
        };

        runVerifications();
    }, []);

    const allPassed = Object.values(results).every(result => result === true);

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4">
            <div className="max-w-3xl mx-auto">
                <div className="bg-white rounded-lg shadow-lg p-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-6">
                        Frontend Setup Verification
                    </h1>

                    {isRunning ? (
                        <div className="flex items-center justify-center py-12">
                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                            <span className="ml-4 text-gray-600">Running verifications...</span>
                        </div>
                    ) : (
                        <>
                            <div className="space-y-4 mb-8">
                                <VerificationItem
                                    title="Environment Variables"
                                    passed={results.environment}
                                    description="Checking if all required environment variables are set"
                                />
                                <VerificationItem
                                    title="Firebase Setup"
                                    passed={results.firebase}
                                    description="Verifying Firebase Auth and Google Provider initialization"
                                />
                                <VerificationItem
                                    title="HTTP Client"
                                    passed={results.httpClient}
                                    description="Checking Axios instances for Auth, IAM, and Enrollment services"
                                />
                                <VerificationItem
                                    title="Token Management"
                                    passed={results.tokenManagement}
                                    description="Testing in-memory JWT token storage and retrieval"
                                />
                            </div>

                            <div className={`p-6 rounded-lg ${allPassed ? 'bg-green-50 border-2 border-green-500' : 'bg-red-50 border-2 border-red-500'}`}>
                                <div className="flex items-center">
                                    <span className="text-4xl mr-4">{allPassed ? '✅' : '❌'}</span>
                                    <div>
                                        <h2 className={`text-xl font-bold ${allPassed ? 'text-green-900' : 'text-red-900'}`}>
                                            {allPassed ? 'All Verifications Passed!' : 'Some Verifications Failed'}
                                        </h2>
                                        <p className={`mt-1 ${allPassed ? 'text-green-700' : 'text-red-700'}`}>
                                            {allPassed
                                                ? 'Your frontend is correctly configured and ready to use.'
                                                : 'Please check the browser console for detailed error messages.'}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            {allPassed && (
                                <div className="mt-8 p-6 bg-blue-50 rounded-lg border border-blue-200">
                                    <h3 className="text-lg font-semibold text-blue-900 mb-3">Next Steps:</h3>
                                    <ul className="space-y-2 text-blue-800">
                                        <li className="flex items-start">
                                            <span className="mr-2">1.</span>
                                            <span>Start the backend services (Auth, IAM, Enrollment)</span>
                                        </li>
                                        <li className="flex items-start">
                                            <span className="mr-2">2.</span>
                                            <span>Test the authentication flow with Google Sign-In</span>
                                        </li>
                                        <li className="flex items-start">
                                            <span className="mr-2">3.</span>
                                            <span>Verify API integrations are working</span>
                                        </li>
                                        <li className="flex items-start">
                                            <span className="mr-2">4.</span>
                                            <span>Start building your UI components</span>
                                        </li>
                                    </ul>
                                </div>
                            )}

                            <div className="mt-6 text-center">
                                <button
                                    onClick={() => window.location.reload()}
                                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                                >
                                    Run Verifications Again
                                </button>
                            </div>
                        </>
                    )}
                </div>

                <div className="mt-6 text-center text-sm text-gray-600">
                    <p>Check the browser console for detailed verification logs</p>
                </div>
            </div>
        </div>
    );
}

interface VerificationItemProps {
    title: string;
    passed: boolean;
    description: string;
}

function VerificationItem({ title, passed, description }: VerificationItemProps) {
    return (
        <div className={`p-4 rounded-lg border-2 ${passed ? 'bg-green-50 border-green-300' : 'bg-red-50 border-red-300'}`}>
            <div className="flex items-center justify-between">
                <div className="flex-1">
                    <h3 className={`font-semibold ${passed ? 'text-green-900' : 'text-red-900'}`}>
                        {title}
                    </h3>
                    <p className={`text-sm mt-1 ${passed ? 'text-green-700' : 'text-red-700'}`}>
                        {description}
                    </p>
                </div>
                <div className="ml-4">
                    <span className="text-2xl">{passed ? '✅' : '❌'}</span>
                </div>
            </div>
        </div>
    );
}
