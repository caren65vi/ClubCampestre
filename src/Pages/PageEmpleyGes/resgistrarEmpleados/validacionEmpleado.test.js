import { describe, expect, it } from "vitest";
import { validar } from "./validacionEmpleado";

const CEDULA = { idTipoDocumento: 1, nombreTipoDocumento: "CC" };
const PASAPORTE = { idTipoDocumento: 2, nombreTipoDocumento: "PA" };
const TIPOS_CONTACTO = [
  { idTipoContacto: 1, nombreTipoContacto: "Celular" },
  { idTipoContacto: 2, nombreTipoContacto: "Correo electrónico" },
];

const formValido = (cambios = {}) => ({
  idTipoDocumento: "1",
  numeroDocumento: "1020304050",
  primerNombre: "Mateo",
  segundoNombre: "",
  primerApellido: "Restrepo",
  segundoApellido: "",
  indicativo: "+57",
  telefono: "3124567890",
  correo: "mateo@correo.com",
  idArea: "1",
  idCargo: "2",
  fechaContratacion: "2026-01-15",
  salario: "1750905",
  crearUsuario: true,
  idRol: "1",
  nombreUsuario: "mrestrepo",
  contrasena: "Clave2026",
  confirmarContrasena: "Clave2026",
  ...cambios,
});

const validarForm = (cambios, { tipoDocumento = CEDULA, contactos = [] } = {}) =>
  validar(formValido(cambios), contactos, TIPOS_CONTACTO, tipoDocumento);

describe("Formulario válido", () => {
  it("no devuelve errores con todos los datos correctos", () => {
    expect(validarForm()).toEqual({});
  });
});

describe("Campo obligatorio: dejar el campo vacío", () => {
  it.each([
    ["idTipoDocumento", "Selecciona el tipo de documento."],
    ["numeroDocumento", "Escribe el número de documento."],
    ["primerNombre", "Escribe el primer nombre."],
    ["primerApellido", "Escribe el primer apellido."],
    ["telefono", "Escribe el número de celular."],
    ["correo", "Escribe el correo electrónico."],
    ["idArea", "Selecciona el área."],
    ["idCargo", "Selecciona el cargo."],
    ["fechaContratacion", "Selecciona la fecha de ingreso."],
    ["salario", "Escribe el salario."],
    ["idRol", "Selecciona el rol."],
    ["nombreUsuario", "Escribe el nombre de usuario."],
    ["contrasena", "Escribe la contraseña."],
  ])("%s vacío muestra '%s'", (campo, mensaje) => {
    expect(validarForm({ [campo]: "" })[campo]).toBe(mensaje);
  });

  it("un campo con solo espacios cuenta como vacío", () => {
    expect(validarForm({ primerNombre: "   " }).primerNombre).toBe("Escribe el primer nombre.");
  });

  it("los campos opcionales pueden quedar vacíos", () => {
    expect(validarForm({ segundoNombre: "", segundoApellido: "" })).toEqual({});
  });

  it("los datos de acceso no son obligatorios si no se crea usuario", () => {
    const errores = validarForm({
      crearUsuario: false,
      idRol: "",
      nombreUsuario: "",
      contrasena: "",
      confirmarContrasena: "",
    });
    expect(errores).toEqual({});
  });

  it("un contacto adicional sin valor muestra error", () => {
    const errores = validarForm({}, { contactos: [{ id: 7, idTipoContacto: "1", valor: "" }] });
    expect(errores["extra-7"]).toBe("Escribe el contacto o elimina la fila.");
  });
});

describe("Texto: nombre con letras y espacios", () => {
  it.each(["Mateo", "María José", "Ñúñez", "O'Connor", "José Luis Pérez"])("acepta '%s'", (nombre) => {
    const errores = validarForm({
      primerNombre: nombre,
      segundoNombre: nombre,
      primerApellido: nombre,
      segundoApellido: nombre,
    });
    expect(errores).toEqual({});
  });
});

describe("Longitud mínima", () => {
  it("rechaza un documento de menos de 5 dígitos", () => {
    expect(validarForm({ numeroDocumento: "1234" }).numeroDocumento).toBe(
      "Usa solo números, entre 5 y 20 dígitos.",
    );
  });

  it("acepta un documento de exactamente 5 dígitos", () => {
    expect(validarForm({ numeroDocumento: "12345" }).numeroDocumento).toBeUndefined();
  });

  it("rechaza un celular de menos de 7 dígitos", () => {
    expect(validarForm({ telefono: "312456" }).telefono).toBe("Usa solo números, entre 7 y 10 dígitos.");
  });

  it("rechaza un nombre de usuario de menos de 4 caracteres", () => {
    expect(validarForm({ nombreUsuario: "mat" }).nombreUsuario).toMatch(/entre 4 y 50/);
  });

  it("rechaza una contraseña de menos de 8 caracteres", () => {
    const errores = validarForm({ contrasena: "Clave1", confirmarContrasena: "Clave1" });
    expect(errores.contrasena).toBe("Mínimo 8 caracteres, con una mayúscula, una minúscula y un número.");
  });
});

