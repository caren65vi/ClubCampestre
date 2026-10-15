# ENDPOINTS – API REST del Club Campestre Villa Katy

Evidencias GA7-220501096-AA5-EV03 (diseño y desarrollo de servicios web – proyecto) y AA5-EV04 (API del proyecto).

- **URL base local:** `http://localhost:8080/api`
- **Formato:** JSON (`Content-Type: application/json`)
- **Seguridad:** todas las rutas, excepto `/api/auth/**`, exigen el encabezado `Authorization: Bearer <token>`.
  El token se obtiene con `POST /api/auth/login`. Sin token la API responde **403**.
- **Errores:** todas las respuestas de error tienen la forma `{ "error": "mensaje" }`.

| Código | Significado en esta API |
|---|---|
| 200 | Operación exitosa |
| 400 | Datos inválidos (validaciones `@Valid` o reglas de negocio) |
| 401 | Usuario o contraseña incorrectos |
| 403 | Falta el token o no es válido |
| 404 | El registro no existe |
| 409 | Registro duplicado (por ejemplo, documento de empleado ya registrado) |

---

## 1. Autenticación – `/api/auth`

| Método | Ruta | Descripción | Cuerpo | Respuestas |
|---|---|---|---|---|
| POST | `/api/auth/login` | Inicia sesión de un colaborador y devuelve el token JWT. | `{ "nombreUsuario": "...", "contrasena": "..." }` | 200, 401 |

Respuesta 200:

```json
{ "idUsuario": 6, "nombreUsuario": "admin.pruebas", "rol": "Administrador", "token": "eyJhbGciOi..." }
```

## 2. Empleados – `/api/empleados` (RF-0110 a RF-0114)

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/empleados` | Lista todos los empleados. Filtros opcionales: `?nombre=`, `?idArea=`, `?estado=activo\|vacaciones\|inactivo`. | 200, 400 (estado inválido), 404 (sin coincidencias con filtros) |
| GET | `/api/empleados/{numeroDocumento}` | Consulta un empleado. | 200, 404 |
| POST | `/api/empleados` | Registra un empleado (queda **activo**). | 200, 400, 409 |
| PUT | `/api/empleados/{numeroDocumento}` | Actualiza los datos (el documento no cambia). | 200, 400, 404 |
| PATCH | `/api/empleados/{numeroDocumento}/estado` | Cambia el estado con fecha efectiva y motivo. | 200, 400, 404 |
| DELETE | `/api/empleados/{numeroDocumento}` | Baja lógica: el empleado pasa a **inactivo** y se conserva su historial. | 200, 404 |
| GET | `/api/empleados/{numeroDocumento}/historial` | Bitácora de cambios del empleado. | 200, 404 |

Cuerpo de POST / PUT:

```json
{
  "numeroDocumento": "1098765432",
  "idTipoDocumento": 1,
  "primerNombre": "Andrea",
  "segundoNombre": "Paola",
  "primerApellido": "Ríos",
  "segundoApellido": "Castaño",
  "telefono": "3124567890",
  "indicativo": "+57",
  "correo": "andrea.rios@villakaty.com",
  "idCargo": 6,
  "fechaContratacion": "2026-10-01",
  "salario": 1750905,
  "crearUsuario": false
}
```

Validaciones: documento, tipo de documento, primer nombre, primer apellido, cargo, fecha de contratación y salario son
obligatorios; el salario debe ser mayor a 0; el correo debe ser válido; celular y correo son obligatorios al registrar;
no se permiten documentos duplicados (409). Si `crearUsuario` es `true` se exigen `idRol`, `nombreUsuario` único y una
contraseña de mínimo 8 caracteres con mayúscula, minúscula y número.

Cuerpo de PATCH `/estado`:

```json
{ "estado": "vacaciones", "fechaEfectiva": "2026-10-14", "motivo": "Vacaciones programadas - acta 015" }
```

Reglas: el estado debe ser `activo`, `vacaciones` o `inactivo`, distinto al actual; la fecha efectiva no puede ser
anterior a la fecha de ingreso; el motivo es obligatorio (máximo 300 caracteres).

## 3. Catálogos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tipos-documento` | Tipos de documento (CC, CE, TI, PA, RC). |
| GET | `/api/tipos-contacto` | Tipos de contacto (Celular, Teléfono fijo, Correo electrónico). |
| GET | `/api/cargos` | Cargos con su área, salario base y nivel jerárquico. |
| GET | `/api/roles` | Roles del sistema. |

