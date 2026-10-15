package co.edu.sena.villakaty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import co.edu.sena.villakaty.datos.EmpleadoParser;
import co.edu.sena.villakaty.modelo.Catalogo;
import co.edu.sena.villakaty.modelo.Empleado;

/** Pruebas unitarias de la conversión JSON ↔ objetos (datos de entrada y salida de la API). */
public class EmpleadoParserTest {

    private static final String RESPUESTA = "[{\"numeroDocumento\":\"1098765432\",\"nombreTipoDocumento\":\"CC\","
            + "\"primerNombre\":\"Andrea\",\"segundoNombre\":\"Paola\",\"primerApellido\":\"Ríos\","
            + "\"segundoApellido\":null,\"nombreCargo\":\"Mesero\",\"nombreArea\":\"Restaurante\","
            + "\"telefono\":\"3209876543\",\"salario\":1750905,\"estado\":\"vacaciones\"}]";

    @Test
    public void leeLaListaDeEmpleados() throws Exception {
        List<Empleado> lista = EmpleadoParser.listaDesdeJson(RESPUESTA);
        Empleado e = lista.get(0);
        assertEquals("Andrea Paola Ríos", e.getNombreCompleto());
        assertEquals("AR", e.getIniciales());
        assertEquals("vacaciones", e.getEstado());
        assertEquals(1750905, e.getSalario(), 0.01);
    }

    @Test
    public void armaElCuerpoDelRegistro() throws Exception {
        Empleado e = new Empleado();
        e.setNumeroDocumento("1098765432");
        e.setPrimerNombre("Andrea");
        e.setSegundoNombre("  ");
        e.setIdCargo(6);
        JSONObject json = EmpleadoParser.empleadoAJson(e);
        assertEquals("1098765432", json.getString("numeroDocumento"));
        assertEquals("+57", json.getString("indicativo"));
        assertFalse(json.getBoolean("crearUsuario"));
        assertEquals(JSONObject.NULL, json.get("segundoNombre"));
    }

    @Test
    public void leeLosCargosConSuArea() throws Exception {
        List<Catalogo> cargos = EmpleadoParser.cargosDesdeJson(
                "[{\"idCargo\":6,\"nombreCargo\":\"Recepcionista\",\"nombreArea\":\"Recepción\"}]");
        assertEquals("Recepcionista (Recepción)", cargos.get(0).toString());
    }

    @Test
    public void extraeElMensajeDeError() {
        assertEquals("Ya existe un empleado con el documento 1",
                EmpleadoParser.mensajeDeError("{\"error\":\"Ya existe un empleado con el documento 1\"}", "x"));
        assertEquals("por defecto", EmpleadoParser.mensajeDeError("<html>", "por defecto"));
    }
}
