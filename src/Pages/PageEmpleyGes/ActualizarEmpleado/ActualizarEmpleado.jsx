import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  Briefcase,
  Building2,
  CircleAlert,
  CircleCheck,
  ContactRound,
  Download,
  FileText,
  History,
  Hourglass,
  IdCard,
  Lock,
  Mail,
  Palmtree,
  Phone,
  RefreshCw,
  Save,
  ShieldAlert,
  SlidersHorizontal,
  UserPen,
  UserX,
} from "lucide-react";
import { listarCargos } from "../../../services/catalogosApi";
import {
  actualizarEmpleado,
  buscarEmpleado,
  cambiarEstadoEmpleado,
  obtenerHistorialEmpleado,
} from "../../../services/empleadosApi";
import "./ActualizarEmpleado.css";

const indicativos = [
  { valor: "+57", etiqueta: "CO +57" },
  { valor: "+1", etiqueta: "US +1" },
  { valor: "+52", etiqueta: "MX +52" },
  { valor: "+58", etiqueta: "VE +58" },
  { valor: "+593", etiqueta: "EC +593" },
  { valor: "+34", etiqueta: "ES +34" },
];

const ESTADOS = [
  { valor: "activo", etiqueta: "Activo", detalle: "Acceso pleno", icono: CircleCheck },
  { valor: "vacaciones", etiqueta: "Vacaciones", detalle: "Pausado", icono: Palmtree },
  { valor: "inactivo", etiqueta: "Inactivo", detalle: "Baja", icono: UserX },
];

const CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const etiquetaEstado = (estado) =>
  ESTADOS.find((e) => e.valor === estado)?.etiqueta ?? (estado || "—");

const nombreCompleto = (e) =>
  [e.primerNombre, e.segundoNombre, e.primerApellido, e.segundoApellido].filter(Boolean).join(" ");

const iniciales = (e) =>
  [e.primerNombre, e.primerApellido].filter(Boolean).map((p) => p.charAt(0).toUpperCase()).join("");

const hoyISO = () => {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
};

// "2022-03-15" -> "15/03/2022" sin pasar por Date para no correr el día por zona horaria
const formatoFecha = (iso) => {
  if (!iso) return "—";
  const [a, m, d] = iso.slice(0, 10).split("-");
  return `${d}/${m}/${a}`;
};

const formatoHora = (iso) => (iso && iso.length >= 16 ? iso.slice(11, 16) : "");

const antiguedad = (fechaIngreso) => {
  if (!fechaIngreso) return "—";
  const [a, m, d] = fechaIngreso.split("-").map(Number);
  const hoy = new Date();
  let meses = (hoy.getFullYear() - a) * 12 + (hoy.getMonth() + 1 - m);
  if (hoy.getDate() < d) meses -= 1;
  if (meses < 1) return "Menos de un mes";
  const anios = Math.floor(meses / 12);
  const resto = meses % 12;
  const partes = [];
  if (anios) partes.push(`${anios} ${anios === 1 ? "año" : "años"}`);
  if (resto) partes.push(`${resto} ${resto === 1 ? "mes" : "meses"}`);
  return partes.join(", ");
};

const permisoDe = (h) => {
  if (h.tipoEvento === "edicion") return "Sin cambios";
  if (h.estadoNuevo === "activo") return h.estadoAnterior ? "Restaurado" : "Acceso pleno";
  if (h.estadoNuevo === "vacaciones") return "Pausado";
  return "Baja";
};

const telefonoValido = (indicativo, telefono) => {
  const digitos = telefono.replace(/\s/g, "");
  return indicativo === "+57" ? /^\d{10}$/.test(digitos) : /^\d{7,10}$/.test(digitos);
};

const formDesde = (empleado) => ({
  idArea: empleado.idArea != null ? String(empleado.idArea) : "",
  idCargo: empleado.idCargo != null ? String(empleado.idCargo) : "",
  indicativo: empleado.indicativo || "+57",
  telefono: empleado.telefono || "",
  correo: empleado.correo || "",
});

function EstadoBadge({ estado }) {
  return <span className={`ae-badge ae-badge--${estado || "inactivo"}`}>{etiquetaEstado(estado)}</span>;
}

