import http from './http';

// Types
export interface Course {
    id: string;
    code: string;
    name: string;
    description?: string;
    credits?: number;
    active: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface Enrollment {
    id: string;
    userId: string;
    courseId: string;
    enrolledAt: string;
    status: 'active' | 'completed' | 'dropped';
    grade?: number;
    course?: Course;
}

export interface CreateCourseRequest {
    code: string;
    name: string;
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
    userId: string;
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
        const response = await http.enrollment.get<Course[]>('/enrollment/courses', {
            params: { active: activeOnly || undefined }
        });
        return response.data;
    },

    /**
     * Get course by ID
     */
    async getCourseById(courseId: string): Promise<Course> {
        const response = await http.enrollment.get<Course>(`/enrollment/courses/${courseId}`);
        return response.data;
    },

    /**
     * Create a new course
     */
    async createCourse(data: CreateCourseRequest): Promise<Course> {
        const response = await http.enrollment.post<Course>('/enrollment/courses', data);
        return response.data;
    },

    /**
     * Update a course
     */
    async updateCourse(courseId: string, data: UpdateCourseRequest): Promise<Course> {
        const response = await http.enrollment.put<Course>(`/enrollment/courses/${courseId}`, data);
        return response.data;
    },

    /**
     * Delete a course
     */
    async deleteCourse(courseId: string): Promise<void> {
        await http.enrollment.delete(`/enrollment/courses/${courseId}`);
    },

    /**
     * Get all enrollments
     */
    async getEnrollments(): Promise<Enrollment[]> {
        const response = await http.enrollment.get<Enrollment[]>('/enrollment/enrollments');
        return response.data;
    },

    /**
     * Get enrollments by user ID
     */
    async getUserEnrollments(userId: string): Promise<Enrollment[]> {
        const response = await http.enrollment.get<Enrollment[]>(`/enrollment/users/${userId}/enrollments`);
        return response.data;
    },

    /**
     * Get enrollments by course ID
     */
    async getCourseEnrollments(courseId: string): Promise<Enrollment[]> {
        const response = await http.enrollment.get<Enrollment[]>(`/enrollment/courses/${courseId}/enrollments`);
        return response.data;
    },

    /**
     * Enroll user in a course
     */
    async enrollUser(data: EnrollRequest): Promise<Enrollment> {
        const response = await http.enrollment.post<Enrollment>('/enrollment/enrollments', data);
        return response.data;
    },

    /**
     * Update an enrollment
     */
    async updateEnrollment(enrollmentId: string, data: UpdateEnrollmentRequest): Promise<Enrollment> {
        const response = await http.enrollment.put<Enrollment>(`/enrollment/enrollments/${enrollmentId}`, data);
        return response.data;
    },

    /**
     * Delete an enrollment (unenroll)
     */
    async deleteEnrollment(enrollmentId: string): Promise<void> {
        await http.enrollment.delete(`/enrollment/enrollments/${enrollmentId}`);
    },

    /**
     * Check if user is enrolled in a course
     */
    async isUserEnrolled(userId: string, courseId: string): Promise<boolean> {
        try {
            const enrollments = await this.getUserEnrollments(userId);
            return enrollments.some(e => e.courseId === courseId && e.status === 'active');
        } catch (error) {
            console.error('Error checking enrollment:', error);
            return false;
        }
    },
};
