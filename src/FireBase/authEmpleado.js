const API_URL = "http://localhost:8080/api/auth";
const EMPLOYEE_SESSION_KEY = "employeeSession";

export const loginEmpleado = async (nombreUsuario, contrasena) => {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nombreUsuario: nombreUsuario.trim(), contrasena }),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      const error = new Error(data.error || "Usuario o contraseña de empleado incorrectos.");
      error.isEmpleadoAuthError = true;
      error.status = response.status;
      throw error;
    }

    localStorage.setItem(EMPLOYEE_SESSION_KEY, JSON.stringify(data));
    return data; // { idUsuario, nombreUsuario, rol, token }
  } catch (error) {
    if (error?.isEmpleadoAuthError) {
      throw error;
    }

    const backendError = new Error("No se pudo contactar con el servicio de empleados.");
    backendError.isBackendUnavailable = true;
    throw backendError;
  }
};

export const getEmployeeSession = () => {
  try {
    const session = JSON.parse(localStorage.getItem(EMPLOYEE_SESSION_KEY));
    return session?.token ? session : null;
  } catch {
    return null;
  }
};

export const logoutEmpleado = () => {
  localStorage.removeItem(EMPLOYEE_SESSION_KEY);
};
