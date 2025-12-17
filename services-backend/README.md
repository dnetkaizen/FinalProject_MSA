# Backend Microservices Orchestration

Este directorio contiene los microservicios core de la plataforma Kaizen. Todos están desarrollados con **Java 17** y **Spring Boot 3.x**.

## 🏗 Servicios Incluidos

1.  **Auth Service**: Puerto `8081`. Gestiona Login, MFA y generación de JWT.
2.  **IAM Service**: Puerto `8082`. Gestiona Roles y Permisos (RBAC).
3.  **Enrollment Service**: Puerto `8083`. Gestiona Cursos e Inscripciones.

## ⚙️ Configuración Global de Variables

Cada microservicio requiere su propio archivo `.env` basado en el `.env.example` dentro de su respectiva carpeta. Aquí tienes una guía de las variables comunes y específicas:

### Variables Comunes (Base de Datos & JWT)
Estas variables deben estar presentes en los 3 servicios:

| Variable | Descripción | Ejemplo (Supabase) |
|----------|-------------|---------|
| `DB_HOST` | Host de la base de datos Supabase | `aws-0-us-east-1.pooler.supabase.com` |
| `DB_PORT` | Puerto (6543 para Session Pooler) | `6543` |
| `DB_NAME` | Nombre de la base de datos | `postgres` |
| `DB_USER` | Usuario (formato `postgres.xxxx`) | `postgres.myproject` |
| `DB_PASSWORD`| Contraseña del proyecto | `tu_password_seguro` |
| `JWT_SECRET` | Clave secreta para firmar tokens | `32_chars_at_least_secret_key` |
| `JWT_ISSUER` | Emisor del token | `auth-service` |

### Variables Específicas

#### Auth Service (`auth-service/`)
- `DB_SCHEMA`: `auth_schema`
- `MAILTRAP_HOST`: `sandbox.smtp.mailtrap.io`
- `MAILTRAP_PORT`: `2525`
- `MAILTRAP_USERNAME`: Tu usuario de Mailtrap
- `MAILTRAP_PASSWORD`: Tu contraseña de Mailtrap
- `FIREBASE_PROJECT_ID`: ID de tu proyecto Firebase
- `FIREBASE_SERVICE_ACCOUNT_JSON`: El JSON de la cuenta de servicio (todo el contenido en una línea o como string)

#### IAM Service (`iam-service/`)
- `DB_SCHEMA`: `iam_schema`

#### Enrollment Service (`enrollment-service/`)
- `DB_SCHEMA`: `enrollment_schema`

---

## 🚀 Levantamiento con Docker

Para levantar todos los microservicios backend al mismo tiempo:

1. Asegúrate de haber ejecutado el `init-db.sql` ubicado en la carpeta `database` en tu instancia de Supabase.
2. Asegúrate de que cada carpeta (`auth-service`, `iam-service`, `enrollment-service`) tenga su archivo `.env` basado en el `.env.example` correspondiente.
3. Ejecución:
   ```bash
   docker compose up --build -d
   ```

### 🔑 Nota Importante: Crear el primer Administrador
Por defecto, todos los usuarios se registran con el rol `student`. Para obtener acceso total:
1. Regístrate normalmente en la aplicación.
2. Copia tu **UID de Firebase**.
3. Sigue las instrucciones en el [README de IAM Service](./iam-service/README.md#🔑-otorgar-rol-de-administrador-primer-usuario) para promover tu cuenta a `admin` vía SQL.

## 🛠 Script de Base de Datos
El script para inicializar toda la base de datos (esquemas, tablas y datos semilla) se encuentra de forma accesible en:
`database/init-db.sql`

Cópialo y ejecútalo en el editor SQL de Supabase antes de iniciar los servicios.
