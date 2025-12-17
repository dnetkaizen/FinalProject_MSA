# IAM Service (Identity & Access Management)

Microservicio responsable de el control de acceso basado en roles (**RBAC**). Define quién puede hacer qué en el sistema.

## 📋 Funcionalidades

- **RBAC**: Gestión de Roles (`admin`, `student`, `operator`) y Permisos (`courses:create`, `enrollments:read`, etc.).
- **Asignación de Roles**: Vinculación de usuarios externos (procedentes de Auth/Firebase) con roles internos.
- **Validación de Tokens**: Verificación de firmas JWT para asegurar la integridad de las peticiones.

## 🚀 Endpoints Principales

| Método | Endpoint | Descripción | Auth Requerida |
|--------|----------|-------------|----------------|
| GET | `/iam/users/{userId}/roles` | Obtiene los roles de un usuario. | Sí |
| POST | `/iam/users/{userId}/roles` | Asigna un nuevo rol a un usuario. | Sí (Admin) |
| GET | `/iam/users/{userId}/roles/permissions` | Obtiene permisos consolidados del usuario. | Sí |


## 🔑 Otorgar Rol de Administrador (Primer Usuario)

Dado que por defecto todos los nuevos usuarios registrados obtienen el rol `student`, el primer administrador debe promoverse manualmente. Tienes dos formas de hacerlo:

### Opción 1: Mediante SQL (Recomendado para el primer admin)
Ejecuta el siguiente script en el editor SQL de Supabase, reemplazando `'TU_FIREBASE_UID'` por el ID que aparece en la consola de Firebase o en tu tabla de usuarios:

```sql
INSERT INTO iam_schema.user_roles (user_id, role_id)
SELECT 'TU_FIREBASE_UID', r.id
FROM iam_schema.roles r
WHERE r.name = 'admin'
ON CONFLICT (user_id, role_id) DO NOTHING;
```

### Opción 2: Mediante API (Postman)
Si ya tienes un usuario con permisos de admin, puedes usar este endpoint:

- **URL**: `POST http://localhost:8082/iam/users/{userId}/roles`
- **Headers**: `Authorization: Bearer <JWT_TOKEN_ADMIN>`
- **Body**:
```json
{
  "role": "admin"
}
```

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