describe("Longitud máxima", () => {
  it("rechaza un documento de más de 20 dígitos", () => {
    expect(validarForm({ numeroDocumento: "1".repeat(21) }).numeroDocumento).toBe(
      "Usa solo números, entre 5 y 20 dígitos.",
    );
  });

  it("acepta un documento de exactamente 20 dígitos", () => {
    expect(validarForm({ numeroDocumento: "1".repeat(20) }).numeroDocumento).toBeUndefined();
  });

  it("rechaza un pasaporte de más de 20 caracteres", () => {
    const errores = validarForm({ numeroDocumento: "AB".repeat(11) }, { tipoDocumento: PASAPORTE });
    expect(errores.numeroDocumento).toBe("Usa entre 5 y 20 letras o números.");
  });

  it("rechaza un celular de más de 10 dígitos", () => {
    expect(validarForm({ telefono: "31245678901" }).telefono).toBe("Usa solo números, entre 7 y 10 dígitos.");
  });

  it("rechaza un nombre de usuario de más de 50 caracteres", () => {
    expect(validarForm({ nombreUsuario: "a".repeat(51) }).nombreUsuario).toMatch(/entre 4 y 50/);
  });
});

describe("Número: valor numérico válido", () => {
  it("acepta un salario positivo", () => {
    expect(validarForm({ salario: "2500000" }).salario).toBeUndefined();
  });

  it("acepta un celular con espacios entre los dígitos", () => {
    expect(validarForm({ telefono: "312 456 7890" }).telefono).toBeUndefined();
  });

  it("acepta un documento de solo números para cédula", () => {
    expect(validarForm({ numeroDocumento: "98765432" }).numeroDocumento).toBeUndefined();
  });
});

describe("Número inválido: texto en un campo numérico", () => {
  it("rechaza letras en el documento cuando es cédula", () => {
    expect(validarForm({ numeroDocumento: "10A2030" }).numeroDocumento).toBe(
      "Usa solo números, entre 5 y 20 dígitos.",
    );
  });

  it("acepta letras en el documento cuando es pasaporte", () => {
    const errores = validarForm({ numeroDocumento: "AB12345" }, { tipoDocumento: PASAPORTE });
    expect(errores.numeroDocumento).toBeUndefined();
  });

  it("rechaza letras en el celular", () => {
    expect(validarForm({ telefono: "31245abc90" }).telefono).toBe("Usa solo números, entre 7 y 10 dígitos.");
  });

  it("rechaza letras en un contacto adicional de tipo celular", () => {
    const errores = validarForm({}, { contactos: [{ id: 3, idTipoContacto: "1", valor: "tres uno dos" }] });
    expect(errores["extra-3"]).toBe("Usa solo números, entre 7 y 10 dígitos.");
  });

  it.each(["0", "-1500"])("rechaza el salario %s", (salario) => {
    expect(validarForm({ salario }).salario).toBe("El salario debe ser mayor a 0.");
  });
});

describe("Fecha: formato válido", () => {
  it("acepta una fecha de ingreso con formato AAAA-MM-DD", () => {
    expect(validarForm({ fechaContratacion: "2026-03-01" }).fechaContratacion).toBeUndefined();
  });
});

describe("Fecha inválida", () => {
  // El <input type="date"> entrega "" cuando la fecha está incompleta o mal escrita,
  // así que en el formulario llega como campo vacío. El formato y las fechas no
  // permitidas también se validan en el backend (ver pruebas de EmpleadoService).
  it("una fecha incompleta llega vacía y muestra validación", () => {
    expect(validarForm({ fechaContratacion: "" }).fechaContratacion).toBe("Selecciona la fecha de ingreso.");
  });
});

describe("Caracteres especiales: símbolos no permitidos", () => {
  it.each(["Mat3o", "Mateo@", "<script>", "Ana_María", "Luis!"])("rechaza '%s' en el nombre", (nombre) => {
    expect(validarForm({ primerNombre: nombre }).primerNombre).toBe("Usa solo letras.");
  });

  it("rechaza símbolos en los apellidos", () => {
    const errores = validarForm({ primerApellido: "Restrepo#", segundoApellido: "Mont*ya" });
    expect(errores.primerApellido).toBe("Usa solo letras.");
    expect(errores.segundoApellido).toBe("Usa solo letras.");
  });

  it.each(["m restrepo", "m#restrepo", "mrestrepo!"])("rechaza '%s' como nombre de usuario", (usuario) => {
    expect(validarForm({ nombreUsuario: usuario }).nombreUsuario).toMatch(/sin espacios/);
  });

  it("acepta puntos, guiones, guion bajo y @ en el nombre de usuario", () => {
    expect(validarForm({ nombreUsuario: "m.restrepo_01@club-x" }).nombreUsuario).toBeUndefined();
  });

  it.each(["mateo@@correo.com", "mateo correo@x.com", "mateo.com", "mateo@correo"])(
    "rechaza el correo '%s'",
    (correo) => {
      expect(validarForm({ correo }).correo).toBe("Escribe un correo válido, por ejemplo nombre@correo.com.");
    },
  );

  it("rechaza un correo mal escrito en un contacto adicional", () => {
    const errores = validarForm({}, { contactos: [{ id: 4, idTipoContacto: "2", valor: "ana@@x" }] });
    expect(errores["extra-4"]).toBe("Escribe un correo válido.");
  });
});

describe("Confirmación de contraseña", () => {
  it("muestra error si las contraseñas no coinciden", () => {
    expect(validarForm({ confirmarContrasena: "Otra2026" }).confirmarContrasena).toBe(
      "Las contraseñas no coinciden.",
    );
  });
});

// Duplicidad (documento, nombre de usuario, número de habitación) la valida el backend
// y responde 409; ver EmpleadoServiceValidacionTest y HabitacionServiceValidacionTest.
