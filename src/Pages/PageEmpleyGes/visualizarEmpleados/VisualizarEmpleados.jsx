import { useMemo, useState } from 'react'
import { Search, Plus, Pencil } from 'lucide-react'
import './VisualizarEmpleados.css'

const empleadosMock = [
  { id: 1, nombre: 'Laura Gómez', area: 'Cocina', cargo: 'Chef de línea', estado: 'Activo' },
  { id: 2, nombre: 'Jorge Salas', area: 'Mantenimiento', cargo: 'Técnico', estado: 'Inactivo' },
  { id: 3, nombre: 'Camila Ruiz', area: 'Recepción', cargo: 'Recepcionista', estado: 'Activo' },
]

const areas = ['Todas', 'Cocina', 'Bar', 'Recepción']

const getInitial = (nombre) => nombre.trim().charAt(0).toUpperCase()

const VisualizarEmpleados = ({ empleados = empleadosMock, onRegistrar, onEditar }) => {
  const [busqueda, setBusqueda] = useState('')
  const [areaActiva, setAreaActiva] = useState('Todas')

  const empleadosFiltrados = useMemo(() => {
    const texto = busqueda.trim().toLowerCase()

    return empleados.filter((empleado) => {
      const coincideArea = areaActiva === 'Todas' || empleado.area === areaActiva
      const coincideBusqueda = !texto || empleado.nombre.toLowerCase().includes(texto)
      return coincideArea && coincideBusqueda
    })
  }, [empleados, areaActiva, busqueda])

  return (
    <section className="visualizar-empleados">
      <header className="ve-header">
        <div>
          <span className="ve-kicker">Gestión de personal</span>
          <h1 className="ve-title">Empleados</h1>
        </div>
        <button className="ve-btn-registrar" type="button" onClick={onRegistrar}>
          <Plus size={16} />
          Registrar
        </button>
      </header>

      <label className="ve-search">
        <Search size={16} className="ve-search__icon" />
        <input
          type="text"
          placeholder="Buscar por nombre o documento"
          value={busqueda}
          onChange={(event) => setBusqueda(event.target.value)}
        />
      </label>

      <div className="ve-chips">
        {areas.map((area) => (
          <button
            className={`ve-chip${areaActiva === area ? ' ve-chip--active' : ''}`}
            key={area}
            type="button"
            onClick={() => setAreaActiva(area)}
          >
            {area}
          </button>
        ))}
      </div>

      <div className="ve-table-wrap">
        <table className="ve-table">
          <thead>
            <tr><th>Empleado</th><th>Área</th><th>Cargo</th><th>Estado</th><th aria-hidden="true" /></tr>
          </thead>
          <tbody>
            {empleadosFiltrados.map((empleado) => (
              <tr key={empleado.id}>
                <td><div className="ve-empleado"><span className="ve-avatar">{getInitial(empleado.nombre)}</span><span className="ve-nombre">{empleado.nombre}</span></div></td>
                <td>{empleado.area}</td>
                <td>{empleado.cargo}</td>
                <td><span className={`ve-badge ${empleado.estado === 'Activo' ? 've-badge--activo' : 've-badge--inactivo'}`}>{empleado.estado}</span></td>
                <td className="ve-editar"><button className="ve-editar-btn" type="button" onClick={() => onEditar?.(empleado)} aria-label={`Editar ${empleado.nombre}`}><Pencil size={15} /></button></td>
              </tr>
            ))}
            {empleadosFiltrados.length === 0 && <tr><td colSpan={5} className="ve-empty">No se encontraron empleados.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  )
}

export default VisualizarEmpleados
