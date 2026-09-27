import { getEmployeeSession, logoutEmpleado } from '../FireBase/authEmpleado'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const getToken = () => getEmployeeSession()?.token || null

export const request = async (path, options = {}) => {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }
  let response
  try {
    response = await fetch(`${API_URL}${path}`, { ...options, headers })
  } catch {
    throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté encendido.')
  }
  const data = await response.json().catch(() => null)

  // 401/403 sin mensaje: el backend rechazó el token (vencido, inválido o sin iniciar sesión)
  if ((response.status === 401 || response.status === 403) && !data?.error) {
    logoutEmpleado()
    const error = new Error('Tu sesión de empleado venció o no es válida. Vuelve a iniciar sesión en Acceso corporativo.')
    error.sesionVencida = true
    throw error
  }

  if (!response.ok) {
    throw new Error(data?.message || data?.error || `No se pudo completar la solicitud (error ${response.status}).`)
  }

  return data
}

export const listarEmpleados = () => request('/empleados')

export const buscarEmpleado = (numeroDocumento) => request(
  `/empleados/${encodeURIComponent(numeroDocumento)}`,
)

export const crearEmpleado = (empleado) => request('/empleados', {
  method: 'POST',
  body: JSON.stringify(empleado),
})

export const actualizarEmpleado = (numeroDocumento, empleado) => request(
  `/empleados/${encodeURIComponent(numeroDocumento)}`,
  {
    method: 'PUT',
    body: JSON.stringify(empleado),
  },
)

// Baja lógica: el backend lo pasa a "inactivo" y lo registra en la bitácora
export const eliminarEmpleado = (numeroDocumento) => request(
  `/empleados/${encodeURIComponent(numeroDocumento)}`,
  { method: 'DELETE' },
)

// cambio: { estado, fechaEfectiva: 'YYYY-MM-DD', motivo }
export const cambiarEstadoEmpleado = (numeroDocumento, cambio) => request(
  `/empleados/${encodeURIComponent(numeroDocumento)}/estado`,
  {
    method: 'PATCH',
    body: JSON.stringify(cambio),
  },
)

export const obtenerHistorialEmpleado = (numeroDocumento) => request(
  `/empleados/${encodeURIComponent(numeroDocumento)}/historial`,
)
