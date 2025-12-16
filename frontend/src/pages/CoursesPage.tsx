import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { enrollmentApi, Course, Enrollment } from '../services/enrollmentApi';
import { EnrollmentModal } from '../components/ui/EnrollmentModal';
import toast from 'react-hot-toast';

type Tab = 'available' | 'enrolled';

export default function CoursesPage() {
    const { state, logout } = useAuth();
    const navigate = useNavigate();

    // State
    const [activeTab, setActiveTab] = useState<Tab>('available');
    const [courses, setCourses] = useState<Course[]>([]);
    const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
    const [loading, setLoading] = useState(true);

    // Modal State
    const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Derived State
    const enrolledCourseIds = useMemo(() => {
        return new Set((enrollments || []).map(e => e.courseId));
    }, [enrollments]);

    // Load courses and enrollments on mount
    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);

            // Parallel fetch
            const [coursesData, enrollmentsData] = await Promise.all([
                enrollmentApi.getCourses(true),
                state.isAuthenticated ? enrollmentApi.getMyEnrollments() : Promise.resolve([])
            ]);

            setCourses(coursesData || []);
            setEnrollments(enrollmentsData || []);
        } catch (err) {
            console.error('Error loading data:', err);
            toast.error('Failed to load courses. Please try again.');
            setCourses([]);
            setEnrollments([]);
        } finally {
            setLoading(false);
        }
    };

    const openEnrollModal = (course: Course) => {
        if (!state.isAuthenticated) {
            toast.error('You must be logged in to enroll');
            return;
        }
        setSelectedCourse(course);
        setIsModalOpen(true);
    };

    const handleEnrollConfirm = async (courseId: string) => {
        try {
            await enrollmentApi.enrollUser(courseId);

            toast.success('Successfully enrolled in course!');

            // Refresh enrollments to update UI state
            const updatedEnrollments = await enrollmentApi.getMyEnrollments();
            setEnrollments(updatedEnrollments || []);

        } catch (err: any) {
            console.error('Error enrolling:', err);
            const message = err.response?.data?.message || 'Failed to enroll. Please try again.';
            toast.error(message);
            throw err; // Re-throw to let modal know it failed
        }
    };

    const handleLogout = async () => {
        await logout();
        navigate('/login');
        toast.success('Logged out successfully');
    };

    return (
        <div className="min-h-screen bg-gray-50 font-sans text-gray-900">
            {/* Navbar */}
            <nav className="bg-white shadow-sm sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16 items-center">
                        <div className="flex-shrink-0 flex items-center gap-2">
                            <div className="h-8 w-8 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold text-lg">
                                U
                            </div>
                            <span className="font-bold text-xl tracking-tight text-gray-900">UniPortal</span>
                        </div>
                        <div className="flex items-center gap-4">
                            <div className="hidden md:block text-right">
                                <p className="text-sm font-semibold text-gray-700">{state.email}</p>
                                <p className="text-xs text-gray-500">Student</p>
                            </div>
                            <button
                                onClick={handleLogout}
                                className="ml-4 px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-gray-800 hover:bg-gray-700 transition"
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

                {/* Page Header & Stats */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">Course Dashboard</h1>
                    <p className="mt-1 text-gray-500">Manage your academic journey effectively.</p>
                </div>

                {/* Tabs */}
                <div className="border-b border-gray-200 mb-8">
                    <nav className="-mb-px flex space-x-8" aria-label="Tabs">
                        <button
                            onClick={() => setActiveTab('available')}
                            className={`${activeTab === 'available'
                                    ? 'border-blue-500 text-blue-600'
                                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition-colors`}
                        >
                            Available Courses
                            <span className="ml-2 bg-gray-100 text-gray-600 py-0.5 px-2.5 rounded-full text-xs font-medium">
                                {(courses || []).length}
                            </span>
                        </button>
                        <button
                            onClick={() => setActiveTab('enrolled')}
                            className={`${activeTab === 'enrolled'
                                    ? 'border-blue-500 text-blue-600'
                                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition-colors`}
                        >
                            My Enrollments
                            <span className="ml-2 bg-blue-100 text-blue-600 py-0.5 px-2.5 rounded-full text-xs font-medium">
                                {(enrollments || []).length}
                            </span>
                        </button>
                    </nav>
                </div>

                {/* Content Area */}
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-20 text-gray-500">
                        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-4"></div>
                        <p>Loading data...</p>
                    </div>
                ) : (
                    <>
                        {/* Tab: Available Courses */}
                        {activeTab === 'available' && (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 animate-fade-in-up">
                                {(courses || []).map((course) => {
                                    const isEnrolled = enrolledCourseIds.has(course.id);

                                    return (
                                        <div
                                            key={course.id}
                                            className="bg-white rounded-xl shadow-sm border border-gray-100 hover:shadow-lg transition-all duration-300 flex flex-col h-full overflow-hidden group"
                                        >
                                            <div className="p-6 flex-1 flex flex-col">
                                                <div className="flex justify-between items-start mb-4">
                                                    <span className="bg-blue-50 text-blue-700 text-xs font-bold px-2 py-1 rounded uppercase tracking-wide">
                                                        {course.code}
                                                    </span>
                                                    {course.credits && (
                                                        <span className="text-gray-400 text-xs font-medium flex items-center">
                                                            ★ {course.credits} Credits
                                                        </span>
                                                    )}
                                                </div>

                                                <h3 className="text-xl font-bold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors">
                                                    {course.name}
                                                </h3>

                                                {course.description && (
                                                    <p className="text-gray-500 text-sm mb-4 line-clamp-2 flex-1">
                                                        {course.description}
                                                    </p>
                                                )}

                                                <button
                                                    onClick={() => openEnrollModal(course)}
                                                    disabled={isEnrolled}
                                                    className={`w-full mt-auto py-2.5 px-4 rounded-lg font-medium text-sm transition-all focus:ring-2 focus:ring-offset-1 ${isEnrolled
                                                            ? 'bg-green-50 text-green-700 border border-green-200 cursor-default opacity-80'
                                                            : 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500 shadow-md hover:shadow-lg transform active:scale-95'
                                                        }`}
                                                >
                                                    {isEnrolled ? (
                                                        <span className="flex items-center justify-center gap-2">
                                                            ✓ Enrolled
                                                        </span>
                                                    ) : (
                                                        'Enroll Now'
                                                    )}
                                                </button>
                                            </div>
                                        </div>
                                    );
                                })}
                                {(courses || []).length === 0 && (
                                    <div className="col-span-full py-12 text-center bg-white rounded-xl border border-dashed border-gray-300">
                                        <p className="text-gray-500">No active courses available at the moment.</p>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Tab: My Enrollments */}
                        {activeTab === 'enrolled' && (
                            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden animate-fade-in">
                                {(enrollments || []).length > 0 ? (
                                    <div className="overflow-x-auto">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                                <tr>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Course</th>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Enrolled Date</th>
                                                </tr>
                                            </thead>
                                            <tbody className="bg-white divide-y divide-gray-200">
                                                {(enrollments || []).map((enrollment) => {
                                                    // Lookup course details from the courses list since EnrollmentResponse doesn't include it
                                                    const course = courses.find(c => c.id === enrollment.courseId);

                                                    return (
                                                        <tr key={enrollment.id} className="hover:bg-gray-50 transition-colors">
                                                            <td className="px-6 py-4 whitespace-nowrap">
                                                                <div className="flex flex-col">
                                                                    <span className="text-sm font-medium text-gray-900">{course?.name || 'Unknown Course'}</span>
                                                                    <span className="text-xs text-gray-500">{course?.code || enrollment.courseId}</span>
                                                                </div>
                                                            </td>
                                                            <td className="px-6 py-4 whitespace-nowrap">
                                                                <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                                                    Enrolled
                                                                </span>
                                                            </td>
                                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                                {enrollment.enrolledAt ? new Date(enrollment.enrolledAt).toLocaleDateString() : '-'}
                                                            </td>
                                                        </tr>
                                                    );
                                                })}
                                            </tbody>
                                        </table>
                                    </div>
                                ) : (
                                    <div className="px-6 py-12 text-center">
                                        <div className="mx-auto h-12 w-12 text-gray-400 mb-4">
                                            <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                                            </svg>
                                        </div>
                                        <h3 className="text-lg font-medium text-gray-900">No enrollments yet</h3>
                                        <p className="mt-1 text-sm text-gray-500">
                                            Browse active courses and enroll to get started.
                                        </p>
                                        <button
                                            onClick={() => setActiveTab('available')}
                                            className="mt-6 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none"
                                        >
                                            Browse Courses
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </>
                )}
            </main>

            {/* Modal */}
            <EnrollmentModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onConfirm={handleEnrollConfirm}
                course={selectedCourse}
            />
        </div>
    );
}