export default function ActualizarEmpleado({ onVolver }) {
  const { numeroDocumento } = useParams();
  const navigate = useNavigate();
  const volver = onVolver || (() => navigate("/admin/empleados"));

  const [empleado, setEmpleado] = useState(null);
  const [historial, setHistorial] = useState([]);
  const [cargos, setCargos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState("");

  const [form, setForm] = useState(null);
  const [errores, setErrores] = useState({});
  const [guardando, setGuardando] = useState(false);
  const [mensajeInfo, setMensajeInfo] = useState({ tipo: "", texto: "" });

  const [nuevoEstado, setNuevoEstado] = useState("");
  const [fechaEfectiva, setFechaEfectiva] = useState(hoyISO());
  const [motivo, setMotivo] = useState("");
  const [aplicando, setAplicando] = useState(false);
  const [mensajeEstado, setMensajeEstado] = useState({ tipo: "", texto: "" });

  const pedirEmpleado = useCallback(() => Promise.all([
    buscarEmpleado(numeroDocumento),
    obtenerHistorialEmpleado(numeroDocumento),
  ]), [numeroDocumento]);

  const aplicarEmpleado = ([datos, bitacora]) => {
    setEmpleado(datos);
    setForm(formDesde(datos));
    setHistorial(bitacora);
  };

  const cargarEmpleado = async () => aplicarEmpleado(await pedirEmpleado());

  useEffect(() => {
    Promise.all([pedirEmpleado(), listarCargos()])
      .then(([datosEmpleado, listaCargos]) => {
        aplicarEmpleado(datosEmpleado);
        setCargos(listaCargos);
      })
      .catch((error) => setErrorCarga(error.message))
      .finally(() => setCargando(false));
  }, [pedirEmpleado]);

  const areas = useMemo(() => {
    const unicas = new Map();
    cargos.forEach((c) => unicas.set(c.idArea, { idArea: c.idArea, nombreArea: c.nombreArea, descripcionArea: c.descripcionArea }));
    return [...unicas.values()];
  }, [cargos]);

  const cargosDelArea = cargos.filter((c) => String(c.idArea) === form?.idArea);
  const cargoSeleccionado = cargos.find((c) => String(c.idCargo) === form?.idCargo);
  const areaSeleccionada = areas.find((a) => String(a.idArea) === form?.idArea);
  const ultimaEdicion = historial.find((h) => h.tipoEvento === "edicion");

  const hayCambios = Boolean(
    empleado && form && JSON.stringify(form) !== JSON.stringify(formDesde(empleado)),
  );

  const telefonoOk = form ? telefonoValido(form.indicativo, form.telefono) : false;
  const correoOk = form ? CORREO.test(form.correo.trim()) : false;

  const handleChange = (campo) => (e) => {
    const valor = e.target.value;
    setForm((prev) => ({ ...prev, [campo]: valor, ...(campo === "idArea" && { idCargo: "" }) }));
    setErrores((prev) => ({ ...prev, [campo]: undefined }));
    setMensajeInfo({ tipo: "", texto: "" });
  };

  const descartarCambios = () => {
    setForm(formDesde(empleado));
    setErrores({});
    setMensajeInfo({ tipo: "", texto: "" });
  };

  const guardarCambios = async (e) => {
    e.preventDefault();
    const encontrados = {};
    if (!form.idArea) encontrados.idArea = "Selecciona el área funcional.";
    if (!form.idCargo) encontrados.idCargo = "Selecciona el cargo laboral.";
    if (!form.telefono.trim()) encontrados.telefono = "Escribe el teléfono móvil.";
    else if (!telefonoOk) {
      encontrados.telefono = form.indicativo === "+57"
        ? "El celular en Colombia debe tener 10 dígitos."
        : "Usa solo números, entre 7 y 10 dígitos.";
    }
    if (!form.correo.trim()) encontrados.correo = "Escribe el correo electrónico.";
    else if (!correoOk) encontrados.correo = "Escribe un correo válido, por ejemplo nombre@correo.com.";

    setErrores(encontrados);
    if (Object.keys(encontrados).length > 0) {
      document.getElementById(`ae-${Object.keys(encontrados)[0]}`)?.focus();
      return;
    }

    setGuardando(true);
    try {
      await actualizarEmpleado(empleado.numeroDocumento, {
        numeroDocumento: empleado.numeroDocumento,
        idTipoDocumento: empleado.idTipoDocumento,
        primerNombre: empleado.primerNombre,
        segundoNombre: empleado.segundoNombre,
        primerApellido: empleado.primerApellido,
        segundoApellido: empleado.segundoApellido,
        fechaContratacion: empleado.fechaContratacion,
        salario: empleado.salario,
        idCargo: Number(form.idCargo),
        indicativo: form.indicativo,
        telefono: form.telefono.replace(/\s/g, ""),
        correo: form.correo.trim(),
      });
      await cargarEmpleado();
      setMensajeInfo({ tipo: "exito", texto: "Los cambios quedaron guardados." });
    } catch (error) {
      setMensajeInfo({ tipo: "error", texto: error.message });
    } finally {
      setGuardando(false);
    }
  };

  const aplicarEstado = async () => {
    setMensajeEstado({ tipo: "", texto: "" });
    if (!nuevoEstado) {
      setMensajeEstado({ tipo: "error", texto: "Selecciona el nuevo estado." });
      return;
    }
    if (!fechaEfectiva) {
      setMensajeEstado({ tipo: "error", texto: "Selecciona la fecha efectiva." });
      return;
    }
    if (!motivo.trim()) {
      setMensajeEstado({ tipo: "error", texto: "Escribe el motivo o número de acta." });
      document.getElementById("ae-motivo")?.focus();
      return;
    }
    if (nuevoEstado === "inactivo"
      && !window.confirm(`¿Dar de baja a ${nombreCompleto(empleado)}? El cambio quedará en la bitácora.`)) {
      return;
    }

    setAplicando(true);
    try {
      await cambiarEstadoEmpleado(empleado.numeroDocumento, {
        estado: nuevoEstado,
        fechaEfectiva,
        motivo: motivo.trim(),
      });
      await cargarEmpleado();
      setMensajeEstado({ tipo: "exito", texto: `Estado actualizado a ${etiquetaEstado(nuevoEstado)}.` });
      setNuevoEstado("");
      setMotivo("");
      setFechaEfectiva(hoyISO());
    } catch (error) {
      setMensajeEstado({ tipo: "error", texto: error.message });
    } finally {
      setAplicando(false);
    }
  };

  const exportarCSV = () => {
    const celda = (valor) => `"${String(valor ?? "").replace(/"/g, '""')}"`;
    const filas = [
      ["Fecha registro", "Hora", "Fecha efectiva", "Tipo", "Estado previo", "Nuevo estado", "Usuario responsable", "Cargo responsable", "Motivo / Acta", "Permisos"],
      ...historial.map((h) => [
        formatoFecha(h.fechaRegistro),
        formatoHora(h.fechaRegistro),
        formatoFecha(h.fechaEfectiva),
        h.tipoEvento === "edicion" ? "Edición de datos" : "Cambio de estado",
        h.estadoAnterior ? etiquetaEstado(h.estadoAnterior) : "",
        etiquetaEstado(h.estadoNuevo),
        h.nombreResponsable || h.usuarioResponsable || "Sistema",
        h.cargoResponsable || "",
        h.motivo,
        permisoDe(h),
      ]),
    ];
    const csv = "﻿" + filas.map((f) => f.map(celda).join(";")).join("\r\n");
    const url = URL.createObjectURL(new Blob([csv], { type: "text/csv;charset=utf-8" }));
    const enlace = document.createElement("a");
    enlace.href = url;
    enlace.download = `bitacora-${empleado.numeroDocumento}.csv`;
    enlace.click();
    URL.revokeObjectURL(url);
  };

  if (cargando) {
    return <section className="ae-page"><p className="ae-estado-carga">Cargando información del empleado...</p></section>;
  }

  if (errorCarga || !empleado) {
    return (
      <section className="ae-page">
        <div className="ae-card ae-estado-carga">
          <p role="alert">No se pudo cargar el empleado: {errorCarga || "no encontrado"}</p>
          <button type="button" className="ae-btn ae-btn--soft" onClick={volver}>
            <ArrowLeft size={16} aria-hidden="true" /> Volver a empleados
          </button>
        </div>
      </section>
    );
  }

  const nombre = nombreCompleto(empleado);

  return (
    <section className="ae-page">
      <button type="button" className="ae-volver ae-no-print" onClick={volver}>
        <ArrowLeft size={16} aria-hidden="true" /> Volver a empleados
      </button>

      {/* Perfil */}
      <header className="ae-card ae-perfil">
        <div className="ae-avatar" aria-hidden="true">
          {iniciales(empleado)}
          <span className={`ae-avatar__dot ae-avatar__dot--${empleado.estado}`} />
        </div>
        <div className="ae-perfil__info">
          <div className="ae-perfil__titulo">
            <h1>{nombre}</h1>
            <EstadoBadge estado={empleado.estado} />
          </div>
          <p className="ae-perfil__cargo">
            {empleado.nombreCargo} <span aria-hidden="true">•</span> <strong>{empleado.nombreArea}</strong>
          </p>
          <p className="ae-perfil__meta">
            <span><IdCard size={14} aria-hidden="true" /> {empleado.nombreTipoDocumento} {empleado.numeroDocumento}</span>
            <span><FileText size={14} aria-hidden="true" /> Ingreso: {formatoFecha(empleado.fechaContratacion)}</span>
          </p>
          <p className="ae-perfil__antiguedad">
            <Hourglass size={14} aria-hidden="true" /> Antigüedad: {antiguedad(empleado.fechaContratacion)}
          </p>
        </div>
        <button type="button" className="ae-btn ae-btn--soft ae-no-print" onClick={() => window.print()}>
          <FileText size={16} aria-hidden="true" /> Ver expediente PDF
        </button>
      </header>

      {/* Actualizar información */}
      <form className="ae-card" onSubmit={guardarCambios} noValidate>
        <div className="ae-card__header">
          <span className="ae-card__icono"><UserPen size={18} aria-hidden="true" /></span>
          <div>
            <h2>Actualizar información de empleado</h2>
            <p>Modificación de información operativa y de contacto laboral</p>
          </div>
          <span className="ae-pill"><span className="ae-pill__dot" /> Modo edición</span>
        </div>

        <div className="ae-documento">
          <div className="ae-documento__top">
            <h3><IdCard size={16} aria-hidden="true" /> Documento de identificación</h3>
            <span className="ae-tag-bloqueo"><ShieldAlert size={13} aria-hidden="true" /> Dato no modificable</span>
          </div>
          <div className="ae-grid ae-grid--2">
            <div className="ae-readonly">
              <span>Tipo doc:</span>
              <strong>{empleado.nombreTipoDocumento}</strong>
            </div>
            <div className="ae-readonly">
              <span>Número:</span>
              <strong>{empleado.numeroDocumento}</strong>
              <Lock size={15} className="ae-readonly__lock" aria-label="Bloqueado" />
            </div>
          </div>
        </div>

        <div className="ae-grid ae-grid--2">
          <div className="ae-field">
            <label htmlFor="ae-idCargo">Cargo laboral <span className="ae-required">*</span></label>
            <div className="ae-input-icon">
              <Briefcase size={16} aria-hidden="true" />
              <select id="ae-idCargo" value={form.idCargo} onChange={handleChange("idCargo")}
                disabled={!form.idArea} aria-invalid={Boolean(errores.idCargo)}>
                <option value="" disabled>{form.idArea ? "Selecciona un cargo" : "Primero elige el área"}</option>
                {cargosDelArea.map((c) => <option key={c.idCargo} value={c.idCargo}>{c.nombreCargo}</option>)}
              </select>
            </div>
            {errores.idCargo
              ? <small className="ae-error">{errores.idCargo}</small>
              : <small className="ae-help">{cargoSeleccionado?.descripcionCargo || cargoSeleccionado?.nivelJerarquico || "Cargo que ocupa en el club"}</small>}
          </div>

          <div className="ae-field">
            <label htmlFor="ae-idArea">Área funcional <span className="ae-required">*</span></label>
            <div className="ae-input-icon">
              <Building2 size={16} aria-hidden="true" />
              <select id="ae-idArea" value={form.idArea} onChange={handleChange("idArea")} aria-invalid={Boolean(errores.idArea)}>
                <option value="" disabled>Selecciona un área</option>
                {areas.map((a) => <option key={a.idArea} value={a.idArea}>{a.nombreArea}</option>)}
              </select>
            </div>
            {errores.idArea
              ? <small className="ae-error">{errores.idArea}</small>
              : <small className="ae-help">{areaSeleccionada?.descripcionArea || "Al cambiar el área debes elegir de nuevo el cargo"}</small>}
          </div>
        </div>

        <div className="ae-subseccion">
          <h3><ContactRound size={16} aria-hidden="true" /> Agregar / actualizar contactos</h3>
          <p>Canales oficiales para turnos, notificaciones institucionales y emergencias</p>
        </div>

        <div className="ae-grid ae-grid--2">
          <div className="ae-field">
            <label htmlFor="ae-telefono">Teléfono móvil <span className="ae-required">*</span></label>
            <div className="ae-phone">
              <select aria-label="Indicativo del país" value={form.indicativo} onChange={handleChange("indicativo")}>
                {indicativos.map((i) => <option key={i.valor} value={i.valor}>{i.etiqueta}</option>)}
              </select>
              <div className="ae-input-icon">
                <Phone size={16} aria-hidden="true" />
                <input id="ae-telefono" type="tel" inputMode="numeric" maxLength={14} placeholder="Ej: 310 452 8901"
                  value={form.telefono} onChange={handleChange("telefono")} aria-invalid={Boolean(errores.telefono)} />
              </div>
            </div>
            {errores.telefono && <small className="ae-error">{errores.telefono}</small>}
          </div>

          <div className="ae-field">
            <label htmlFor="ae-correo">Correo electrónico <span className="ae-required">*</span></label>
            <div className="ae-input-icon">
              <Mail size={16} aria-hidden="true" />
              <input id="ae-correo" type="email" maxLength={150} placeholder="nombre@villakaty.com"
                value={form.correo} onChange={handleChange("correo")} aria-invalid={Boolean(errores.correo)} />
            </div>
            {errores.correo && <small className="ae-error">{errores.correo}</small>}
          </div>
        </div>

        <p className={`ae-validacion ${telefonoOk && correoOk ? "ae-validacion--ok" : "ae-validacion--pendiente"}`} role="status">
          {telefonoOk && correoOk
            ? <CircleCheck size={15} aria-hidden="true" />
            : <CircleAlert size={15} aria-hidden="true" />}
          Validación: teléfono móvil {telefonoOk ? "válido" : "pendiente"}
          {form.indicativo === "+57" ? " (10 dígitos)" : ""} y correo {correoOk ? "válido" : "pendiente"}.
        </p>

        <div className="ae-card__footer">
          <small className="ae-ultima-edicion">
            {ultimaEdicion
              ? <>Última edición por <strong>{ultimaEdicion.nombreResponsable || ultimaEdicion.usuarioResponsable || "Sistema"}</strong>: {formatoFecha(ultimaEdicion.fechaRegistro)} {formatoHora(ultimaEdicion.fechaRegistro)}</>
              : "Sin ediciones registradas"}
          </small>
          <div className="ae-card__acciones">
            {mensajeInfo.texto && (
              <p className={mensajeInfo.tipo === "error" ? "ae-error" : "ae-exito"} role={mensajeInfo.tipo === "error" ? "alert" : "status"}>
                {mensajeInfo.texto}
              </p>
            )}
            <button type="button" className="ae-btn ae-btn--soft" onClick={descartarCambios} disabled={!hayCambios || guardando}>
              Descartar cambios
            </button>
            <button type="submit" className="ae-btn ae-btn--primary" disabled={!hayCambios || guardando}>
              <Save size={16} aria-hidden="true" /> {guardando ? "Guardando..." : "Guardar cambios"}
            </button>
          </div>
        </div>
      </form>

      {/* Estado laboral */}
      <section className="ae-card ae-estado" aria-labelledby="ae-estado-titulo">
        <div className="ae-estado__intro">
          <span className="ae-card__icono ae-card__icono--accent"><SlidersHorizontal size={18} aria-hidden="true" /></span>
          <div>
            <h2 id="ae-estado-titulo">Gestión de estado laboral</h2>
            <p>Transición operativa y disponibilidad</p>
          </div>
          <small>Estado actual: <EstadoBadge estado={empleado.estado} /></small>
        </div>

        <fieldset className="ae-estado__opciones">
          <legend>Seleccionar nuevo estado</legend>
          <div className="ae-opciones">
            {ESTADOS.map(({ valor, etiqueta, detalle, icono: Icono }) => {
              const actual = empleado.estado === valor;
              return (
                <button
                  key={valor}
                  type="button"
                  className={`ae-opcion ae-opcion--${valor}${nuevoEstado === valor ? " ae-opcion--activa" : ""}`}
                  onClick={() => { setNuevoEstado(valor); setMensajeEstado({ tipo: "", texto: "" }); }}
                  disabled={actual}
                  aria-pressed={nuevoEstado === valor}
                  title={actual ? "Es el estado actual" : undefined}
                >
                  <span className="ae-opcion__icono"><Icono size={16} aria-hidden="true" /></span>
                  <strong>{etiqueta}</strong>
                  <small>{actual ? "Estado actual" : detalle}</small>
                </button>
              );
            })}
          </div>
        </fieldset>

        <div className="ae-estado__datos">
          <div className="ae-field">
            <label htmlFor="ae-fecha-efectiva">Fecha efectiva</label>
            <input id="ae-fecha-efectiva" type="date" value={fechaEfectiva}
              min={empleado.fechaContratacion} onChange={(e) => setFechaEfectiva(e.target.value)} />
          </div>
          <div className="ae-field">
            <label htmlFor="ae-motivo">Motivo / acta <span className="ae-required">*</span></label>
            <input id="ae-motivo" type="text" maxLength={300} placeholder="Justificación..."
              value={motivo} onChange={(e) => setMotivo(e.target.value)} />
          </div>
        </div>

        <div className="ae-estado__accion">
          <button type="button" className="ae-btn ae-btn--accent" onClick={aplicarEstado} disabled={aplicando}>
            <RefreshCw size={16} aria-hidden="true" /> {aplicando ? "Aplicando..." : "Aplicar estado"}
          </button>
        </div>

        {mensajeEstado.texto && (
          <p className={`ae-estado__mensaje ${mensajeEstado.tipo === "error" ? "ae-error" : "ae-exito"}`}
            role={mensajeEstado.tipo === "error" ? "alert" : "status"}>
            {mensajeEstado.texto}
          </p>
        )}
      </section>

      {/* Bitácora */}
      <section className="ae-card" aria-labelledby="ae-historial-titulo">
        <div className="ae-card__header">
          <span className="ae-card__icono"><History size={18} aria-hidden="true" /></span>
          <div>
            <h2 id="ae-historial-titulo">Historial y bitácora de transiciones (auditoría)</h2>
            <p>Trazabilidad de cambios de estado y de datos del empleado</p>
          </div>
          <span className="ae-pill">{historial.length} {historial.length === 1 ? "registro" : "registros"}</span>
        </div>

        <div className="ae-tabla-wrap">
          <table className="ae-tabla">
            <thead>
              <tr>
                <th>Fecha / hora</th>
                <th>Estado previo</th>
                <th>Nuevo estado</th>
                <th>Usuario responsable</th>
                <th>Motivo / acta</th>
                <th>Permisos</th>
              </tr>
            </thead>
            <tbody>
              {historial.length === 0 && (
                <tr><td colSpan="6" className="ae-tabla__vacia">Todavía no hay movimientos registrados para este empleado.</td></tr>
              )}
              {historial.map((h) => (
                <tr key={h.idHistorial}>
                  <td className="ae-tabla__fecha" data-label="Fecha / hora">
                    {formatoFecha(h.fechaRegistro)}
                    <small>{formatoHora(h.fechaRegistro)}</small>
                  </td>
                  <td data-label="Estado previo">{h.tipoEvento === "edicion" || !h.estadoAnterior ? "—" : etiquetaEstado(h.estadoAnterior)}</td>
                  <td data-label="Nuevo estado">
                    {h.tipoEvento === "edicion"
                      ? <span className="ae-badge ae-badge--edicion">Edición de datos</span>
                      : <EstadoBadge estado={h.estadoNuevo} />}
                  </td>
                  <td className="ae-tabla__ancha" data-label="Usuario responsable">
                    <strong>{h.nombreResponsable || h.usuarioResponsable || "Sistema"}</strong>
                    {h.cargoResponsable && <small className="ae-tabla__sub">{h.cargoResponsable}</small>}
                  </td>
                  <td className="ae-tabla__ancha" data-label="Motivo / acta">
                    {h.motivo}
                    {h.tipoEvento === "estado" && h.fechaEfectiva && h.fechaEfectiva !== h.fechaRegistro?.slice(0, 10) && (
                      <small className="ae-tabla__sub">Efectiva: {formatoFecha(h.fechaEfectiva)}</small>
                    )}
                  </td>
                  <td data-label="Permisos"><span className="ae-permiso">{permisoDe(h)}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="ae-card__footer">
          <small className="ae-ultima-edicion">Los registros de la bitácora no se pueden editar ni eliminar.</small>
          <button type="button" className="ae-link-btn ae-no-print" onClick={exportarCSV} disabled={historial.length === 0}>
            <Download size={15} aria-hidden="true" /> Exportar bitácora completa (CSV)
          </button>
        </div>
      </section>
    </section>
  );
}
