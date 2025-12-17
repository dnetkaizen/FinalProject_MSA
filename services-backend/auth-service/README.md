# Auth Service

Microservicio encargado de la autenticación, gestión de sesiones y seguridad MFA (Multi-Factor Authentication).

## 📋 Funcionalidades Principales

- **Login de Usuarios**: Autenticación mediante credenciales (email/password).
- **Emisión de JWT**: Generación de Access Tokens y Refresh Tokens (HS256).
- **MFA (Multi-Factor Authentication)**: Envío de códigos OTP vía correo electrónico (integración con Mailtrap).
- **Validación de Identidad**: Integración con Firebase Admin SDK.

## ⚙️ Configuración (.env)

Es obligatorio crear un archivo `.env` en esta carpeta.

### Variables de Base de Datos (Supabase)
Se recomienda encarecidamente usar el **Session Pooler** de Supabase para evitar agotar las conexiones.

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_HOST` | Host de Supabase | `aws-0-us-east-1.pooler.supabase.com` |
| `DB_PORT` | Puerto del Pooler | `6543` |
| `DB_NAME` | Base de datos | `postgres` |
| `DB_USER` | Usuario | `postgres.myproject` |
| `DB_PASSWORD`| Contraseña | `tu_password_seguro` |
| `DB_SCHEMA` | Esquema | `auth_schema` |

### Otras Variables Criticas
- **JWT**: `JWT_SECRET` (Mínimo 32 caracteres) y `JWT_ISSUER`.
- **Firebase**: `FIREBASE_PROJECT_ID` y `FIREBASE_SERVICE_ACCOUNT_JSON`.
- **Mailtrap**: `MAILTRAP_HOST`, `MAILTRAP_PORT`, `MAILTRAP_USERNAME`, `MAILTRAP_PASSWORD`.

## 🛠 Base de Datos
Antes de arrancar, asegúrate de ejecutar el script SQL de inicialización que se encuentra en:
`../database/init-db.sql`

## 🚀 Ejecución

Lanzar vía Docker (desde la raíz de `services-backend`):
```bash
docker compose up --build -d
```

O ejecutar localmente:
```bash
./mvnw spring-boot:run
```
