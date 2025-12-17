# Frontend Application

Interfaz de usuario moderna construida con **React**, **TypeScript** y **Vite** para el sistema universitario Kaizen.

## 📋 Funcionalidades

- **Autenticación Segura**:
  - Login con credenciales.
  - Soporte MFA (OTP).
  - Integración con Firebase (Client SDK).
  - Manejo de sesión con Access/Refresh Tokens.
- **Dashboard Dinámico**: Vistas personalizadas según el rol (Admin, Student, Operator).
- **Gestión Académica**: Visualización de cursos e inscripciones en tiempo real.
- **Seguridad**: Renovación automática de sesión e interceptores de Axios para manejo de Tokens.

## 🛠 Tecnologías

- **React 18** + **Vite**
- **TypeScript**
- **Tailwind CSS**
- **Axios** (Consumo de APIs)
- **Firebase** (Manejo de estados de autenticación)

## ⚙️ Configuración

Este proyecto requiere un archivo `.env` en la raíz de la carpeta `frontend`. Utiliza el archivo [.env.example](./.env.example) como referencia.

### Variables Requeridas

| Variable | Descripción |
|----------|-------------|
| `VITE_FIREBASE_API_KEY` | API Key de tu proyecto Firebase |
| `VITE_FIREBASE_AUTH_DOMAIN` | Auth Domain de Firebase |
| `VITE_FIREBASE_PROJECT_ID` | Project ID de Firebase |
| `VITE_AUTH_API_URL` | URL del Auth Service (`http://localhost:8081`) |
| `VITE_IAM_API_URL` | URL del IAM Service (`http://localhost:8082`) |
| `VITE_ENROLLMENT_API_URL` | URL del Enrollment Service (`http://localhost:8083`) |

## 🚀 Despliegue con Docker

Para levantar el frontend de manera aislada u orquestada:

1. Crea y configura tu archivo `.env`.
2. Ejecuta el comando:
   ```bash
   docker compose up --build -d
   ```
3. Accede a `http://localhost:3000`.

## 🏃‍♂️ Desarrollo Local (Sin Docker)

1. Instala las dependencias:
   ```bash
   npm install
   ```
2. Inicia el servidor de desarrollo:
   ```bash
   npm run dev
   ```
