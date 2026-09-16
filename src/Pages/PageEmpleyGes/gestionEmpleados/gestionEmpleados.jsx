import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, Plus, Pencil, MoreHorizontal } from 'lucide-react'
import { listarEmpleados } from '../../../services/empleadosApi'
import './GestionarEmpleados.css'

const areas = ['Todas', 'Cocina', 'Bar', 'Recepción']

const getNombre = (empleado) => [
  empleado.primerNombre,
  empleado.segundoNombre,
  empleado.primerApellido,
  empleado.segundoApellido,
].filter(Boolean).join(' ')

const getInitial = (nombre) => nombre.trim().charAt(0).toUpperCase()

const GestionarEmpleados = ({ onRegistrar, onEditar, onAbrirMenu }) => {
  const navigate = useNavigate()
  const [empleados, setEmpleados] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [areaActiva, setAreaActiva] = useState('Todas')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    listarEmpleados()
      .then(setEmpleados)
      .catch((requestError) => setError(requestError.message))
      .finally(() => setLoading(false))
  }, [])

  const empleadosFiltrados = useMemo(() => {
    const texto = busqueda.trim().toLowerCase()

    return empleados.filter((empleado) => {
      const nombre = getNombre(empleado)
      const documento = empleado.numeroDocumento || ''
      const coincideArea = areaActiva === 'Todas' || empleado.nombreArea === areaActiva
      const coincideBusqueda = !texto || `${nombre} ${documento}`.toLowerCase().includes(texto)
      return coincideArea && coincideBusqueda
    })
  }, [empleados, areaActiva, busqueda])

  return (
    <section className="ge-card">
      <div className="ge-top">
        <span className="ge-kicker">Gestión de personal</span>
        <button className="ge-menu-btn" type="button" onClick={onAbrirMenu} aria-label="Más opciones"><MoreHorizontal size={18} /></button>
      </div>

      <div className="ge-header">
        <h1 className="ge-title">Empleados</h1>
        <button className="ge-btn-registrar" type="button" onClick={onRegistrar || (() => navigate('/admin/empleados/registrar'))}><Plus size={16} />Registrar empleado</button>
      </div>

      <label className="ge-search">
        <Search size={16} className="ge-search__icon" />
        <input type="search" placeholder="Buscar por nombre o documento" value={busqueda} onChange={(event) => setBusqueda(event.target.value)} />
      </label>

      <div className="ge-chips">
        {areas.map((area) => <button className={`ge-chip${areaActiva === area ? ' ge-chip--active' : ''}`} key={area} type="button" onClick={() => setAreaActiva(area)}>{area}</button>)}
      </div>

      <div className="ge-table-wrap">
        <table className="ge-table">
          <thead><tr><th>Empleado</th><th>Área</th><th>Cargo</th><th>Estado</th><th aria-hidden="true" /></tr></thead>
          <tbody>
            {loading && <tr><td colSpan="5" className="ge-empty">Cargando empleados...</td></tr>}
            {!loading && error && <tr><td colSpan="5" className="ge-empty">{error}</td></tr>}
            {!loading && !error && empleadosFiltrados.map((empleado) => {
              const nombre = getNombre(empleado)
              const activo = empleado.estado?.toLowerCase() === 'activo'
              return <tr key={empleado.numeroDocumento}>
                <td><div className="ge-empleado"><span className="ge-avatar">{getInitial(nombre)}</span><span className="ge-nombre">{nombre}</span></div></td>
                <td>{empleado.nombreArea}</td><td>{empleado.nombreCargo}</td>
                <td><span className={`ge-badge ${activo ? 'ge-badge--activo' : 'ge-badge--inactivo'}`}>{activo ? 'Activo' : 'Inactivo'}</span></td>
                <td className="ge-editar"><button className="ge-editar-btn" type="button" onClick={() => onEditar?.(empleado)} aria-label={`Editar ${nombre}`}><Pencil size={15} /></button></td>
              </tr>
            })}
            {!loading && !error && empleadosFiltrados.length === 0 && <tr><td colSpan="5" className="ge-empty">No se encontraron empleados.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  )
}

export default GestionarEmpleados
