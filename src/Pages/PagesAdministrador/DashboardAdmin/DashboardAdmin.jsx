import { Route, Routes, useNavigate } from 'react-router-dom'
import NavAdmin from '../NavAdmin/NavAdmin.jsx'
import GestionarEmpleados from '../../PageEmpleyGes/gestionEmpleados/gestionEmpleados.jsx'
import RegistrarEmpleado from '../../PageEmpleyGes/resgistrarEmpleados/resgistrarEmpleado.jsx'
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

  return (
    <div className="dashboardAdminLayout">
      <NavAdmin />
      <main className="dashboardAdminContent">
        <Routes>
          <Route index element={<ResumenAdmin />} />
          <Route path="empleados" element={<GestionarEmpleados />} />
          <Route path="empleados/registrar" element={<RegistrarEmpleado onCancelar={() => navigate('/admin/empleados')} />} />
          <Route path="*" element={<ResumenAdmin />} />
        </Routes>
      </main>
    </div>
  )
}

export { DashboardAdmin }
