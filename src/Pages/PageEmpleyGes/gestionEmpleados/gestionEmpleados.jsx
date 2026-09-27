import { useEffect, useMemo, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import {
  ArrowDownWideNarrow,
  BadgeCheck,
  ChevronLeft,
  ChevronRight,
  CircleCheck,
  Download,
  FilterX,
  IdCard,
  Mail,
  Palmtree,
  Pencil,
  Phone,
  Search,
  Trash2,
  UserPlus,
  Users,
  UserX,
  X,
} from 'lucide-react'
import { eliminarEmpleado, listarEmpleados } from '../../../services/empleadosApi'
import './GestionarEmpleados.css'

const POR_PAGINA = 8

const estadoLabels = { activo: 'Activo', vacaciones: 'En vacaciones', inactivo: 'Inactivo' }

const ordenes = [
  { valor: 'reciente', etiqueta: 'Ingreso más reciente' },
  { valor: 'antiguo', etiqueta: 'Ingreso más antiguo' },
  { valor: 'nombre-asc', etiqueta: 'Nombre A-Z' },
  { valor: 'nombre-desc', etiqueta: 'Nombre Z-A' },
]

// Colores del proyecto que se reparten entre las áreas (chip y avatar)
const tonos = ['accent', 'pool', 'nature', 'cta']

const getNombre = (empleado) => [
  empleado.primerNombre,
  empleado.segundoNombre,
  empleado.primerApellido,
  empleado.segundoApellido,
].filter(Boolean).join(' ')

const getIniciales = (empleado) => [empleado.primerNombre, empleado.primerApellido]
  .filter(Boolean)
  .map((parte) => parte.charAt(0).toUpperCase())
  .join('')

const getEstado = (empleado) => {
  const estado = empleado.estado?.toLowerCase()
  return estado === 'activo' || estado === 'vacaciones' ? estado : 'inactivo'
}

const formatoTelefono = (telefono = '') => {
  const digitos = telefono.replace(/\D/g, '')
  return digitos.length === 10 ? `${digitos.slice(0, 3)} ${digitos.slice(3, 6)} ${digitos.slice(6)}` : telefono
}

const formatoDocumento = (numero = '') => (/^\d+$/.test(numero) ? Number(numero).toLocaleString('es-CO') : numero)

// Números de página con "..." cuando hay muchas: 1 … 4 5 6 … 12
const paginasVisibles = (actual, total) => {
  const paginas = []
  for (let p = 1; p <= total; p += 1) {
    if (p === 1 || p === total || Math.abs(p - actual) <= 1) paginas.push(p)
    else if (paginas[paginas.length - 1] !== '…') paginas.push('…')
  }
  return paginas
}

const GestionarEmpleados = ({ onRegistrar, onEditar }) => {
  const navigate = useNavigate()
  const location = useLocation()
  const [empleados, setEmpleados] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busqueda, setBusqueda] = useState('')
  const [areaActiva, setAreaActiva] = useState('Todas')
  const [estadoFiltro, setEstadoFiltro] = useState('todos')
  const [orden, setOrden] = useState('reciente')
  const [pagina, setPagina] = useState(1)
  const [eliminando, setEliminando] = useState('')
  // Aviso que llega al volver de registrar (p. ej. "X quedó registrado con éxito")
  const [aviso, setAviso] = useState(() => (location.state?.aviso ? { tipo: 'exito', texto: location.state.aviso } : { tipo: '', texto: '' }))

  useEffect(() => {
    // Se limpia el estado del historial para que el aviso no reaparezca al recargar
    if (location.state?.aviso) navigate(location.pathname, { replace: true, state: null })
  }, [location, navigate])

  useEffect(() => {
    listarEmpleados()
      .then(setEmpleados)
      .catch((requestError) => setError(requestError.message))
      .finally(() => setLoading(false))
  }, [])

  const conteos = useMemo(() => empleados.reduce((acc, empleado) => {
    acc[getEstado(empleado)] += 1
    return acc
  }, { activo: 0, vacaciones: 0, inactivo: 0 }), [empleados])

  const areas = useMemo(() => {
    const porArea = new Map()
    empleados.forEach((empleado) => {
      if (empleado.nombreArea) porArea.set(empleado.nombreArea, (porArea.get(empleado.nombreArea) || 0) + 1)
    })
    return [...porArea].sort(([a], [b]) => a.localeCompare(b, 'es'))
      .map(([nombre, total], indice) => ({ nombre, total, tono: tonos[indice % tonos.length] }))
  }, [empleados])

  const tonoDeArea = (nombreArea) => areas.find((area) => area.nombre === nombreArea)?.tono ?? 'accent'

  const empleadosFiltrados = useMemo(() => {
    const texto = busqueda.trim().toLowerCase()
    const filtrados = empleados.filter((empleado) => {
      const coincideArea = areaActiva === 'Todas' || empleado.nombreArea === areaActiva
      const coincideEstado = estadoFiltro === 'todos' || getEstado(empleado) === estadoFiltro
      const buscable = `${getNombre(empleado)} ${empleado.numeroDocumento} ${empleado.nombreCargo} ${empleado.correo ?? ''}`
      return coincideArea && coincideEstado && (!texto || buscable.toLowerCase().includes(texto))
    })

    return filtrados.sort((a, b) => {
      if (orden === 'nombre-asc') return getNombre(a).localeCompare(getNombre(b), 'es')
      if (orden === 'nombre-desc') return getNombre(b).localeCompare(getNombre(a), 'es')
      const fechaA = a.fechaContratacion || ''
      const fechaB = b.fechaContratacion || ''
      return orden === 'antiguo' ? fechaA.localeCompare(fechaB) : fechaB.localeCompare(fechaA)
    })
  }, [empleados, areaActiva, estadoFiltro, busqueda, orden])

  const totalPaginas = Math.max(1, Math.ceil(empleadosFiltrados.length / POR_PAGINA))
  const paginaActual = Math.min(pagina, totalPaginas)
  const inicio = (paginaActual - 1) * POR_PAGINA
  const empleadosPagina = empleadosFiltrados.slice(inicio, inicio + POR_PAGINA)

  const hayFiltros = busqueda || areaActiva !== 'Todas' || estadoFiltro !== 'todos'
  const descripcionFiltro = [
    areaActiva !== 'Todas' && areaActiva,
    estadoFiltro !== 'todos' && estadoLabels[estadoFiltro],
    busqueda.trim() && `"${busqueda.trim()}"`,
  ].filter(Boolean).join(' · ') || 'Todos'

  const conFiltro = (setter) => (valor) => {
    setter(valor)
    setPagina(1)
  }
  const cambiarBusqueda = conFiltro(setBusqueda)
  const cambiarArea = conFiltro(setAreaActiva)
  const cambiarEstado = conFiltro(setEstadoFiltro)

  const limpiarFiltros = () => {
    setBusqueda('')
    setAreaActiva('Todas')
    setEstadoFiltro('todos')
    setPagina(1)
  }

  const registrar = onRegistrar || (() => navigate('/admin/empleados/registrar'))
  const editar = (empleado) => (onEditar
    ? onEditar(empleado)
    : navigate(`/admin/empleados/${encodeURIComponent(empleado.numeroDocumento)}/editar`))

  const eliminar = async (empleado) => {
    const nombre = getNombre(empleado)
    if (!window.confirm(`¿Dar de baja a ${nombre}? Quedará como inactivo y el cambio se guardará en su bitácora.`)) return

    setEliminando(empleado.numeroDocumento)
    setAviso({ tipo: '', texto: '' })
    try {
      const actualizado = await eliminarEmpleado(empleado.numeroDocumento)
      setEmpleados((prev) => prev.map((e) => (e.numeroDocumento === actualizado.numeroDocumento ? actualizado : e)))
      setAviso({ tipo: 'exito', texto: `${nombre} quedó dado de baja.` })
    } catch (requestError) {
      setAviso({ tipo: 'error', texto: requestError.message })
    } finally {
      setEliminando('')
    }
  }

  const exportar = () => {
    const celda = (valor) => `"${String(valor ?? '').replace(/"/g, '""')}"`
    const filas = [
      ['Tipo documento', 'Número documento', 'Nombre', 'Área', 'Cargo', 'Indicativo', 'Teléfono', 'Correo', 'Estado', 'Fecha de ingreso'],
      ...empleadosFiltrados.map((e) => [
        e.nombreTipoDocumento, e.numeroDocumento, getNombre(e), e.nombreArea, e.nombreCargo,
        e.indicativo, e.telefono, e.correo, estadoLabels[getEstado(e)], e.fechaContratacion,
      ]),
    ]
    const csv = '﻿' + filas.map((fila) => fila.map(celda).join(';')).join('\r\n')
    const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = 'empleados-villa-katy.csv'
    enlace.click()
    URL.revokeObjectURL(url)
  }

  const tarjetas = [
    { clave: 'total', etiqueta: 'Total empleados', valor: empleados.length, icono: Users, tono: 'accent' },
    { clave: 'activo', etiqueta: 'Personal activo', valor: conteos.activo, icono: CircleCheck, tono: 'nature' },
    { clave: 'vacaciones', etiqueta: 'En vacaciones', valor: conteos.vacaciones, icono: Palmtree, tono: 'cta' },
    { clave: 'inactivo', etiqueta: 'Inactivos / bajas', valor: conteos.inactivo, icono: UserX, tono: 'muted' },
  ]

  return (
    <section className="ge-page">
      <header className="ge-card ge-header">
        <div>
          <h1 className="ge-title">Gestión de empleados</h1>
          <p className="ge-subtitle">Administración centralizada del talento humano del Club Campestre Villa Katy.</p>
        </div>
        <div className="ge-header__acciones">
          <button className="ge-btn ge-btn--soft" type="button" onClick={exportar} disabled={loading || empleadosFiltrados.length === 0}>
            <Download size={16} aria-hidden="true" />Exportar Excel
          </button>
          <button className="ge-btn ge-btn--primary" type="button" onClick={registrar}>
            <UserPlus size={16} aria-hidden="true" />Registrar empleado
          </button>
        </div>
      </header>

      <div className="ge-stats">
        {tarjetas.map(({ clave, etiqueta, valor, icono: Icono, tono }) => (
          <article className="ge-card ge-stat" key={clave}>
            <div>
              <span className="ge-stat__label">{etiqueta}</span>
              <strong className={`ge-stat__valor ge-stat__valor--${clave}`}>{loading ? '—' : String(valor).padStart(2, '0')}</strong>
            </div>
            <span className={`ge-stat__icono ge-tone--${tono}`}><Icono size={18} aria-hidden="true" /></span>
          </article>
        ))}
      </div>

      <div className="ge-card ge-filtros">
        <div className="ge-filtros__fila">
          <label className="ge-search">
            <Search size={16} className="ge-search__icon" aria-hidden="true" />
            <input
              type="search"
              placeholder="Buscar por nombre, documento o cargo"
              value={busqueda}
              onChange={(event) => cambiarBusqueda(event.target.value)}
              aria-label="Buscar empleados"
            />
            {busqueda && (
              <button type="button" className="ge-search__clear" onClick={() => cambiarBusqueda('')} aria-label="Limpiar búsqueda">
                <X size={15} />
              </button>
            )}
          </label>
          <select className="ge-select" value={estadoFiltro} onChange={(event) => cambiarEstado(event.target.value)} aria-label="Filtrar por estado">
            <option value="todos">Todos los estados</option>
            <option value="activo">Activos</option>
            <option value="vacaciones">En vacaciones</option>
            <option value="inactivo">Inactivos</option>
          </select>
          <label className="ge-select-icon">
            <select className="ge-select" value={orden} onChange={(event) => setOrden(event.target.value)} aria-label="Ordenar">
              {ordenes.map((o) => <option key={o.valor} value={o.valor}>{o.etiqueta}</option>)}
            </select>
            <ArrowDownWideNarrow size={16} aria-hidden="true" />
          </label>
          <button className="ge-icon-btn" type="button" onClick={limpiarFiltros} disabled={!hayFiltros} aria-label="Quitar filtros" title="Quitar filtros">
            <FilterX size={17} />
          </button>
        </div>

        <div className="ge-chips">
          <span className="ge-chips__label">Área:</span>
          {[{ nombre: 'Todas', total: empleados.length }, ...areas].map((area) => (
            <button
              className={`ge-chip${areaActiva === area.nombre ? ' ge-chip--active' : ''}`}
              key={area.nombre}
              type="button"
              onClick={() => cambiarArea(area.nombre)}
              aria-pressed={areaActiva === area.nombre}
            >
              {area.nombre} <span className="ge-chip__count">({area.total})</span>
            </button>
          ))}
        </div>
      </div>

      <div className="ge-card ge-tabla-card">
        <div className="ge-tabla-top">
          <span className="ge-tabla-top__titulo">
            <span className="ge-dot" aria-hidden="true" />Registro de funcionarios
            <span className="ge-pill">{empleadosFiltrados.length} {empleadosFiltrados.length === 1 ? 'registrado' : 'registrados'}</span>
          </span>
          <span className="ge-tabla-top__meta"><BadgeCheck size={15} aria-hidden="true" />Directorio de talento humano</span>
        </div>

        {aviso.texto && (
          <p className={`ge-aviso ge-aviso--${aviso.tipo}`} role={aviso.tipo === 'error' ? 'alert' : 'status'}>{aviso.texto}</p>
        )}

        <div className="ge-table-wrap">
          <table className="ge-table">
            <thead>
              <tr>
                <th>Empleado</th>
                <th>Área</th>
                <th>Cargo</th>
                <th>Contacto</th>
                <th>Estado</th>
                <th className="ge-th-accion"><Pencil size={13} aria-hidden="true" /> Actualizar</th>
                <th className="ge-th-accion ge-th-accion--danger"><Trash2 size={13} aria-hidden="true" /> Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {loading && <tr><td colSpan="7" className="ge-empty">Cargando empleados...</td></tr>}
              {!loading && error && <tr><td colSpan="7" className="ge-empty">{error}</td></tr>}
              {!loading && !error && empleadosPagina.map((empleado) => {
                const nombre = getNombre(empleado)
                const estado = getEstado(empleado)
                const tono = tonoDeArea(empleado.nombreArea)
                return (
                  <tr key={empleado.numeroDocumento}>
                    <td className="ge-td-empleado">
                      <div className="ge-empleado">
                        <span className={`ge-avatar ge-tone--${tono}`} aria-hidden="true">{getIniciales(empleado)}</span>
                        <div>
                          <span className="ge-nombre">{nombre}</span>
                          <span className="ge-documento">
                            <IdCard size={13} aria-hidden="true" />{empleado.nombreTipoDocumento} {formatoDocumento(empleado.numeroDocumento)}
                          </span>
                        </div>
                      </div>
                    </td>
                    <td className="ge-td-area" data-label="Área"><span className={`ge-area ge-tone--${tono}`}>{empleado.nombreArea}</span></td>
                    <td className="ge-td-cargo" data-label="Cargo">{empleado.nombreCargo}</td>
                    <td className="ge-td-contacto" data-label="Contacto">
                      <div className="ge-contacto">
                        {empleado.telefono && <span><Phone size={13} aria-hidden="true" />{formatoTelefono(empleado.telefono)}</span>}
                        {empleado.correo && <span><Mail size={13} aria-hidden="true" />{empleado.correo}</span>}
                        {!empleado.telefono && !empleado.correo && <span>—</span>}
                      </div>
                    </td>
                    <td className="ge-td-estado" data-label="Estado"><span className={`ge-badge ge-badge--${estado}`}>{estadoLabels[estado]}</span></td>
                    <td className="ge-td-accion">
                      <button className="ge-accion ge-accion--editar" type="button" onClick={() => editar(empleado)} aria-label={`Actualizar ${nombre}`}>
                        <Pencil size={14} aria-hidden="true" />Actualizar
                      </button>
                    </td>
                    <td className="ge-td-accion">
                      <button
                        className="ge-accion ge-accion--eliminar"
                        type="button"
                        onClick={() => eliminar(empleado)}
                        disabled={estado === 'inactivo' || eliminando === empleado.numeroDocumento}
                        title={estado === 'inactivo' ? 'Ya está dado de baja' : undefined}
                        aria-label={`Eliminar ${nombre}`}
                      >
                        <Trash2 size={14} aria-hidden="true" />{eliminando === empleado.numeroDocumento ? 'Eliminando...' : 'Eliminar'}
                      </button>
                    </td>
                  </tr>
                )
              })}
              {!loading && !error && empleadosFiltrados.length === 0 && (
                <tr><td colSpan="7" className="ge-empty">No se encontraron empleados con esos filtros.</td></tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="ge-footer">
          <span className="ge-footer__info">
            {empleadosFiltrados.length > 0
              ? <>Mostrando <strong>{inicio + 1}</strong> a <strong>{inicio + empleadosPagina.length}</strong> de <strong>{empleadosFiltrados.length}</strong> empleados</>
              : 'Sin resultados'}
            <span className="ge-footer__sep" aria-hidden="true">•</span>
            Filtro actual: <strong>{descripcionFiltro}</strong>
          </span>
          {totalPaginas > 1 && (
            <nav className="ge-paginacion" aria-label="Paginación">
              <button type="button" onClick={() => setPagina(paginaActual - 1)} disabled={paginaActual === 1} aria-label="Página anterior">
                <ChevronLeft size={16} />
              </button>
              {paginasVisibles(paginaActual, totalPaginas).map((p, indice) => (p === '…'
                ? <span key={`sep-${indice}`} className="ge-paginacion__sep">…</span>
                : (
                  <button
                    key={p}
                    type="button"
                    className={p === paginaActual ? 'ge-paginacion__actual' : undefined}
                    onClick={() => setPagina(p)}
                    aria-current={p === paginaActual ? 'page' : undefined}
                  >
                    {p}
                  </button>
                )))}
              <button type="button" onClick={() => setPagina(paginaActual + 1)} disabled={paginaActual === totalPaginas} aria-label="Página siguiente">
                <ChevronRight size={16} />
              </button>
            </nav>
          )}
        </div>
      </div>
    </section>
  )
}

export default GestionarEmpleados
