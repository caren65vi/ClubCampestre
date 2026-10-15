/**
 * RF-0111 - Formulario de registro de empleados.
 * Carga los catálogos (tipos de documento, cargos, roles), valida los datos antes de enviarlos
 * y llama a POST /api/empleados. Opcionalmente crea el usuario de acceso al sistema.
 */
import { useEffect, useMemo, useState } from "react";
import {
  ArrowLeft,
  AtSign,
  Briefcase,
  Contact,
  Eye,
  EyeOff,
  IdCard,
  KeyRound,
  LockKeyhole,
  Network,
  Phone,
  Plus,
  RotateCcw,
  Save,
  ShieldCheck,
  Trash2,
  User,
  X,
} from "lucide-react";
import {
  listarCargos,
  listarRoles,
  listarTiposContacto,
  listarTiposDocumento,
} from "../../../services/catalogosApi";
import { crearEmpleado } from "../../../services/empleadosApi";
import { esCorreo, validar } from "./validacionEmpleado";
import "./resgistrarEmpleado.css";

const indicativos = [
  { valor: "+57", etiqueta: "CO +57" },
  { valor: "+1", etiqueta: "US +1" },
  { valor: "+52", etiqueta: "MX +52" },
  { valor: "+58", etiqueta: "VE +58" },
  { valor: "+593", etiqueta: "EC +593" },
  { valor: "+34", etiqueta: "ES +34" },
];

// Valores iniciales del formulario
const formVacio = {
  idTipoDocumento: "",
  numeroDocumento: "",
  primerNombre: "",
  segundoNombre: "",
  primerApellido: "",
  segundoApellido: "",
  indicativo: "+57",
  telefono: "",
  correo: "",
  idArea: "",
  idCargo: "",
  fechaContratacion: "",
  salario: "",
  crearUsuario: true,
  idRol: "",
  nombreUsuario: "",
  contrasena: "",
  confirmarContrasena: "",
};

let contactoIdCounter = 0;

// Contenedor de un campo: etiqueta, asterisco si es obligatorio, ayuda y mensaje de error
function Campo({ id, label, requerido, error, ayuda, extra, children }) {
  return (
    <div className="re-field">
      <div className="re-field__top">
        <label htmlFor={id}>
          {label}
          {requerido && <span className="re-required" aria-hidden="true"> *</span>}
        </label>
        {extra}
      </div>
      {children}
      {error ? (
        <small className="re-error" id={`${id}-error`}>{error}</small>
      ) : (
        ayuda && <small className="re-help">{ayuda}</small>
      )}
    </div>
  );
}

// Sección numerada del formulario (identificación, contactos, datos laborales, acceso)
function Seccion({ numero, titulo, descripcion, icono: Icono, children }) {
  return (
    <section className="re-card" aria-labelledby={`re-seccion-${numero}`}>
      <header className="re-card__header">
        <span className="re-card__numero">{numero}</span>
        <div>
          <h2 id={`re-seccion-${numero}`}>{titulo}</h2>
          {descripcion && <p>{descripcion}</p>}
        </div>
        <Icono className="re-card__icono" size={22} aria-hidden="true" />
      </header>
      {children}
    </section>
  );
}

