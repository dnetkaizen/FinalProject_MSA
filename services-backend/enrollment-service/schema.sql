-- Schema para enrollment-service
CREATE SCHEMA IF NOT EXISTS enrollment_schema;

-- Tabla courses
CREATE TABLE IF NOT EXISTS enrollment_schema.courses (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla enrollments (alineada al modelo Java)
CREATE TABLE IF NOT EXISTS enrollment_schema.enrollments (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    course_id UUID NOT NULL,
    enrolled_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrollment_course
        FOREIGN KEY (course_id)
        REFERENCES enrollment_schema.courses(id),
    CONSTRAINT uq_user_course UNIQUE (user_id, course_id)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_enrollments_user_id
    ON enrollment_schema.enrollments(user_id);

CREATE INDEX IF NOT EXISTS idx_enrollments_course_id
    ON enrollment_schema.enrollments(course_id);