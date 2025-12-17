# Enrollment Service

Microservicio core de negocio encargado del catálogo de cursos y la gestión de inscripciones de alumnos.

## 📋 Funcionalidades

- **Cursos**: Listado y administración de oferta académica.
- **Inscripciones**: Proceso de matriculación de estudiantes.
- **Validación RBAC**: Comprobación de permisos específicos para operaciones de escritura/lectura.

## ⚙️ Configuración (.env)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_SCHEMA` | Esquema DB | `enrollment_schema` |
| `JWT_SECRET` | Igual que en Auth Service | `tu_secreto_compartido` |
| `IAM_SERVICE_URL` | URL de IAM Service | `http://iam-service:8082` o `http://localhost:8082` |
| `DB_HOST` | Host Supabase | `aws-0-...pooler.supabase.com` |

## 🛠 Base de Datos (Especial)
Este microservicio depende del script maestro de SQL para todo el sistema Kaizen.
Para mayor facilidad, se ha duplicado en la carpeta raíz del backend:
`../database/init-db.sql`

Este script debe ejecutarse en Supabase antes que cualquier otro servicio, ya que inicializa:
1. Esquemas (`auth`, `iam`, `enrollment`).
2. Tablas de todos los servicios.
3. Datos semilla (Roles, Permisos y Cursos iniciales).

## 🚀 Ejecución

```bash
./mvnw spring-boot:run
```
o mediante el docker-compose en la carpeta superior.
