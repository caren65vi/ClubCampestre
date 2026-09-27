const API_URL = "http://localhost:8080/api/auth";
const EMPLOYEE_SESSION_KEY = "employeeSession";

// recordar = true guarda la sesión en localStorage; false solo en sessionStorage (se borra al cerrar el navegador)
export const loginEmpleado = async (nombreUsuario, contrasena, recordar = true) => {
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

    logoutEmpleado();
    (recordar ? localStorage : sessionStorage).setItem(EMPLOYEE_SESSION_KEY, JSON.stringify(data));
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

// El JWT trae la fecha de vencimiento (exp, en segundos) en su parte central
const tokenVencido = (token) => {
  try {
    const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
    return typeof payload.exp === "number" && payload.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
};

export const getEmployeeSession = () => {
  try {
    const guardada = localStorage.getItem(EMPLOYEE_SESSION_KEY) ?? sessionStorage.getItem(EMPLOYEE_SESSION_KEY);
    const session = JSON.parse(guardada);
    if (!session?.token) return null;
    if (tokenVencido(session.token)) {
      logoutEmpleado();
      return null;
    }
    return session;
  } catch {
    return null;
  }
};

export const logoutEmpleado = () => {
  localStorage.removeItem(EMPLOYEE_SESSION_KEY);
  sessionStorage.removeItem(EMPLOYEE_SESSION_KEY);
};
