const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const getToken = () => {
  try {
    const session = JSON.parse(localStorage.getItem('employeeSession'))
    return session?.token || null
  } catch {
    return null
  }
}

const request = async (path, options = {}) => {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }
  const response = await fetch(`${API_URL}${path}`, { ...options, headers })
  const data = await response.json().catch(() => null)

  if (!response.ok) {
    throw new Error(data?.message || data?.error || 'No se pudo completar la solicitud.')
  }

  return data
}

export const listarEmpleados = () => request('/empleados')

export const crearEmpleado = (empleado) => request('/empleados', {
  method: 'POST',
  body: JSON.stringify(empleado),
})

export const cambiarEstadoEmpleado = (numeroDocumento, estado) => request(
  `/empleados/${encodeURIComponent(numeroDocumento)}/estado`,
  {
    method: 'PATCH',
    body: JSON.stringify({ estado }),
  },
)
