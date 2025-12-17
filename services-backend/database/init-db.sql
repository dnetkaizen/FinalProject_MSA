-- Auth microservice
CREATE SCHEMA auth_schema;

-- IAM microservice
CREATE SCHEMA iam_schema;

-- Enrollment microservice
CREATE SCHEMA enrollment_schema;

--=======================================================================

CREATE TABLE auth_schema.mfa_otp (
    id          uuid PRIMARY KEY,
    user_id     varchar(255) NOT NULL,
    email       varchar(255) NOT NULL,
    otp_hash    varchar(255) NOT NULL,
    expires_at  timestamptz  NOT NULL,
    verified_at timestamptz  NULL,
    created_at  timestamptz  NOT NULL
);

CREATE INDEX idx_mfa_otp_user_valid
ON auth_schema.mfa_otp (user_id, expires_at, verified_at);

--=======================================================================

-- Crear el esquema si no existe
CREATE SCHEMA IF NOT EXISTS iam_schema;

-- Tabla de roles
CREATE TABLE IF NOT EXISTS iam_schema.roles (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de permisos
CREATE TABLE IF NOT EXISTS iam_schema.permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de relación usuario-rol
CREATE TABLE IF NOT EXISTS iam_schema.user_roles (
    user_id VARCHAR(255) NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (role_id) REFERENCES iam_schema.roles(id) ON DELETE CASCADE
);

-- Tabla de relación rol-permiso
CREATE TABLE IF NOT EXISTS iam_schema.role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES iam_schema.roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES iam_schema.permissions(id) ON DELETE CASCADE
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON iam_schema.user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON iam_schema.user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON iam_schema.role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON iam_schema.role_permissions(permission_id);

--=======================================================================

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
    email VARCHAR(255),
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


--==================================================================

--INSERT DE CURSOS:

ALTER TABLE enrollment_schema.courses
ALTER COLUMN id SET DEFAULT gen_random_uuid();

INSERT INTO enrollment_schema.courses (code, name) VALUES
('DSW-101', 'Diseño y Desarrollo de Software I'),
('DSW-102', 'Diseño y Desarrollo de Software II'),
('BD-201',  'Bases de Datos I'),
('BD-202',  'Bases de Datos II'),
('WEB-301', 'Desarrollo Web Frontend'),
('WEB-302', 'Desarrollo Web Backend'),
('MOB-401', 'Desarrollo de Aplicaciones Móviles'),
('QA-501',  'Testing y Aseguramiento de la Calidad'),
('ARQ-601', 'Arquitectura de Software'),
('SEG-701', 'Seguridad Informática');


--===============================================================

-- 1) Roles
INSERT INTO iam_schema.roles (id, name)
VALUES
  (gen_random_uuid(), 'student'),
  (gen_random_uuid(), 'operator'),
  (gen_random_uuid(), 'admin')
ON CONFLICT (name) DO NOTHING;

-- 2) Permissions
INSERT INTO iam_schema.permissions (id, name)
VALUES
  (gen_random_uuid(), 'courses:read'),
  (gen_random_uuid(), 'courses:create'),
  (gen_random_uuid(), 'courses:update'),
  (gen_random_uuid(), 'courses:delete'),
  (gen_random_uuid(), 'enrollments:create'),
  (gen_random_uuid(), 'enrollments:read:me'),
  (gen_random_uuid(), 'enrollments:read:all')
ON CONFLICT (name) DO NOTHING;

-- 3) role_permissions mappings
-- student: puede ver cursos, inscribirse, ver sus inscripciones
INSERT INTO iam_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM iam_schema.roles r
JOIN iam_schema.permissions p ON p.name IN ('courses:read','enrollments:create','enrollments:read:me')
WHERE r.name = 'student'
ON CONFLICT DO NOTHING;

-- operator: puede ver cursos y ver inscripciones globales (solo lectura)
INSERT INTO iam_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM iam_schema.roles r
JOIN iam_schema.permissions p ON p.name IN ('courses:read','enrollments:read:all')
WHERE r.name = 'operator'
ON CONFLICT DO NOTHING;

-- admin: todo
INSERT INTO iam_schema.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM iam_schema.roles r
JOIN iam_schema.permissions p ON p.name IN ('courses:read','courses:create','courses:update','courses:delete','enrollments:create','enrollments:read:me','enrollments:read:all')
WHERE r.name = 'admin'
ON CONFLICT DO NOTHING;