// Componente del formulario de registro. Props: onCancelar y onGuardar(empleadoCreado)
export default function RegistrarEmpleado({ onCancelar, onGuardar }) {
  const [tiposDocumento, setTiposDocumento] = useState([]);
  const [tiposContacto, setTiposContacto] = useState([]);
  const [cargos, setCargos] = useState([]);
  const [roles, setRoles] = useState([]);
  const [errorCatalogos, setErrorCatalogos] = useState("");
  const [verContrasena, setVerContrasena] = useState(false);

  const [form, setForm] = useState(formVacio);
  const [contactosExtra, setContactosExtra] = useState([]);
  const [errores, setErrores] = useState({});
  const [errorServidor, setErrorServidor] = useState("");
  const [guardando, setGuardando] = useState(false);
  const [exito, setExito] = useState("");

  // Carga los catálogos de la API al abrir el formulario
  useEffect(() => {
    Promise.all([listarTiposDocumento(), listarTiposContacto(), listarCargos(), listarRoles()])
      .then(([documentos, contactos, listaCargos, listaRoles]) => {
        setTiposDocumento(documentos);
        setTiposContacto(contactos);
        setCargos(listaCargos);
        setRoles(listaRoles);
      })
      .catch((error) => setErrorCatalogos(error.message));
  }, []);

  // Agrupa los cargos por área para los selectores dependientes Área → Cargo
  const areas = useMemo(() => {
    const unicas = new Map();
    cargos.forEach((c) => unicas.set(c.idArea, c.nombreArea));
    return [...unicas].map(([idArea, nombreArea]) => ({ idArea, nombreArea }));
  }, [cargos]);

  const cargosDelArea = cargos.filter((c) => String(c.idArea) === String(form.idArea));
  const tipoDocumento = tiposDocumento.find(
    (t) => String(t.idTipoDocumento) === String(form.idTipoDocumento),
  );

  const limpiarError = (campo) =>
    setErrores((prev) => {
      if (!prev[campo]) return prev;
      const siguiente = { ...prev };
      delete siguiente[campo];
      return siguiente;
    });

  // Actualiza el campo en el estado; al cambiar el área reinicia el cargo y sugiere el salario
  const handleChange = (campo) => (e) => {
    const valor = e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setForm((prev) => {
      const siguiente = { ...prev, [campo]: valor };
      if (campo === "idArea") {
        siguiente.idCargo = "";
      }
      if (campo === "idCargo") {
        const cargo = cargos.find((c) => String(c.idCargo) === valor);
        if (cargo && !prev.salario) siguiente.salario = String(cargo.salarioBase);
      }
      return siguiente;
    });
    limpiarError(campo);
    setExito("");
  };

  // Agrega una fila de contacto adicional
  const agregarContacto = () => {
    contactoIdCounter += 1;
    setContactosExtra((prev) => [
      ...prev,
      { id: contactoIdCounter, idTipoContacto: "", indicativo: "+57", valor: "" },
    ]);
  };

  const cambiarContacto = (id, campo) => (e) => {
    const valor = e.target.value;
    setContactosExtra((prev) => prev.map((c) => (c.id === id ? { ...c, [campo]: valor } : c)));
    limpiarError(`extra-${id}`);
  };

  // Quita una fila de contacto adicional
  const eliminarContacto = (id) => {
    setContactosExtra((prev) => prev.filter((c) => c.id !== id));
    limpiarError(`extra-${id}`);
  };

  // Deja el formulario en blanco
  const limpiarFormulario = () => {
    setForm(formVacio);
    setContactosExtra([]);
    setErrores({});
    setErrorServidor("");
    setExito("");
    setVerContrasena(false);
  };

  // Valida, arma el objeto para la API y llama a POST /api/empleados (evento onSubmit)
  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorServidor("");
    setExito("");

    const encontrados = validar(form, contactosExtra, tiposContacto, tipoDocumento);
    setErrores(encontrados);
    if (Object.keys(encontrados).length > 0) {
      const primero = Object.keys(encontrados)[0];
      document.getElementById(primero)?.focus();
      return;
    }

    const opcional = (valor) => valor.trim() || null;
    const datos = {
      idTipoDocumento: Number(form.idTipoDocumento),
      numeroDocumento: form.numeroDocumento.trim(),
      primerNombre: form.primerNombre.trim(),
      segundoNombre: opcional(form.segundoNombre),
      primerApellido: form.primerApellido.trim(),
      segundoApellido: opcional(form.segundoApellido),
      indicativo: form.indicativo,
      telefono: form.telefono.replace(/\s/g, ""),
      correo: form.correo.trim(),
      contactosAdicionales: contactosExtra.map((c) => {
        const tipo = tiposContacto.find((t) => String(t.idTipoContacto) === String(c.idTipoContacto));
        return {
          idTipoContacto: Number(c.idTipoContacto),
          indicativo: esCorreo(tipo) ? null : c.indicativo,
          valor: esCorreo(tipo) ? c.valor.trim() : c.valor.replace(/\s/g, ""),
        };
      }),
      idCargo: Number(form.idCargo),
      fechaContratacion: form.fechaContratacion,
      salario: Number(form.salario),
      crearUsuario: form.crearUsuario,
      ...(form.crearUsuario && {
        idRol: Number(form.idRol),
        nombreUsuario: form.nombreUsuario.trim(),
        contrasena: form.contrasena,
      }),
    };

    setGuardando(true);
    try {
      const creado = await crearEmpleado(datos);
      limpiarFormulario();
      setExito(`${creado.primerNombre} ${creado.primerApellido} quedó registrado.`);
      onGuardar?.(creado);
    } catch (error) {
      setErrorServidor(error.message);
    } finally {
      setGuardando(false);
    }
  };

  const invalido = (campo) => ({
    "aria-invalid": Boolean(errores[campo]),
    "aria-describedby": errores[campo] ? `${campo}-error` : undefined,
  });

  return (
    <section className="registrar-empleado-page">
      <header className="re-page-header">
        <div>
          <h1>Registro de nuevo empleado</h1>
          <span>Gestión de empleados</span>
        </div>
        <button type="button" className="re-btn re-btn--soft" onClick={onCancelar}>
          <ArrowLeft size={16} aria-hidden="true" />
          Volver
        </button>
      </header>

      {errorCatalogos && (
        <p className="re-alert" role="alert">
          No se pudieron cargar las listas del formulario: {errorCatalogos}
        </p>
      )}

      <form className="registrar-empleado" onSubmit={handleSubmit} noValidate>
        <Seccion numero={1} titulo="Datos de identificación personal" icono={IdCard}>
          <div className="re-grid re-grid--2">
            <Campo id="idTipoDocumento" label="Tipo de documento" requerido error={errores.idTipoDocumento}>
              <select
                id="idTipoDocumento"
                value={form.idTipoDocumento}
                onChange={handleChange("idTipoDocumento")}
                {...invalido("idTipoDocumento")}
              >
                <option value="" disabled>Selecciona un tipo</option>
                {tiposDocumento.map((t) => (
                  <option key={t.idTipoDocumento} value={t.idTipoDocumento}>
                    {t.descripcion ? `${t.descripcion} (${t.nombreTipoDocumento})` : t.nombreTipoDocumento}
                  </option>
                ))}
              </select>
            </Campo>
            <Campo id="numeroDocumento" label="Número de documento de identidad" requerido error={errores.numeroDocumento}>
              <input
                id="numeroDocumento"
                type="text"
                inputMode={tipoDocumento?.nombreTipoDocumento === "PA" ? "text" : "numeric"}
                maxLength={20}
                placeholder="Ej: 1098765432"
                value={form.numeroDocumento}
                onChange={handleChange("numeroDocumento")}
                {...invalido("numeroDocumento")}
              />
            </Campo>
          </div>
          <div className="re-grid re-grid--4">
            <Campo id="primerNombre" label="Primer nombre" requerido error={errores.primerNombre}>
              <input id="primerNombre" type="text" maxLength={50} placeholder="Ej: Mateo"
                value={form.primerNombre} onChange={handleChange("primerNombre")} {...invalido("primerNombre")} />
            </Campo>
            <Campo id="segundoNombre" label="Segundo nombre" error={errores.segundoNombre}>
              <input id="segundoNombre" type="text" maxLength={50} placeholder="Ej: Alejandro"
                value={form.segundoNombre} onChange={handleChange("segundoNombre")} {...invalido("segundoNombre")} />
            </Campo>
            <Campo id="primerApellido" label="Primer apellido" requerido error={errores.primerApellido}>
              <input id="primerApellido" type="text" maxLength={50} placeholder="Ej: Restrepo"
                value={form.primerApellido} onChange={handleChange("primerApellido")} {...invalido("primerApellido")} />
            </Campo>
            <Campo id="segundoApellido" label="Segundo apellido" error={errores.segundoApellido}>
              <input id="segundoApellido" type="text" maxLength={50} placeholder="Ej: Montoya"
                value={form.segundoApellido} onChange={handleChange("segundoApellido")} {...invalido("segundoApellido")} />
            </Campo>
          </div>
        </Seccion>

        <Seccion
          numero={2}
          titulo="Contactos"
          descripcion="Información de comunicación directa del colaborador"
          icono={Contact}
        >
          <div className="re-grid re-grid--2">
            <Campo
              id="telefono"
              label="Número de celular / WhatsApp"
              requerido
              error={errores.telefono}
              ayuda="Línea directa para notificaciones operativas"
            >
              <div className="re-phone">
                <select
                  aria-label="Indicativo del país"
                  value={form.indicativo}
                  onChange={handleChange("indicativo")}
                >
                  {indicativos.map((i) => (
                    <option key={i.valor} value={i.valor}>{i.etiqueta}</option>
                  ))}
                </select>
                <div className="re-input-icon">
                  <Phone size={16} aria-hidden="true" />
                  <input id="telefono" type="tel" inputMode="numeric" maxLength={14} placeholder="Ej: 312 456 7890"
                    value={form.telefono} onChange={handleChange("telefono")} {...invalido("telefono")} />
                </div>
              </div>
            </Campo>
            <Campo
              id="correo"
              label="Correo electrónico"
              requerido
              error={errores.correo}
              ayuda="Correo corporativo o personal"
            >
              <div className="re-input-icon">
                <AtSign size={16} aria-hidden="true" />
                <input id="correo" type="email" maxLength={150} placeholder="ejemplo@correo.com"
                  value={form.correo} onChange={handleChange("correo")} {...invalido("correo")} />
              </div>
            </Campo>
          </div>

          {contactosExtra.map((c) => {
            const tipo = tiposContacto.find((t) => String(t.idTipoContacto) === String(c.idTipoContacto));
            const idError = `extra-${c.id}`;
            return (
              <div className="re-extra" key={c.id}>
                <div className="re-extra__row">
                  <select
                    id={idError}
                    aria-label="Tipo de contacto"
                    value={c.idTipoContacto}
                    onChange={cambiarContacto(c.id, "idTipoContacto")}
                    aria-invalid={Boolean(errores[idError])}
                  >
                    <option value="" disabled>Tipo de contacto</option>
                    {tiposContacto.map((t) => (
                      <option key={t.idTipoContacto} value={t.idTipoContacto}>{t.nombreTipoContacto}</option>
                    ))}
                  </select>
                  {!esCorreo(tipo) && (
                    <select
                      aria-label="Indicativo del país"
                      value={c.indicativo}
                      onChange={cambiarContacto(c.id, "indicativo")}
                    >
                      {indicativos.map((i) => (
                        <option key={i.valor} value={i.valor}>{i.etiqueta}</option>
                      ))}
                    </select>
                  )}
                  <input
                    aria-label="Contacto"
                    type={esCorreo(tipo) ? "email" : "tel"}
                    placeholder={esCorreo(tipo) ? "ejemplo@correo.com" : "Ej: 312 456 7890"}
                    maxLength={150}
                    value={c.valor}
                    onChange={cambiarContacto(c.id, "valor")}
                    aria-invalid={Boolean(errores[idError])}
                  />
                  <button
                    type="button"
                    className="re-icon-btn"
                    onClick={() => eliminarContacto(c.id)}
                    aria-label="Eliminar contacto"
                  >
                    <Trash2 size={16} aria-hidden="true" />
                  </button>
                </div>
                {errores[idError] && <small className="re-error">{errores[idError]}</small>}
              </div>
            );
          })}

          <div className="re-card__footer">
            <button type="button" className="re-link-btn" onClick={agregarContacto}>
              <Plus size={16} aria-hidden="true" />
              Agregar otro teléfono o contacto de emergencia
            </button>
            <span className="re-help">Opcional</span>
          </div>
        </Seccion>

        <Seccion
          numero={3}
          titulo="Datos laborales"
          descripcion="Área y cargo que ocupará en el club"
          icono={Network}
        >
          <div className="re-grid re-grid--4">
            <Campo id="idArea" label="Área" requerido error={errores.idArea}>
              <select id="idArea" value={form.idArea} onChange={handleChange("idArea")} {...invalido("idArea")}>
                <option value="" disabled>Selecciona un área</option>
                {areas.map((a) => (
                  <option key={a.idArea} value={a.idArea}>{a.nombreArea}</option>
                ))}
              </select>
            </Campo>
            <Campo id="idCargo" label="Cargo" requerido error={errores.idCargo}>
              <div className="re-input-icon re-input-icon--right">
                <select
                  id="idCargo"
                  value={form.idCargo}
                  onChange={handleChange("idCargo")}
                  disabled={!form.idArea}
                  {...invalido("idCargo")}
                >
                  <option value="" disabled>
                    {form.idArea ? "Selecciona un cargo" : "Primero elige el área"}
                  </option>
                  {cargosDelArea.map((c) => (
                    <option key={c.idCargo} value={c.idCargo}>{c.nombreCargo}</option>
                  ))}
                </select>
                <Briefcase size={16} aria-hidden="true" />
              </div>
            </Campo>
            <Campo id="fechaContratacion" label="Fecha de ingreso" requerido error={errores.fechaContratacion}>
              <input id="fechaContratacion" type="date" value={form.fechaContratacion}
                onChange={handleChange("fechaContratacion")} {...invalido("fechaContratacion")} />
            </Campo>
            <Campo
              id="salario"
              label="Salario"
              requerido
              error={errores.salario}
              ayuda={form.idCargo ? "Sugerido según el cargo" : undefined}
            >
              <input id="salario" type="number" min="1" step="1000" placeholder="Ej: 1750905"
                value={form.salario} onChange={handleChange("salario")} {...invalido("salario")} />
            </Campo>
          </div>
        </Seccion>

        <Seccion
          numero={4}
          titulo="Credenciales y acceso de usuario"
          descripcion="Configuración de acceso al sistema de Villa Katy Club Campestre"
          icono={LockKeyhole}
        >
          <fieldset className="re-fieldset" disabled={!form.crearUsuario}>
            <legend className="re-sr-only">Credenciales de acceso</legend>
            <div className="re-grid re-grid--2">
              <Campo
                id="idRol"
                label="Rol en el sistema"
                requerido
                error={errores.idRol}
                ayuda="Determina los módulos y permisos asignados"
              >
                <select id="idRol" value={form.idRol} onChange={handleChange("idRol")} {...invalido("idRol")}>
                  <option value="" disabled>Selecciona un rol</option>
                  {roles.map((r) => (
                    <option key={r.idRol} value={r.idRol}>{r.nombreRol}</option>
                  ))}
                </select>
              </Campo>
              <Campo
                id="nombreUsuario"
                label="Nombre de usuario"
                requerido
                error={errores.nombreUsuario}
                ayuda="Identificador único para iniciar sesión en el club"
              >
                <div className="re-input-icon">
                  <User size={16} aria-hidden="true" />
                  <input id="nombreUsuario" type="text" maxLength={50} autoComplete="off" placeholder="Ej: mrestrepo"
                    value={form.nombreUsuario} onChange={handleChange("nombreUsuario")} {...invalido("nombreUsuario")} />
                </div>
              </Campo>
              <Campo
                id="contrasena"
                label="Contraseña"
                requerido
                error={errores.contrasena}
                ayuda="Mínimo 8 caracteres, con mayúsculas, minúsculas y números"
              >
                <div className="re-input-icon re-input-password">
                  <KeyRound size={16} aria-hidden="true" />
                  <input id="contrasena" type={verContrasena ? "text" : "password"} autoComplete="new-password"
                    placeholder="••••••••" value={form.contrasena} onChange={handleChange("contrasena")}
                    {...invalido("contrasena")} />
                  <button
                    type="button"
                    className="re-password-toggle"
                    onClick={() => setVerContrasena((v) => !v)}
                    aria-label={verContrasena ? "Ocultar contraseña" : "Mostrar contraseña"}
                    aria-pressed={verContrasena}
                  >
                    {verContrasena ? <EyeOff size={16} aria-hidden="true" /> : <Eye size={16} aria-hidden="true" />}
                  </button>
                </div>
              </Campo>
              <Campo
                id="confirmarContrasena"
                label="Confirmar contraseña"
                requerido
                error={errores.confirmarContrasena}
                ayuda="Debe coincidir con la contraseña ingresada"
              >
                <div className="re-input-icon">
                  <ShieldCheck size={16} aria-hidden="true" />
                  <input id="confirmarContrasena" type={verContrasena ? "text" : "password"} autoComplete="new-password"
                    placeholder="••••••••" value={form.confirmarContrasena} onChange={handleChange("confirmarContrasena")}
                    {...invalido("confirmarContrasena")} />
                </div>
              </Campo>
            </div>
          </fieldset>

          <div className="re-card__footer">
            <label className="re-checkbox">
              <input type="checkbox" checked={form.crearUsuario} onChange={handleChange("crearUsuario")} />
              Habilitar acceso inmediato al sistema tras guardar
            </label>
            <span className="re-help">
              {form.crearUsuario ? "Se creará el usuario" : "El empleado se registra sin usuario"}
            </span>
          </div>
        </Seccion>

        <div className="re-card re-actions">
          <div className="re-actions__left">
            <button type="button" className="re-btn re-btn--soft" onClick={limpiarFormulario}>
              <RotateCcw size={16} aria-hidden="true" />
              Limpiar formulario
            </button>
            <button type="button" className="re-btn re-btn--danger" onClick={onCancelar}>
              <X size={16} aria-hidden="true" />
              Cancelar registro
            </button>
          </div>
          <div className="re-actions__right">
            {errorServidor && <p className="re-error" role="alert">{errorServidor}</p>}
            {exito && <p className="re-success" role="status">{exito}</p>}
            <button type="submit" className="re-btn re-btn--primary" disabled={guardando}>
              <Save size={16} aria-hidden="true" />
              {guardando ? "Guardando..." : "Guardar y registrar empleado"}
            </button>
          </div>
        </div>
      </form>
    </section>
  );
}
