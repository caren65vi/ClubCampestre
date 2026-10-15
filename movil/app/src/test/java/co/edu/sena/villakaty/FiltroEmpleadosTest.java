package co.edu.sena.villakaty;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import co.edu.sena.villakaty.modelo.Empleado;
import co.edu.sena.villakaty.util.FiltroEmpleados;

/** Pruebas unitarias de la búsqueda y el filtro por estado (RF-0112). */
public class FiltroEmpleadosTest {

    private static Empleado empleado(String nombre, String apellido, String documento, String cargo, String estado) {
        Empleado e = new Empleado();
        e.setPrimerNombre(nombre);
        e.setPrimerApellido(apellido);
        e.setNumeroDocumento(documento);
        e.setNombreCargo(cargo);
        e.setEstado(estado);
        return e;
    }

    private final List<Empleado> empleados = Arrays.asList(
            empleado("Andrea", "Ríos", "1098765432", "Recepcionista", Empleado.ESTADO_ACTIVO),
            empleado("Julián", "Ortega", "1023456789", "Cocinero", Empleado.ESTADO_INACTIVO),
            empleado("Laura", "Martínez", "1000000001", "Administrador de Sistema", Empleado.ESTADO_VACACIONES));

    @Test
    public void buscaSinDistinguirTildesNiMayusculas() {
        assertEquals("Julián", FiltroEmpleados.filtrar(empleados, "JULIAN", null).get(0).getPrimerNombre());
    }

    @Test
    public void buscaPorDocumentoYCargo() {
        assertEquals(1, FiltroEmpleados.filtrar(empleados, "1098765432", null).size());
        assertEquals(1, FiltroEmpleados.filtrar(empleados, "cocinero", null).size());
    }

    @Test
    public void filtraPorEstado() {
        assertEquals(1, FiltroEmpleados.filtrar(empleados, "", Empleado.ESTADO_ACTIVO).size());
        assertEquals(3, FiltroEmpleados.filtrar(empleados, "", null).size());
    }

    @Test
    public void sinResultados() {
        assertEquals(0, FiltroEmpleados.filtrar(empleados, "Zacarías", null).size());
    }

    @Test
    public void cuentaPorEstado() {
        assertEquals(1, FiltroEmpleados.contar(empleados, Empleado.ESTADO_INACTIVO));
    }
}
