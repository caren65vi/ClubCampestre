package co.edu.sena.villakaty.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import co.edu.sena.villakaty.R;
import co.edu.sena.villakaty.datos.ApiCliente;
import co.edu.sena.villakaty.datos.ApiException;
import co.edu.sena.villakaty.datos.SesionManager;
import co.edu.sena.villakaty.modelo.Empleado;
import co.edu.sena.villakaty.util.Tareas;
import co.edu.sena.villakaty.util.Validador;

/**
 * RF-0112 (consultar un empleado) y RF-0114 (cambiar su estado laboral con fecha efectiva y motivo).
 */
public class DetalleEmpleadoActivity extends AppCompatActivity {

    public static final String EXTRA_DOCUMENTO = "documento";

    private ApiCliente api;
    private String documento;
    private ChipGroup grupoNuevoEstado;
    private TextInputEditText campoFecha;
    private TextInputEditText campoMotivo;
    private TextInputLayout capaMotivo;
    private TextView textoMensaje;
    private MaterialButton botonAplicar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_empleado);
        api = new ApiCliente(new SesionManager(this));
        documento = getIntent().getStringExtra(EXTRA_DOCUMENTO);

        ((com.google.android.material.appbar.MaterialToolbar) findViewById(R.id.barra))
                .setNavigationOnClickListener(v -> finish());
        grupoNuevoEstado = findViewById(R.id.grupoNuevoEstado);
        campoFecha = findViewById(R.id.campoFecha);
        campoMotivo = findViewById(R.id.campoMotivo);
        capaMotivo = findViewById(R.id.capaMotivo);
        textoMensaje = findViewById(R.id.textoMensaje);
        botonAplicar = findViewById(R.id.botonAplicar);

        campoFecha.setText(LocalDate.now().toString());
        botonAplicar.setOnClickListener(v -> aplicarEstado());
        cargar();
    }

    private void cargar() {
        Tareas.ejecutar(() -> api.consultarEmpleado(documento), new Tareas.Respuesta<Empleado>() {
            @Override
            public void exito(Empleado empleado) {
                mostrar(empleado);
            }

            @Override
            public void error(ApiException error) {
                mostrarMensaje(error.getMessage(), false);
            }
        });
    }

    private void mostrar(Empleado e) {
        ((TextView) findViewById(R.id.textoIniciales)).setText(e.getIniciales());
        ((TextView) findViewById(R.id.textoNombre)).setText(e.getNombreCompleto());
        ((TextView) findViewById(R.id.textoCargo)).setText(e.getNombreCargo() + " • " + e.getNombreArea());
        EstadoVista.pintar(findViewById(R.id.textoEstado), e.getEstado());
        String salario = NumberFormat.getCurrencyInstance(new Locale("es", "CO")).format(e.getSalario());
        ((TextView) findViewById(R.id.textoDatos)).setText(
                "Documento: " + e.getNombreTipoDocumento() + " " + e.getNumeroDocumento()
                        + "\nFecha de ingreso: " + e.getFechaContratacion()
                        + "\nCelular: " + valor(e.getTelefono())
                        + "\nCorreo: " + valor(e.getCorreo())
                        + "\nSalario: " + salario);
    }

    /** Valida estado, fecha y motivo, y envía PATCH /api/empleados/{documento}/estado. */
    private void aplicarEstado() {
        String estado = estadoSeleccionado();
        String fecha = campoFecha.getText() == null ? "" : campoFecha.getText().toString().trim();
        String motivo = campoMotivo.getText() == null ? "" : campoMotivo.getText().toString().trim();
        capaMotivo.setError(Validador.validarMotivo(motivo));
        if (estado == null) {
            mostrarMensaje("Selecciona el nuevo estado.", false);
            return;
        }
        if (capaMotivo.getError() != null) {
            return;
        }
        botonAplicar.setEnabled(false);
        Tareas.ejecutar(() -> api.cambiarEstado(documento, estado, fecha, motivo), new Tareas.Respuesta<Empleado>() {
            @Override
            public void exito(Empleado empleado) {
                botonAplicar.setEnabled(true);
                campoMotivo.setText("");
                mostrar(empleado);
                mostrarMensaje("Estado actualizado a " + empleado.getEstado() + ".", true);
            }

            @Override
            public void error(ApiException error) {
                botonAplicar.setEnabled(true);
                mostrarMensaje(error.getMessage(), false);
            }
        });
    }

    private String estadoSeleccionado() {
        int chip = grupoNuevoEstado.getCheckedChipId();
        if (chip == R.id.chipActivo) {
            return Empleado.ESTADO_ACTIVO;
        } else if (chip == R.id.chipVacaciones) {
            return Empleado.ESTADO_VACACIONES;
        } else if (chip == R.id.chipInactivo) {
            return Empleado.ESTADO_INACTIVO;
        }
        return null;
    }

    private void mostrarMensaje(String mensaje, boolean exito) {
        textoMensaje.setText(mensaje);
        textoMensaje.setTextColor(ContextCompat.getColor(this, exito ? R.color.vk_activo : R.color.vk_inactivo));
        textoMensaje.setBackgroundColor(ContextCompat.getColor(this, exito ? R.color.vk_activo_fondo : R.color.vk_inactivo_fondo));
        textoMensaje.setVisibility(View.VISIBLE);
    }

    private static String valor(String texto) {
        return texto == null || texto.isEmpty() ? "—" : texto;
    }
}
