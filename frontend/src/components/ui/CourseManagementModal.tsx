import React, { useState, useEffect } from 'react';
import { Modal } from './Modal';
import { Course, CreateCourseRequest, UpdateCourseRequest } from '../../services/enrollmentApi';

interface CourseManagementModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: (data: CreateCourseRequest | UpdateCourseRequest) => Promise<void>;
    course?: Course | null; // If present, we are editing
}

export const CourseManagementModal: React.FC<CourseManagementModalProps> = ({ isOpen, onClose, onSave, course }) => {
    const isEditing = !!course;
    const [code, setCode] = useState('');
    const [name, setName] = useState('');
    const [active, setActive] = useState(true);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Reset or populate form when opening
    useEffect(() => {
        if (isOpen) {
            if (course) {
                setCode(course.code);
                setName(course.name);
                setActive(course.active);
            } else {
                setCode('');
                setName('');
                setActive(true);
            }
            setError(null);
        }
    }, [isOpen, course]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!code.trim() || !name.trim()) {
            setError('Please fill in all required fields');
            return;
        }

        try {
            setLoading(true);
            const data = { code, name, active };
            await onSave(data);
            onClose();
        } catch (err) {
            // Error is handled/toast by parent, but we stop loading state
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={isEditing ? `Edit Course: ${course?.code}` : 'Create New Course'}
        >
            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">
                        {error}
                    </div>
                )}

                <div>
                    <label htmlFor="code" className="block text-sm font-medium text-gray-700">
                        Course Code <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="text"
                        id="code"
                        required
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        placeholder="e.g. CS-101"
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm px-3 py-2 border"
                    />
                </div>

                <div>
                    <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                        Course Name <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="text"
                        id="name"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="e.g. Intro to Computer Science"
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm px-3 py-2 border"
                    />
                </div>

                <div className="flex items-center">
                    <input
                        id="active"
                        type="checkbox"
                        checked={active}
                        onChange={(e) => setActive(e.target.checked)}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                    />
                    <label htmlFor="active" className="ml-2 block text-sm text-gray-900">
                        Active (Visible to students)
                    </label>
                </div>

                <div className="mt-6 flex justify-end gap-3">
                    <button
                        type="button"
                        onClick={onClose}
                        className="inline-flex justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={loading}
                        className="inline-flex justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {loading ? 'Saving...' : (isEditing ? 'Save Changes' : 'Create Course')}
                    </button>
                </div>
            </form>
        </Modal>
    );
};
