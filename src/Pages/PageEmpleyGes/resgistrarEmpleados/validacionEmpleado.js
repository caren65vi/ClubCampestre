/**
 * Reglas de validación del formulario de registro de empleados.
 * Devuelve un objeto { campo: mensaje } con los errores encontrados (vacío si todo es válido).
 */
// Mínimo 8 caracteres con mayúscula, minúscula y número
const CONTRASENA_SEGURA = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

// Nombres y apellidos: solo letras (con tildes y ñ), espacios y apóstrofo
const SOLO_LETRAS = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' ]+$/;
const CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const esCorreo = (tipo) => tipo?.nombreTipoContacto?.toLowerCase().includes("correo");

// Valida todo el formulario y devuelve los errores por campo
export function validar(form, contactosExtra, tiposContacto, tipoDocumento) {
  const errores = {};
  const requerido = (campo, mensaje) => {
    if (!String(form[campo]).trim()) errores[campo] = mensaje;
  };

  requerido("idTipoDocumento", "Selecciona el tipo de documento.");
  requerido("numeroDocumento", "Escribe el número de documento.");
  requerido("primerNombre", "Escribe el primer nombre.");
  requerido("primerApellido", "Escribe el primer apellido.");
  requerido("telefono", "Escribe el número de celular.");
  requerido("correo", "Escribe el correo electrónico.");
  requerido("idArea", "Selecciona el área.");
  requerido("idCargo", "Selecciona el cargo.");
  requerido("fechaContratacion", "Selecciona la fecha de ingreso.");
  requerido("salario", "Escribe el salario.");

  const documento = form.numeroDocumento.trim();
  const esPasaporte = tipoDocumento?.nombreTipoDocumento === "PA";
  if (documento && !errores.numeroDocumento) {
    const formato = esPasaporte ? /^[A-Za-z0-9]{5,20}$/ : /^\d{5,20}$/;
    if (!formato.test(documento)) {
      errores.numeroDocumento = esPasaporte
        ? "Usa entre 5 y 20 letras o números."
        : "Usa solo números, entre 5 y 20 dígitos.";
    }
  }

  for (const campo of ["primerNombre", "segundoNombre", "primerApellido", "segundoApellido"]) {
    const valor = form[campo].trim();
    if (valor && !SOLO_LETRAS.test(valor)) errores[campo] = "Usa solo letras.";
  }

  const celular = form.telefono.replace(/\s/g, "");
  if (celular && !/^\d{7,10}$/.test(celular)) {
    errores.telefono = "Usa solo números, entre 7 y 10 dígitos.";
  }
  if (form.correo.trim() && !CORREO.test(form.correo.trim())) {
    errores.correo = "Escribe un correo válido, por ejemplo nombre@correo.com.";
  }
  if (form.salario && Number(form.salario) <= 0) {
    errores.salario = "El salario debe ser mayor a 0.";
  }

  if (form.crearUsuario) {
    requerido("idRol", "Selecciona el rol.");
    requerido("nombreUsuario", "Escribe el nombre de usuario.");
    requerido("contrasena", "Escribe la contraseña.");
    requerido("confirmarContrasena", "Confirma la contraseña.");
    const usuario = form.nombreUsuario.trim();
    if (usuario && !/^[A-Za-z0-9._@-]{4,50}$/.test(usuario)) {
      errores.nombreUsuario = "Usa entre 4 y 50 letras, números, puntos, guiones o @, sin espacios.";
    }
    if (form.contrasena && !CONTRASENA_SEGURA.test(form.contrasena)) {
      errores.contrasena = "Mínimo 8 caracteres, con una mayúscula, una minúscula y un número.";
    }
    if (form.confirmarContrasena && form.confirmarContrasena !== form.contrasena) {
      errores.confirmarContrasena = "Las contraseñas no coinciden.";
    }
  }

  contactosExtra.forEach((c) => {
    const tipo = tiposContacto.find((t) => String(t.idTipoContacto) === String(c.idTipoContacto));
    const valor = c.valor.trim();
    if (!c.idTipoContacto) {
      errores[`extra-${c.id}`] = "Selecciona el tipo de contacto.";
    } else if (!valor) {
      errores[`extra-${c.id}`] = "Escribe el contacto o elimina la fila.";
    } else if (esCorreo(tipo) ? !CORREO.test(valor) : !/^\d{7,10}$/.test(valor.replace(/\s/g, ""))) {
      errores[`extra-${c.id}`] = esCorreo(tipo)
        ? "Escribe un correo válido."
        : "Usa solo números, entre 7 y 10 dígitos.";
    }
  });

  return errores;
}
