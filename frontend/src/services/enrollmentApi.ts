import http from './http';

// Types
export interface Course {
    id: string;
    code: string;
    name: string;
    active: boolean;
    // description and credits might not be returned by minimal backend
    description?: string;
    credits?: number;
    createdAt?: string; // Optional in case backend doesn't return
    updatedAt?: string;
}

export interface Enrollment {
    id: string;
    userId: string;
    courseId: string;
    enrolledAt: string; // Backend might use createdAt
    status: 'active' | 'completed' | 'dropped'; // Backend might generic status
    grade?: number;
    course?: Course;
}

export interface CreateCourseRequest {
    code: string;
    name: string;
    // Optional additional fields although backend might ignore them
    description?: string;
    credits?: number;
    active?: boolean;
}

export interface UpdateCourseRequest {
    code?: string;
    name?: string;
    description?: string;
    credits?: number;
    active?: boolean;
}

export interface EnrollRequest {
    // userId removed - taken from token
    courseId: string;
}

export interface UpdateEnrollmentRequest {
    status?: 'active' | 'completed' | 'dropped';
    grade?: number;
}

/**
 * Enrollment API service
 */
export const enrollmentApi = {
    /**
     * Get all courses
     */
    async getCourses(activeOnly: boolean = false): Promise<Course[]> {
        // Backend maps to /courses
        const response = await http.enrollment.get<Course[]>('/courses', {
            params: { active: activeOnly || undefined }
        });
        return response.data;
    },

    /**
     * Get course by ID
     */
    async getCourseById(courseId: string): Promise<Course> {
        const response = await http.enrollment.get<Course>(`/courses/${courseId}`);
        return response.data;
    },

    /**
     * Create a new course
     */
    async createCourse(data: CreateCourseRequest): Promise<Course> {
        const response = await http.enrollment.post<Course>('/courses', data);
        return response.data;
    },

    /**
     * Update a course
     */
    async updateCourse(courseId: string, data: UpdateCourseRequest): Promise<Course> {
        const response = await http.enrollment.put<Course>(`/courses/${courseId}`, data);
        return response.data;
    },

    /**
     * Delete a course
     */
    async deleteCourse(courseId: string): Promise<void> {
        await http.enrollment.delete(`/courses/${courseId}`);
    },

    /**
     * Get all enrollments (Admin?)
     */
    async getEnrollments(): Promise<Enrollment[]> {
        const response = await http.enrollment.get<Enrollment[]>('/enrollments');
        return response.data;
    },

    /**
     * Get MY enrollments (Authenticated User)
     * Replaces getUserEnrollments(userId)
     */
    async getMyEnrollments(): Promise<Enrollment[]> {
        // Backend maps to /enrollments/me
        const response = await http.enrollment.get<Enrollment[]>('/enrollments/me');
        return response.data;
    },

    /**
     * Get enrollments by course ID
     */
    async getCourseEnrollments(courseId: string): Promise<Enrollment[]> {
        const response = await http.enrollment.get<Enrollment[]>(`/courses/${courseId}/enrollments`);
        return response.data;
    },

    /**
     * Enroll user in a course
     */
    async enrollUser(courseId: string): Promise<Enrollment> {
        const data: EnrollRequest = { courseId };
        // Backend maps to /enrollments
        const response = await http.enrollment.post<Enrollment>('/enrollments', data);
        return response.data;
    },

    /**
     * Update an enrollment
     */
    async updateEnrollment(enrollmentId: string, data: UpdateEnrollmentRequest): Promise<Enrollment> {
        const response = await http.enrollment.put<Enrollment>(`/enrollments/${enrollmentId}`, data);
        return response.data;
    },

    /**
     * Delete an enrollment (unenroll)
     */
    async deleteEnrollment(enrollmentId: string): Promise<void> {
        await http.enrollment.delete(`/enrollments/${enrollmentId}`);
    },

    /**
     * Check if I am enrolled in a course
     */
    async isEnrolled(courseId: string): Promise<boolean> {
        try {
            const enrollments = await this.getMyEnrollments();
            // Assuming status check is relevant, or existence is enough
            return enrollments.some(e => e.courseId === courseId);
        } catch (error) {
            console.error('Error checking enrollment:', error);
            return false;
        }
    },
};
