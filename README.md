# Club Campestre Villa Katy

Sistema de gestión del club: **backend** en Spring Boot (API REST) y **frontend** en React + Vite.

```
ClubCampestre/
├── backend/    API REST (Java 21, Spring Boot 4, SQL Server)
├── frontend/   Aplicación web (React 19, Vite 8)
└── .env        Variables del backend (no se sube a Git)
```

---

## 1. Programas que hay que instalar

| Programa | Versión | Para qué | Descarga |
|---|---|---|---|
| **JDK (Java)** | **21** (probado con 21.0.12) | Compilar y ejecutar el backend | [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) o [Eclipse Temurin 21](https://adoptium.net/) |
| **SQL Server** | 2019 o superior (Express sirve) | Base de datos `villa_Katy` | [SQL Server Express](https://www.microsoft.com/es-es/sql-server/sql-server-downloads) |
| **SQL Server Management Studio** (opcional) | Última | Crear la base y correr los scripts `.sql` | [SSMS](https://learn.microsoft.com/es-es/ssms/download-sql-server-management-studio-ssms) |
| **Node.js** | **20.19+ o 22.12+** (probado con 24.14.1) | Ejecutar el frontend | [nodejs.org](https://nodejs.org/) |
| **npm** | Viene con Node (probado con 11.11.0) | Instalar las librerías del frontend | — |
| **Git** | Cualquiera reciente | Clonar el proyecto | [git-scm.com](https://git-scm.com/) |

> **Maven no hace falta instalarlo.** El backend trae el *Maven Wrapper* (`mvnw` / `mvnw.cmd`), que descarga solo Maven **3.9.16** la primera vez.

Para comprobar lo instalado:

```bash
java -version    # debe decir 21
node -v          # v20.19+ / v22.12+ / v24
npm -v
```

---

## 2. Backend

### Tecnologías y versiones

Definidas en [backend/pom.xml](backend/pom.xml):

| Librería | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 4.0.0 | Framework (web, JPA, validación, seguridad) |
| Spring Boot Starter Web | 4.0.0 | API REST |
| Spring Boot Starter Data JPA (Hibernate 7.1) | 4.0.0 | Acceso a la base de datos |
| Spring Boot Starter Validation (Hibernate Validator 9) | 4.0.0 | Validaciones `@NotBlank`, `@Size`, `@Email`… |
| Spring Boot Starter Security | 4.0.0 | Seguridad de los endpoints |
| Microsoft SQL Server JDBC (`mssql-jdbc`) | 13.4.0.jre11 | Driver de SQL Server |
| JJWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) | 0.12.6 | Tokens JWT del login |
| spring-dotenv | 4.0.0 | Leer variables desde el archivo `.env` |
| Lombok | 1.18.42 (la que trae Spring Boot) | Getters/setters automáticos |
| Spring Boot Starter Test (JUnit 5 + Mockito) | 4.0.0 | Pruebas |
| Maven (wrapper) | 3.9.16 | Compilación |

> Si usas IntelliJ, VS Code o Eclipse, instala el plugin de **Lombok** para que el editor no marque errores en los getters y setters.

### Base de datos

1. En SQL Server, crea la base de datos **`villa_Katy`** con sus tablas.
   - El backend usa `spring.jpa.hibernate.ddl-auto=validate`: **no crea tablas**, solo revisa que existan. Si falta alguna, el backend no arranca.
2. Corre los scripts de [backend/database/](backend/database/). Por ejemplo, [historial_empleado.sql](backend/database/historial_empleado.sql) agrega el estado `vacaciones` y crea la tabla `historial_empleado`.
3. Activa **TCP/IP** en *SQL Server Configuration Manager*, en el puerto **1433** por defecto.

### Archivo `.env`

El backend lee sus variables de un archivo `.env` en la raíz del proyecto (`ClubCampestre/.env`) o dentro de `backend/`:

```properties
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=villa_Katy;user=USUARIO;password=CLAVE;encrypt=true;trustServerCertificate=true
JWT_SECRET=una-clave-larga-de-al-menos-32-caracteres
```

- El usuario y la contraseña de SQL Server van **dentro de `DB_URL`**, porque `application.properties` no tiene variables aparte para ellos.
- `JWT_SECRET` debe tener **mínimo 32 caracteres**. Si es más corta, JJWT falla al iniciar.
- **El `.env` nunca se sube a Git**, porque tiene contraseñas.

### Ejecutar

Desde la carpeta `backend/`:

```bash
# Windows (PowerShell o CMD)
.\mvnw.cmd spring-boot:run

# Git Bash / Linux / macOS
./mvnw spring-boot:run
```

La API queda en **http://localhost:8080/api**, por ejemplo `http://localhost:8080/api/empleados`.

Solo se permiten peticiones desde `http://localhost:*` y `http://127.0.0.1:*` (configurado en [CorsConfig.java](backend/src/main/java/com/clubcampestre/config/CorsConfig.java)).

### Pruebas

```bash
./mvnw test -Dtest='*ValidacionTest*,*ValidacionTest$*'
```

Estas pruebas no necesitan la base de datos. `./mvnw test` sin filtro también corre `ClubCampestreApplicationTests`, que sí necesita SQL Server encendido y el `.env` configurado.

---

## 3. Frontend

### Tecnologías y versiones

Definidas en [frontend/package.json](frontend/package.json) (versiones instaladas):

| Librería | Versión | Uso |
|---|---|---|
| React / React DOM | 19.2.5 | Interfaz |
| React Router DOM | 7.18.3 | Navegación entre páginas |
| Vite | 8.0.10 | Servidor de desarrollo y compilación |
| @vitejs/plugin-react | 6.0.1 | Soporte de React en Vite |
| Tailwind CSS + @tailwindcss/vite | 4.2.4 | Estilos |
| MUI (`@mui/material`, `@mui/icons-material`) | 7.3.11 | Componentes de interfaz |
| Emotion (`@emotion/react`, `@emotion/styled`) | 11.14 | Motor de estilos que usa MUI |
| Toolpad Core | 0.16.0 | Componentes del panel de administración |
| lucide-react | 1.45.0 | Íconos |
| Firebase | 12.18.0 | Login de clientes |
| ESLint | 10.2.1 | Revisión de código |
| Vitest | 5.0.2 | Pruebas |

### Archivo `frontend/.env`

```properties
VITE_FIREBASE_API_KEY=...
VITE_FIREBASE_AUTH_DOMAIN=...
VITE_FIREBASE_PROJECT_ID=...
VITE_FIREBASE_STORAGE_BUCKET=...
VITE_FIREBASE_MESSAGING_SENDER_ID=...
VITE_FIREBASE_APP_ID=...

# Opcional: si no se pone, usa http://localhost:8080/api
VITE_API_URL=http://localhost:8080/api
```

Los valores de Firebase salen de la consola de Firebase: *Configuración del proyecto → Tus apps*.

### Ejecutar

Desde la carpeta `frontend/`:

```bash
npm install      # solo la primera vez o cuando cambie package.json
npm run dev
```

Abre **http://localhost:5173**.

| Comando | Qué hace |
|---|---|
| `npm run dev` | Servidor de desarrollo con recarga automática |
| `npm run build` | Compila para producción en `frontend/dist/` |
| `npm run preview` | Sirve la versión compilada para revisarla |
| `npm run lint` | Revisa el código con ESLint |
| `npm test` | Corre las pruebas con Vitest |

---

## 4. Orden para levantar todo

1. Encender **SQL Server**.
2. Terminal 1, en `backend/`: `.\mvnw.cmd spring-boot:run`. Esperar a que diga `Started ClubCampestreApplication`.
3. Terminal 2, en `frontend/`: `npm run dev`.
4. Abrir **http://localhost:5173**.

## 5. Problemas comunes

| Error | Causa probable |
|---|---|
| `Could not resolve placeholder 'DB_URL'` | No existe el `.env` o no está en la raíz del proyecto ni en `backend/`. |
| `The TCP/IP connection to the host localhost, port 1433 has failed` | SQL Server apagado o TCP/IP desactivado. |
| `Schema-validation: missing table [...]` | Falta crear esa tabla o correr los scripts de `backend/database/`. |
| `WeakKeyException` al iniciar | `JWT_SECRET` tiene menos de 32 caracteres. |
| `invalid source release: 21` | Se está usando un Java anterior al 21 (`java -version`). |
| El frontend muestra errores de red / CORS | El backend no está corriendo en el puerto 8080, o se abrió el frontend con una dirección que no es `localhost`. |
