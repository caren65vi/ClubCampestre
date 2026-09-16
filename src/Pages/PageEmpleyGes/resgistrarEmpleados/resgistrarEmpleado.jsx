import { useState } from "react";
import { Plus, Trash2 } from "lucide-react";
import "./resgistrarEmpleado.css";

const tipoDocumentoOptions = [
  "Cédula de ciudadanía",
  "Cédula de extranjería",
  "Pasaporte",
  "Tarjeta de identidad",
];

const areaOptions = ["Cocina", "Bar", "Recepción", "Mantenimiento"];

const contactoTipoOptions = ["Teléfono", "Correo"];

let contactoIdCounter = 2;

export default function RegistrarEmpleado({ onCancelar, onGuardar }) {
  const [form, setForm] = useState({
    tipoDocumento: "",
    numeroDocumento: "",
    primerNombre: "",
    segundoNombre: "",
    primerApellido: "",
    segundoApellido: "",
    area: "",
    cargo: "",
    fechaContratacion: "",
    salario: "",
  });

  const [contactos, setContactos] = useState([
    { id: 1, tipo: "Teléfono", indicativo: "+57", valor: "" },
    { id: 2, tipo: "Correo", valor: "" },
  ]);

  const handleChange = (field) => (e) =>
    setForm((prev) => ({ ...prev, [field]: e.target.value }));

  const handleContactoChange = (id, field) => (e) => {
    const value = e.target.value;
    setContactos((prev) =>
      prev.map((c) => (c.id === id ? { ...c, [field]: value } : c)),
    );
  };

  const agregarContacto = () => {
    contactoIdCounter += 1;
    setContactos((prev) => [
      ...prev,
      { id: contactoIdCounter, tipo: "Teléfono", indicativo: "+57", valor: "" },
    ]);
  };

  const eliminarContacto = (id) => {
    setContactos((prev) => prev.filter((c) => c.id !== id));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onGuardar?.({ ...form, contactos });
  };

  return (
    <section className="registrar-empleado-page">
      <header className="registrar-empleado-page__header">
        <span>Gestión de personal</span>
        <h1>Registrar empleado</h1>
      </header>
      <form className="registrar-empleado" onSubmit={handleSubmit}>
      {/* IDENTIFICACIÓN */}
      <section className="re-section">
        <h3 className="re-section__title">Identificación</h3>
        <div className="re-grid">
          <div className="re-field">
            <label>Tipo de documento</label>
            <select
              value={form.tipoDocumento}
              onChange={handleChange("tipoDocumento")}
            >
              <option value="" disabled>
                Selecciona un tipo
              </option>
              {tipoDocumentoOptions.map((opt) => (
                <option key={opt} value={opt}>
                  {opt}
                </option>
              ))}
            </select>
          </div>
          <div className="re-field">
            <label>Número de documento</label>
            <input
              type="text"
              placeholder="1023456789"
              value={form.numeroDocumento}
              onChange={handleChange("numeroDocumento")}
            />
          </div>
        </div>
      </section>

      {/* DATOS PERSONALES */}
      <section className="re-section">
        <h3 className="re-section__title">Datos personales</h3>
        <div className="re-grid">
          <div className="re-field">
            <label>Primer nombre</label>
            <input
              type="text"
              value={form.primerNombre}
              onChange={handleChange("primerNombre")}
            />
          </div>
          <div className="re-field">
            <label>Segundo nombre</label>
            <input
              type="text"
              value={form.segundoNombre}
              onChange={handleChange("segundoNombre")}
            />
          </div>
          <div className="re-field">
            <label>Primer apellido</label>
            <input
              type="text"
              value={form.primerApellido}
              onChange={handleChange("primerApellido")}
            />
          </div>
          <div className="re-field">
            <label>Segundo apellido</label>
            <input
              type="text"
              value={form.segundoApellido}
              onChange={handleChange("segundoApellido")}
            />
          </div>
        </div>
      </section>

      {/* CONTACTO */}
      <section className="re-section">
        <div className="re-section__header">
          <h3 className="re-section__title">Contacto</h3>
          <button
            type="button"
            className="re-link-btn"
            onClick={agregarContacto}
          >
            <Plus size={15} />
            Agregar otro contacto
          </button>
        </div>

        {contactos.map((contacto) => (
          <div className="re-contacto-row" key={contacto.id}>
            <div className="re-field re-field--tipo">
              <label>Tipo</label>
              <select
                value={contacto.tipo}
                onChange={handleContactoChange(contacto.id, "tipo")}
              >
                {contactoTipoOptions.map((opt) => (
                  <option key={opt} value={opt}>
                    {opt}
                  </option>
                ))}
              </select>
            </div>

            {contacto.tipo === "Teléfono" ? (
              <>
                <div className="re-field re-field--indicativo">
                  <label>Indicativo</label>
                  <input
                    type="text"
                    placeholder="+57"
                    value={contacto.indicativo}
                    onChange={handleContactoChange(contacto.id, "indicativo")}
                  />
                </div>
                <div className="re-field re-field--valor">
                  <label>Número</label>
                  <input
                    type="text"
                    placeholder="300 000 0000"
                    value={contacto.valor}
                    onChange={handleContactoChange(contacto.id, "valor")}
                  />
                </div>
              </>
            ) : (
              <div className="re-field re-field--valor re-field--correo">
                <label>Correo electrónico</label>
                <input
                  type="email"
                  placeholder="nombre@villakaty.com"
                  value={contacto.valor}
                  onChange={handleContactoChange(contacto.id, "valor")}
                />
              </div>
            )}

            <button
              type="button"
              className="re-delete-btn"
              onClick={() => eliminarContacto(contacto.id)}
              aria-label="Eliminar contacto"
            >
              <Trash2 size={16} />
            </button>
          </div>
        ))}
      </section>

      {/* INFORMACIÓN LABORAL */}
      <section className="re-section">
        <h3 className="re-section__title">Información laboral</h3>
        <div className="re-grid">
          <div className="re-field">
            <label>Área</label>
            <select value={form.area} onChange={handleChange("area")}>
              <option value="" disabled>
                Selecciona un área
              </option>
              {areaOptions.map((opt) => (
                <option key={opt} value={opt}>
                  {opt}
                </option>
              ))}
            </select>
          </div>
          <div className="re-field">
            <label>Cargo</label>
            <input
              type="text"
              placeholder="Chef de línea"
              value={form.cargo}
              onChange={handleChange("cargo")}
            />
          </div>
          <div className="re-field">
            <label>Fecha de contratación</label>
            <input
              type="date"
              value={form.fechaContratacion}
              onChange={handleChange("fechaContratacion")}
            />
          </div>
          <div className="re-field">
            <label>Salario</label>
            <input
              type="number"
              placeholder="1300000"
              value={form.salario}
              onChange={handleChange("salario")}
            />
          </div>
        </div>
      </section>

      {/* ACCIONES */}
      <div className="re-actions">
        <button
          type="button"
          className="re-btn re-btn--ghost"
          onClick={onCancelar}
        >
          Cancelar
        </button>
        <button type="submit" className="re-btn re-btn--primary">
          Guardar empleado
        </button>
      </div>
      </form>
    </section>
  );
}
