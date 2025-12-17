# IAM Service (Identity & Access Management)

Microservicio responsable de el control de acceso basado en roles (**RBAC**). Define quién puede hacer qué en el sistema.

## 📋 Funcionalidades

- **RBAC**: Gestión de Roles (`admin`, `student`, `operator`) y Permisos (`courses:create`, `enrollments:read`, etc.).
- **Asignación de Roles**: Vinculación de usuarios externos (procedentes de Auth/Firebase) con roles internos.
- **Validación de Tokens**: Verificación de firmas JWT para asegurar la integridad de las peticiones.

## ⚙️ Configuración (.env)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_SCHEMA` | Esquema DB | `iam_schema` |
| `JWT_SECRET` | Igual que en Auth Service | `tu_secreto_compartido` |
| `JWT_ISSUER` | Igual que en Auth Service | `auth-service` |
| `DB_HOST` | Host Supabase | `aws-0-...pooler.supabase.com` |
| `DB_PORT` | Puerto Supabase | `6543` |

## 🛠 Base de Datos
Este servicio depende del esquema `iam_schema` definido en:
`../database/init-db.sql`

## 🚀 Ejecución

```bash
./mvnw spring-boot:run
```
o mediante el docker-compose en la carpeta superior.
