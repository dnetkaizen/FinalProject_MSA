import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { enrollmentApi, Course, Enrollment, CreateCourseRequest, UpdateCourseRequest } from '../services/enrollmentApi';
import { EnrollmentModal } from '../components/ui/EnrollmentModal';
import { CourseManagementModal } from '../components/ui/CourseManagementModal';
import toast from 'react-hot-toast';

type Tab = 'available' | 'enrolled' | 'all_enrollments';

export default function CoursesPage() {
    const { state, logout, hasRole } = useAuth();
    const navigate = useNavigate();

    // Check Roles
    const isAdmin = hasRole('admin');

    // State
    const [activeTab, setActiveTab] = useState<Tab>('available');
    const [courses, setCourses] = useState<Course[]>([]);
    const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
    const [allEnrollments, setAllEnrollments] = useState<Enrollment[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    // Modal State regarding Enrollment
    const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
    const [isEnrollModalOpen, setIsEnrollModalOpen] = useState(false);

    // Modal State regarding Management (Admin)
    const [managingCourse, setManagingCourse] = useState<Course | null>(null);
    const [isManageModalOpen, setIsManageModalOpen] = useState(false);

    // Derived State
    const enrolledCourseIds = useMemo(() => {
        return new Set((enrollments || []).map(e => e.courseId));
    }, [enrollments]);

    const filteredCourses = useMemo(() => {
        return (courses || []).filter(c =>
            c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            c.code.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [courses, searchTerm]);

    // Load courses and enrollments on mount
    useEffect(() => {
        loadData();
    }, [isAdmin]); // Reload if role changes

    const loadData = async () => {
        try {
            setLoading(true);

            // Parallel fetch
            const promises: Promise<any>[] = [
                enrollmentApi.getCourses(isAdmin ? false : true), // Admin sees all (active=false too), Student sees only active
            ];

            // If Student (authenticated), fetch my enrollments
            if (state.isAuthenticated && !isAdmin) {
                promises.push(enrollmentApi.getMyEnrollments());
            } else {
                promises.push(Promise.resolve([]));
            }

            // If Admin, fetch all enrollments
            if (isAdmin) {
                promises.push(enrollmentApi.getEnrollments().catch(e => {
                    console.warn("Failed to fetch all enrollments (Admin)", e);
                    return [];
                }));
            } else {
                promises.push(Promise.resolve([]));
            }

            const results = await Promise.all(promises);
            const coursesData = results[0];
            const enrollmentsData = results[1];
            const allEnrollmentsData = results[2];

            setCourses(coursesData || []);
            setEnrollments(enrollmentsData || []);
            setAllEnrollments(allEnrollmentsData || []);

        } catch (err) {
            console.error('Error loading data:', err);
            toast.error('Failed to load courses. Please try again.');
            setCourses([]);
        } finally {
            setLoading(false);
        }
    };

    // --- Actions ---

    const openEnrollModal = (course: Course) => {
        if (!state.isAuthenticated) {
            toast.error('You must be logged in to enroll');
            return;
        }
        setSelectedCourse(course);
        setIsEnrollModalOpen(true);
    };

    const handleEnrollConfirm = async (courseId: string) => {
        try {
            await enrollmentApi.enrollUser(courseId);
            toast.success('Successfully enrolled in course!');
            const updatedEnrollments = await enrollmentApi.getMyEnrollments();
            setEnrollments(updatedEnrollments || []);
        } catch (err: any) {
            console.error('Error enrolling:', err);
            const message = err.response?.data?.message || 'Failed to enroll. Please try again.';
            toast.error(message);
            throw err;
        }
    };

    // Admin Actions
    const openCreateModal = () => {
        setManagingCourse(null);
        setIsManageModalOpen(true);
    };

    const openEditModal = (course: Course) => {
        setManagingCourse(course);
        setIsManageModalOpen(true);
    };

    const handleSaveCourse = async (data: CreateCourseRequest | UpdateCourseRequest) => {
        try {
            if (managingCourse) {
                await enrollmentApi.updateCourse(managingCourse.id, data);
                toast.success('Course updated successfully');
            } else {
                await enrollmentApi.createCourse(data as CreateCourseRequest);
                toast.success('Course created successfully');
            }
            loadData(); // Refetch
        } catch (err: any) {
            console.error('Error saving course:', err);
            const status = err.response?.status;
            if (status === 409) toast.error('Code already exists');
            else if (status === 403) toast.error('Access denied');
            else if (status === 404) toast.error('Course not found');
            else toast.error('Failed to save course');
            throw err;
        }
    };

    const handleToggleActive = async (course: Course) => {
        try {
            await enrollmentApi.updateCourse(course.id, { active: !course.active });
            toast.success(`Course ${course.active ? 'deactivated' : 'activated'} successfully`);
            loadData();
        } catch (err: any) {
            console.error('Error updating course status:', err);
            toast.error('Failed to update course status');
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
                                <p className="text-xs text-gray-500">
                                    {isAdmin ? 'Administrator' : 'Student'}
                                </p>
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
                <div className="mb-8 flex flex-col md:flex-row md:items-end md:justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900">{isAdmin ? 'Admin Dashboard' : 'Course Dashboard'}</h1>
                        <p className="mt-1 text-gray-500">{isAdmin ? 'Manage courses and enrollments.' : 'Manage your academic journey effectively.'}</p>
                    </div>
                    {isAdmin && (
                        <button
                            onClick={openCreateModal}
                            className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 shadow-sm flex items-center gap-2"
                        >
                            <span>+</span> Create Course
                        </button>
                    )}
                </div>

                {/* Tabs */}
                <div className="border-b border-gray-200 mb-8 overflow-x-auto">
                    <nav className="-mb-px flex space-x-8" aria-label="Tabs">
                        <button
                            onClick={() => setActiveTab('available')}
                            className={`${activeTab === 'available'
                                ? 'border-blue-500 text-blue-600'
                                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition-colors`}
                        >
                            {isAdmin ? 'Course Management' : 'Available Courses'}
                            <span className="ml-2 bg-gray-100 text-gray-600 py-0.5 px-2.5 rounded-full text-xs font-medium">
                                {(courses || []).length}
                            </span>
                        </button>

                        {!isAdmin && (
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
                        )}

                        {isAdmin && (
                            <button
                                onClick={() => setActiveTab('all_enrollments')}
                                className={`${activeTab === 'all_enrollments'
                                    ? 'border-blue-500 text-blue-600'
                                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                    } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition-colors`}
                            >
                                All Enrollments
                                <span className="ml-2 bg-purple-100 text-purple-600 py-0.5 px-2.5 rounded-full text-xs font-medium">
                                    {(allEnrollments || []).length}
                                </span>
                            </button>
                        )}
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
                        {/* Tab: Available / Managed Courses */}
                        {activeTab === 'available' && (
                            <div className="animate-fade-in-up space-y-6">
                                {/* Search Bar */}
                                <div className="max-w-md relative">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                        </svg>
                                    </div>
                                    <input
                                        type="text"
                                        className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Search courses by name or code..."
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                    />
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                    {filteredCourses.map((course) => {
                                        const isEnrolled = enrolledCourseIds.has(course.id);
                                        return (
                                            <div
                                                key={course.id}
                                                className={`bg-white rounded-xl shadow-sm border ${course.active ? 'border-gray-100' : 'border-red-200 bg-red-50'} hover:shadow-lg transition-all duration-300 flex flex-col h-full overflow-hidden group relative`}
                                            >
                                                {isAdmin && !course.active && (
                                                    <div className="absolute top-2 right-2 px-2 py-1 bg-red-100 text-red-800 text-xs font-bold rounded">INACTIVE</div>
                                                )}
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

                                                    {/* Actions */}
                                                    <div className="mt-auto pt-4">
                                                        {isAdmin ? (
                                                            <div className="flex gap-2">
                                                                <button
                                                                    onClick={() => openEditModal(course)}
                                                                    className="flex-1 py-2 px-3 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-medium rounded-md transition"
                                                                >
                                                                    Edit
                                                                </button>
                                                                <button
                                                                    onClick={() => handleToggleActive(course)}
                                                                    className={`flex-1 py-2 px-3 text-sm font-medium rounded-md transition ${
                                                                        course.active 
                                                                            ? 'bg-red-50 hover:bg-red-100 text-red-600' 
                                                                            : 'bg-green-50 hover:bg-green-100 text-green-600'
                                                                    }`}
                                                                >
                                                                    {course.active ? 'Deactivate' : 'Activate'}
                                                                </button>
                                                            </div>
                                                        ) : (
                                                            <button
                                                                onClick={() => openEnrollModal(course)}
                                                                disabled={isEnrolled}
                                                                className={`w-full py-2.5 px-4 rounded-lg font-medium text-sm transition-all focus:ring-2 focus:ring-offset-1 ${isEnrolled
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
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        );
                                    })}
                                    {filteredCourses.length === 0 && (
                                        <div className="col-span-full py-12 text-center bg-white rounded-xl border border-dashed border-gray-300">
                                            <p className="text-gray-500">No matching courses found.</p>
                                        </div>
                                    )}
                                </div>
                            </div>
                        )}

                        {/* Tab: My Enrollments (Student Only) */}
                        {!isAdmin && activeTab === 'enrolled' && (
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
                                        <h3 className="text-lg font-medium text-gray-900">No enrollments yet</h3>
                                        <button
                                            onClick={() => setActiveTab('available')}
                                            className="mt-6 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
                                        >
                                            Browse Courses
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Tab: All Enrollments (Admin Only) */}
                        {isAdmin && activeTab === 'all_enrollments' && (
                            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden animate-fade-in">
                                {(allEnrollments || []).length > 0 ? (
                                    <div className="overflow-x-auto">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-purple-50">
                                                <tr>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">User ID</th>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Email</th>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Course</th>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                                                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Enrolled Date</th>
                                                </tr>
                                            </thead>
                                            <tbody className="bg-white divide-y divide-gray-200">
                                                {allEnrollments.map((enrollment) => {
                                                    const course = courses.find(c => c.id === enrollment.courseId);
                                                    return (
                                                        <tr key={enrollment.id} className="hover:bg-gray-50 transition-colors">
                                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-mono">
                                                                {enrollment.userId}
                                                            </td>
                                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                                {enrollment.email || '-'}
                                                            </td>
                                                            <td className="px-6 py-4 whitespace-nowrap">
                                                                <div className="flex flex-col">
                                                                    <span className="text-sm font-medium text-gray-900">{course?.name || 'Unknown Course'}</span>
                                                                    <span className="text-xs text-gray-500">{course?.code || enrollment.courseId}</span>
                                                                </div>
                                                            </td>
                                                            <td className="px-6 py-4 whitespace-nowrap">
                                                                <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
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
                                    <div className="px-6 py-12 text-center text-gray-500">
                                        <p>No enrollments found.</p>
                                    </div>
                                )}
                            </div>
                        )}
                    </>
                )}
            </main>

            {/* Modals */}
            <EnrollmentModal
                isOpen={isEnrollModalOpen}
                onClose={() => setIsEnrollModalOpen(false)}
                onConfirm={handleEnrollConfirm}
                course={selectedCourse}
            />

            <CourseManagementModal
                isOpen={isManageModalOpen}
                onClose={() => setIsManageModalOpen(false)}
                onSave={handleSaveCourse}
                course={managingCourse}
            />
        </div>
    );
}
