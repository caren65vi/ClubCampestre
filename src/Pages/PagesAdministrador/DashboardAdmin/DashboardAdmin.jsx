import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import { getEmployeeSession } from '../../../FireBase/authEmpleado'
import NavAdmin from '../NavAdmin/NavAdmin.jsx'
import GestionarEmpleados from '../../PageEmpleyGes/gestionEmpleados/gestionEmpleados.jsx'
import RegistrarEmpleado from '../../PageEmpleyGes/resgistrarEmpleados/resgistrarEmpleado.jsx'
import ActualizarEmpleado from '../../PageEmpleyGes/ActualizarEmpleado/ActualizarEmpleado.jsx'
import './DashboardAdmin.css'

const ResumenAdmin = () => (
  <section className="dashboardAdminHome">
    <span>Panel administrativo</span>
    <h1>Dashboard general</h1>
    <p>Selecciona una opción del menú para administrar Villa Katy.</p>
  </section>
)

const DashboardAdmin = () => {
  const navigate = useNavigate()

  // Sin sesión de empleado válida (o con el token vencido) el backend rechazaría todo: mejor pedir login
  if (!getEmployeeSession()) {
    return <Navigate to="/login?acceso=corporativo" replace />
  }

  const volverALista = (aviso) => navigate('/admin/empleados', aviso ? { state: { aviso } } : undefined)

  return (
    <div className="dashboardAdminLayout">
      <NavAdmin />
      <main className="dashboardAdminContent">
        <Routes>
          <Route index element={<ResumenAdmin />} />
          <Route path="empleados" element={<GestionarEmpleados />} />
          <Route
            path="empleados/registrar"
            element={(
              <RegistrarEmpleado
                onCancelar={() => volverALista()}
                onGuardar={(creado) => volverALista(`${creado.primerNombre} ${creado.primerApellido} quedó registrado con éxito.`)}
              />
            )}
          />
          <Route path="empleados/:numeroDocumento/editar" element={<ActualizarEmpleado onVolver={() => volverALista()} />} />
          <Route path="*" element={<ResumenAdmin />} />
        </Routes>
      </main>
    </div>
  )
}

export { DashboardAdmin }
