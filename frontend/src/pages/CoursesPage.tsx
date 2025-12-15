import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { enrollmentApi, Course, Enrollment } from '../services/enrollmentApi';

export default function CoursesPage() {
    const { state, logout } = useAuth();
    const navigate = useNavigate();

    // State
    const [courses, setCourses] = useState<Course[]>([]);
    const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [enrollingCourseId, setEnrollingCourseId] = useState<string | null>(null);

    // Load courses and enrollments on mount
    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Fetch courses and user enrollments in parallel
            const [coursesData, enrollmentsData] = await Promise.all([
                enrollmentApi.getCourses(true), // Only active courses
                state.userId ? enrollmentApi.getUserEnrollments(state.userId) : Promise.resolve([])
            ]);

            setCourses(coursesData);
            setEnrollments(enrollmentsData);
        } catch (err) {
            console.error('Error loading data:', err);
            setError('Failed to load courses. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleEnroll = async (courseId: string) => {
        if (!state.userId) {
            setError('User ID not available');
            return;
        }

        try {
            setEnrollingCourseId(courseId);
            setError(null);

            await enrollmentApi.enrollUser({
                userId: state.userId,
                courseId: courseId,
            });

            // Reload enrollments
            const enrollmentsData = await enrollmentApi.getUserEnrollments(state.userId);
            setEnrollments(enrollmentsData);

        } catch (err: any) {
            console.error('Error enrolling:', err);
            setError(err.response?.data?.message || 'Failed to enroll in course. Please try again.');
        } finally {
            setEnrollingCourseId(null);
        }
    };

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    const isEnrolled = (courseId: string) => {
        return enrollments.some(e => e.courseId === courseId && e.status === 'active');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex items-center justify-between">
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Available Courses</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                Browse and enroll in courses
                            </p>
                        </div>
                        <div className="flex items-center gap-4">
                            {state.email && (
                                <div className="text-right">
                                    <p className="text-sm font-medium text-gray-900">{state.email}</p>
                                    <p className="text-xs text-gray-500">
                                        {enrollments.length} enrollment{enrollments.length !== 1 ? 's' : ''}
                                    </p>
                                </div>
                            )}
                            <button
                                onClick={handleLogout}
                                className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Error Message */}
                {error && (
                    <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                        <div className="flex items-center">
                            <svg
                                className="w-5 h-5 text-red-600 mr-2"
                                fill="currentColor"
                                viewBox="0 0 20 20"
                            >
                                <path
                                    fillRule="evenodd"
                                    d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                                    clipRule="evenodd"
                                />
                            </svg>
                            <p className="text-sm text-red-800">{error}</p>
                        </div>
                    </div>
                )}

                {/* Loading State */}
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-12">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
                        <p className="text-gray-600">Loading courses...</p>
                    </div>
                ) : courses.length === 0 ? (
                    /* Empty State */
                    <div className="bg-white rounded-lg shadow p-12 text-center">
                        <svg
                            className="mx-auto h-12 w-12 text-gray-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                            />
                        </svg>
                        <h3 className="mt-2 text-sm font-medium text-gray-900">No courses available</h3>
                        <p className="mt-1 text-sm text-gray-500">
                            There are no active courses at the moment.
                        </p>
                    </div>
                ) : (
                    /* Courses Grid */
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {courses.map((course) => {
                            const enrolled = isEnrolled(course.id);
                            const isEnrolling = enrollingCourseId === course.id;

                            return (
                                <div
                                    key={course.id}
                                    className="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow"
                                >
                                    <div className="p-6">
                                        {/* Course Header */}
                                        <div className="flex items-start justify-between mb-4">
                                            <div className="flex-1">
                                                <span className="inline-block px-2 py-1 text-xs font-semibold text-blue-800 bg-blue-100 rounded">
                                                    {course.code}
                                                </span>
                                                {enrolled && (
                                                    <span className="ml-2 inline-block px-2 py-1 text-xs font-semibold text-green-800 bg-green-100 rounded">
                                                        ✓ Enrolled
                                                    </span>
                                                )}
                                            </div>
                                        </div>

                                        {/* Course Title */}
                                        <h3 className="text-lg font-semibold text-gray-900 mb-2">
                                            {course.name}
                                        </h3>

                                        {/* Course Description */}
                                        {course.description && (
                                            <p className="text-sm text-gray-600 mb-4 line-clamp-3">
                                                {course.description}
                                            </p>
                                        )}

                                        {/* Course Info */}
                                        <div className="flex items-center text-sm text-gray-500 mb-4">
                                            {course.credits && (
                                                <span className="flex items-center">
                                                    <svg
                                                        className="w-4 h-4 mr-1"
                                                        fill="none"
                                                        stroke="currentColor"
                                                        viewBox="0 0 24 24"
                                                    >
                                                        <path
                                                            strokeLinecap="round"
                                                            strokeLinejoin="round"
                                                            strokeWidth={2}
                                                            d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                                                        />
                                                    </svg>
                                                    {course.credits} credits
                                                </span>
                                            )}
                                        </div>

                                        {/* Enroll Button */}
                                        <button
                                            onClick={() => handleEnroll(course.id)}
                                            disabled={enrolled || isEnrolling}
                                            className={`w-full px-4 py-2 text-sm font-medium rounded-lg focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors ${enrolled
                                                    ? 'bg-green-50 text-green-700 border border-green-200 cursor-default'
                                                    : isEnrolling
                                                        ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                                                        : 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500'
                                                }`}
                                        >
                                            {isEnrolling ? (
                                                <span className="flex items-center justify-center">
                                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-gray-400 mr-2"></div>
                                                    Enrolling...
                                                </span>
                                            ) : enrolled ? (
                                                'Already Enrolled'
                                            ) : (
                                                'Enroll Now'
                                            )}
                                        </button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}

                {/* My Enrollments Section */}
                {enrollments.length > 0 && (
                    <div className="mt-12">
                        <h2 className="text-xl font-bold text-gray-900 mb-4">My Enrollments</h2>
                        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                            <div className="overflow-x-auto">
                                <table className="min-w-full divide-y divide-gray-200">
                                    <thead className="bg-gray-50">
                                        <tr>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Course
                                            </th>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Status
                                            </th>
                                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Enrolled At
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                        {enrollments.map((enrollment) => (
                                            <tr key={enrollment.id}>
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <div className="text-sm font-medium text-gray-900">
                                                        {enrollment.course?.name || 'Unknown Course'}
                                                    </div>
                                                    <div className="text-sm text-gray-500">
                                                        {enrollment.course?.code}
                                                    </div>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${enrollment.status === 'active'
                                                            ? 'bg-green-100 text-green-800'
                                                            : enrollment.status === 'completed'
                                                                ? 'bg-blue-100 text-blue-800'
                                                                : 'bg-gray-100 text-gray-800'
                                                        }`}>
                                                        {enrollment.status}
                                                    </span>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                    {new Date(enrollment.enrolledAt).toLocaleDateString()}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}