## 4. Temporadas – `/api/temporadas`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/temporadas` | Lista las temporadas. | 200 |
| GET | `/api/temporadas/{id}` | Consulta una temporada. | 200, 404 |
| POST | `/api/temporadas` | Crea una temporada. | 200, 400 |
| PUT | `/api/temporadas/{id}` | Actualiza una temporada. | 200, 400, 404 |

```json
{ "nombreTemporada": "Temporada de fin de año", "tipoTemporada": "Alta",
  "fechaInicio": "2026-12-15", "fechaFin": "2027-01-15", "descripcion": "Vacaciones de diciembre y enero" }
```

Reglas: `tipoTemporada` debe ser `Baja`, `Media`, `Alta` o `Premium`; `fechaFin` no puede ser anterior a `fechaInicio`.

## 5. Tarifas – `/api/tarifas`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/tarifas` | Lista las tarifas. | 200 |
| GET | `/api/tarifas/{id}` | Consulta una tarifa. | 200, 404 |
| POST | `/api/tarifas` | Crea una tarifa para una temporada. | 200, 400 |

```json
{ "valor": 350000, "fechaVigencia": "2026-12-15", "idTemporada": 1 }
```

## 6. Habitaciones – `/api/habitaciones`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/habitaciones` | Lista las habitaciones activas. | 200 |
| GET | `/api/habitaciones/{numero}` | Consulta una habitación. | 200, 404 |
| POST | `/api/habitaciones` | Crea una habitación con sus características. | 200, 400, 409 |
| PUT | `/api/habitaciones/{numero}` | Actualiza una habitación. | 200, 400, 404 |
| PATCH | `/api/habitaciones/{numero}/estado` | Cambia el estado: `disponible`, `ocupada`, `mantenimiento`, `limpieza`. | 200, 400, 404 |
| DELETE | `/api/habitaciones/{numero}` | Desactiva la habitación (no se permite si está ocupada). | 200, 400, 404 |

```json
{ "numeroHabitacion": 101, "ubicacion": "Bloque A, piso 1", "descripcion": "Vista a la piscina",
  "idTipoHabitacion": 1, "idsCaracteristicas": [1, 2] }
```

## 7. Tarifas por habitación – `/api/tarifas-habitacion`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/tarifas-habitacion` | Asigna una tarifa a una habitación desde una fecha y hora. |
| GET | `/api/tarifas-habitacion/habitacion/{numero}/historial` | Historial de tarifas de la habitación. |
| GET | `/api/tarifas-habitacion/habitacion/{numero}/vigente` | Tarifa vigente de la habitación. |

```json
{ "idTarifa": 1, "numeroHabitacion": 101, "fechaAplicacionTarifa": "2026-12-15T00:00:00" }
```

---

## Pruebas con Postman

1. Encender SQL Server y el backend (`.\mvnw.cmd spring-boot:run` en `backend/`).
2. En Postman: **Import → File** → `backend/postman/VillaKaty-API.postman_collection.json`.
3. En la pestaña **Variables** de la colección, escribir `usuario` y `contrasena` de un usuario del sistema
   (no se guardan en el repositorio).
4. **Run collection → Run**. La primera petición inicia sesión y guarda el token para las demás.

Desde la terminal, con Newman:

```bash
npx newman run backend/postman/VillaKaty-API.postman_collection.json --env-var usuario=USUARIO --env-var contrasena=CLAVE
```
