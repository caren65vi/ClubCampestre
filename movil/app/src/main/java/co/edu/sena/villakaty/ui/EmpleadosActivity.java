package co.edu.sena.villakaty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import co.edu.sena.villakaty.R;
import co.edu.sena.villakaty.datos.ApiCliente;
import co.edu.sena.villakaty.datos.ApiException;
import co.edu.sena.villakaty.datos.SesionManager;
import co.edu.sena.villakaty.modelo.Empleado;
import co.edu.sena.villakaty.util.FiltroEmpleados;
import co.edu.sena.villakaty.util.Tareas;

/**
 * RF-0110 y RF-0112: lista de empleados con indicadores por estado, búsqueda y filtro.
 */
public class EmpleadosActivity extends AppCompatActivity {

    private SesionManager sesion;
    private ApiCliente api;
    private final List<Empleado> todos = new ArrayList<>();
    private EmpleadoAdapter adaptador;
    private TextInputEditText campoBuscar;
    private ChipGroup grupoEstados;
    private TextView textoVacio;
    private ProgressBar cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados);
        sesion = new SesionManager(this);
        api = new ApiCliente(sesion);

        MaterialToolbar barra = findViewById(R.id.barra);
        barra.setSubtitle("Sesión: " + sesion.getUsuario() + " (" + sesion.getRol() + ")");
        barra.inflateMenu(R.menu.menu_empleados);
        barra.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.accionCerrarSesion) {
                cerrarSesion();
            } else {
                cargarEmpleados();
            }
            return true;
        });

        campoBuscar = findViewById(R.id.campoBuscar);
        grupoEstados = findViewById(R.id.grupoEstados);
        textoVacio = findViewById(R.id.textoVacio);
        cargando = findViewById(R.id.cargando);

        RecyclerView lista = findViewById(R.id.listaEmpleados);
        lista.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new EmpleadoAdapter(this::abrirDetalle);
        lista.setAdapter(adaptador);

        campoBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { aplicarFiltros(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        grupoEstados.setOnCheckedStateChangeListener((grupo, seleccion) -> aplicarFiltros());
        findViewById(R.id.botonRegistrar).setOnClickListener(
                v -> startActivity(new Intent(this, RegistroEmpleadoActivity.class)));
    }

    /** Se recarga al volver de registrar o de cambiar un estado. */
    @Override
    protected void onResume() {
        super.onResume();
        cargarEmpleados();
    }

    private void cargarEmpleados() {
        cargando.setVisibility(View.VISIBLE);
        Tareas.ejecutar(api::listarEmpleados, new Tareas.Respuesta<List<Empleado>>() {
            @Override
            public void exito(List<Empleado> empleados) {
                cargando.setVisibility(View.GONE);
                todos.clear();
                todos.addAll(empleados);
                actualizarIndicadores();
                aplicarFiltros();
            }

            @Override
            public void error(ApiException error) {
                cargando.setVisibility(View.GONE);
                Toast.makeText(EmpleadosActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                if (error.esSesionInvalida()) {
                    cerrarSesion();
                }
            }
        });
    }

    private void actualizarIndicadores() {
        ((TextView) findViewById(R.id.indicadorTotal)).setText(getString(R.string.total) + "\n" + todos.size());
        ((TextView) findViewById(R.id.indicadorActivos)).setText(getString(R.string.activos) + "\n"
                + FiltroEmpleados.contar(todos, Empleado.ESTADO_ACTIVO));
        ((TextView) findViewById(R.id.indicadorVacaciones)).setText(getString(R.string.vacaciones) + "\n"
                + FiltroEmpleados.contar(todos, Empleado.ESTADO_VACACIONES));
        ((TextView) findViewById(R.id.indicadorInactivos)).setText(getString(R.string.inactivos) + "\n"
                + FiltroEmpleados.contar(todos, Empleado.ESTADO_INACTIVO));
    }

    private void aplicarFiltros() {
        String texto = campoBuscar.getText() == null ? "" : campoBuscar.getText().toString();
        List<Empleado> filtrados = FiltroEmpleados.filtrar(todos, texto, estadoSeleccionado());
        adaptador.mostrar(filtrados);
        textoVacio.setVisibility(filtrados.isEmpty() && cargando.getVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
    }

    private String estadoSeleccionado() {
        int chip = grupoEstados.getCheckedChipId();
        if (chip == R.id.chipActivos) {
            return Empleado.ESTADO_ACTIVO;
        } else if (chip == R.id.chipVacaciones) {
            return Empleado.ESTADO_VACACIONES;
        } else if (chip == R.id.chipInactivos) {
            return Empleado.ESTADO_INACTIVO;
        }
        return null;
    }

    private void abrirDetalle(Empleado empleado) {
        Intent intento = new Intent(this, DetalleEmpleadoActivity.class);
        intento.putExtra(DetalleEmpleadoActivity.EXTRA_DOCUMENTO, empleado.getNumeroDocumento());
        startActivity(intento);
    }

    private void cerrarSesion() {
        sesion.cerrar();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
