package co.edu.sena.villakaty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Map;

import co.edu.sena.villakaty.util.Validador;

/** Pruebas unitarias de las validaciones del registro de empleados (RF-0111). */
public class ValidadorTest {

    private Map<String, String> validar(String documento, String nombre, String apellido, String celular,
            String correo, String fecha, String salario) {
        return Validador.validarRegistro(documento, nombre, "", apellido, "", celular, correo, fecha, salario);
    }

    @Test
    public void datosValidosNoTienenErrores() {
        assertTrue(validar("1098765432", "Andrea", "Ríos", "3124567890", "andrea@villakaty.com", "2026-10-01", "1750905").isEmpty());
    }

    @Test
    public void camposObligatoriosVacios() {
        Map<String, String> errores = validar("", "", "", "", "", "", "");
        assertEquals(7, errores.size());
        assertEquals("Escribe el número de documento.", errores.get("documento"));
        assertEquals("Escribe el primer nombre.", errores.get("primerNombre"));
    }

    @Test
    public void documentoSoloNumeros() {
        assertEquals("Usa solo números, entre 5 y 20 dígitos.",
                validar("12AB#", "Andrea", "Ríos", "3124567890", "a@b.co", "2026-10-01", "100").get("documento"));
    }

    @Test
    public void nombreConCaracteresEspeciales() {
        assertEquals("Usa solo letras.",
                validar("1098765432", "Andrea3", "R@os", "3124567890", "a@b.co", "2026-10-01", "100").get("primerNombre"));
    }

    @Test
    public void celularConLongitudIncorrecta() {
        assertEquals("Usa solo números, entre 7 y 10 dígitos.",
                validar("1098765432", "Andrea", "Ríos", "31245", "a@b.co", "2026-10-01", "100").get("celular"));
    }

    @Test
    public void correoInvalido() {
        assertTrue(validar("1098765432", "Andrea", "Ríos", "3124567890", "andrea@", "2026-10-01", "100").containsKey("correo"));
    }

    @Test
    public void fechaConFormatoIncorrectoOFutura() {
        assertTrue(validar("1098765432", "Andrea", "Ríos", "3124567890", "a@b.co", "01/10/2026", "100").containsKey("fecha"));
        assertEquals("La fecha de ingreso no puede ser futura.",
                validar("1098765432", "Andrea", "Ríos", "3124567890", "a@b.co", "2999-01-01", "100").get("fecha"));
    }

    @Test
    public void salarioDebeSerMayorACero() {
        assertEquals("El salario debe ser mayor a 0.",
                validar("1098765432", "Andrea", "Ríos", "3124567890", "a@b.co", "2026-10-01", "-50000").get("salario"));
    }

    @Test
    public void motivoObligatorio() {
        assertEquals("Escribe el motivo o número de acta.", Validador.validarMotivo("  "));
        assertNull(Validador.validarMotivo("Vacaciones programadas"));
    }
}
