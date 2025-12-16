import React, { useState } from 'react';
import { Modal } from './Modal';
import { useAuth } from '../../contexts/AuthContext';
import { Course } from '../../services/enrollmentApi';

interface EnrollmentModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: (courseId: string) => Promise<void>;
    course: Course | null;
}

export const EnrollmentModal: React.FC<EnrollmentModalProps> = ({ isOpen, onClose, onConfirm, course }) => {
    const { state } = useAuth();
    const [fullName, setFullName] = useState('');
    const [phone, setPhone] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    if (!course) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!fullName.trim() || !phone.trim()) {
            setError('Please fill in all required fields');
            return;
        }

        try {
            setLoading(true);
            await onConfirm(course.id);
            // Reset form
            setFullName('');
            setPhone('');
            onClose();
        } catch (err) {
            // Error is handled by parent, but we stop loading state
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={`Enroll in ${course.name}`}>
            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">
                        {error}
                    </div>
                )}

                <div className="bg-blue-50 p-4 rounded-md mb-4">
                    <p className="text-sm text-blue-800">
                        You are about to enroll in <strong>{course.code}: {course.name}</strong>.
                    </p>
                    {course.credits && (
                        <p className="text-xs text-blue-600 mt-1">
                            Credits: {course.credits}
                        </p>
                    )}
                </div>

                <div>
                    <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                        Email Address
                    </label>
                    <input
                        type="email"
                        id="email"
                        disabled
                        value={state.email || ''}
                        className="mt-1 block w-full rounded-md border-gray-300 bg-gray-100 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm px-3 py-2 border"
                    />
                </div>

                <div>
                    <label htmlFor="fullName" className="block text-sm font-medium text-gray-700">
                        Full Name <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="text"
                        id="fullName"
                        required
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        placeholder="John Doe"
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm px-3 py-2 border"
                    />
                </div>

                <div>
                    <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
                        Phone Number <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="tel"
                        id="phone"
                        required
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        placeholder="+1 (555) 000-0000"
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm px-3 py-2 border"
                    />
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
                        {loading ? 'Processing...' : 'Confirm Enrollment'}
                    </button>
                </div>
            </form>
        </Modal>
    );
};
