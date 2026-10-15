package co.edu.sena.villakaty.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import co.edu.sena.villakaty.R;
import co.edu.sena.villakaty.datos.ApiCliente;
import co.edu.sena.villakaty.datos.ApiException;
import co.edu.sena.villakaty.datos.SesionManager;
import co.edu.sena.villakaty.modelo.Catalogo;
import co.edu.sena.villakaty.modelo.Empleado;
import co.edu.sena.villakaty.util.Tareas;
import co.edu.sena.villakaty.util.Validador;

/**
 * RF-0111: registro de un empleado desde el celular.
 * Carga los catálogos (tipos de documento y cargos), valida y envía POST /api/empleados.
 */
public class RegistroEmpleadoActivity extends AppCompatActivity {

    private ApiCliente api;
    private Spinner listaTipoDocumento;
    private Spinner listaCargo;
    private TextView textoError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_empleado);
        api = new ApiCliente(new SesionManager(this));

        ((com.google.android.material.appbar.MaterialToolbar) findViewById(R.id.barra))
                .setNavigationOnClickListener(v -> finish());
        listaTipoDocumento = findViewById(R.id.listaTipoDocumento);
        listaCargo = findViewById(R.id.listaCargo);
        textoError = findViewById(R.id.textoError);
        findViewById(R.id.botonGuardar).setOnClickListener(v -> guardar());
        limpiarErrorAlEscribir(R.id.campoDocumento, R.id.capaDocumento);
        limpiarErrorAlEscribir(R.id.campoPrimerNombre, R.id.capaPrimerNombre);
        limpiarErrorAlEscribir(R.id.campoSegundoNombre, R.id.capaSegundoNombre);
        limpiarErrorAlEscribir(R.id.campoPrimerApellido, R.id.capaPrimerApellido);
        limpiarErrorAlEscribir(R.id.campoSegundoApellido, R.id.capaSegundoApellido);
        limpiarErrorAlEscribir(R.id.campoCelular, R.id.capaCelular);
        limpiarErrorAlEscribir(R.id.campoCorreo, R.id.capaCorreo);
        limpiarErrorAlEscribir(R.id.campoFecha, R.id.capaFecha);
        limpiarErrorAlEscribir(R.id.campoSalario, R.id.capaSalario);
        cargarCatalogos();
    }

    /** Quita el mensaje de error de un campo en cuanto el usuario lo corrige. */
    private void limpiarErrorAlEscribir(int idCampo, int idCapa) {
        TextInputEditText campo = findViewById(idCampo);
        campo.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { marcar(idCapa, null); }
            @Override public void afterTextChanged(android.text.Editable s) { }
        });
    }

    private void cargarCatalogos() {
        Tareas.ejecutar(api::listarTiposDocumento, new Tareas.Respuesta<List<Catalogo>>() {
            @Override public void exito(List<Catalogo> tipos) { llenar(listaTipoDocumento, tipos); }
            @Override public void error(ApiException e) { mostrarError(e.getMessage()); }
        });
        Tareas.ejecutar(api::listarCargos, new Tareas.Respuesta<List<Catalogo>>() {
            @Override public void exito(List<Catalogo> cargos) { llenar(listaCargo, cargos); }
            @Override public void error(ApiException e) { mostrarError(e.getMessage()); }
        });
    }

    private void llenar(Spinner lista, List<Catalogo> elementos) {
        ArrayAdapter<Catalogo> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, elementos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista.setAdapter(adaptador);
    }

    /** Valida con Validador; si todo está bien, envía el registro al back-end. */
    private void guardar() {
        Map<String, String> errores = Validador.validarRegistro(texto(R.id.campoDocumento), texto(R.id.campoPrimerNombre),
                texto(R.id.campoSegundoNombre), texto(R.id.campoPrimerApellido), texto(R.id.campoSegundoApellido),
                texto(R.id.campoCelular), texto(R.id.campoCorreo), texto(R.id.campoFecha), texto(R.id.campoSalario));
        marcar(R.id.capaDocumento, errores.get("documento"));
        marcar(R.id.capaPrimerNombre, errores.get("primerNombre"));
        marcar(R.id.capaSegundoNombre, errores.get("segundoNombre"));
        marcar(R.id.capaPrimerApellido, errores.get("primerApellido"));
        marcar(R.id.capaSegundoApellido, errores.get("segundoApellido"));
        marcar(R.id.capaCelular, errores.get("celular"));
        marcar(R.id.capaCorreo, errores.get("correo"));
        marcar(R.id.capaFecha, errores.get("fecha"));
        marcar(R.id.capaSalario, errores.get("salario"));
        textoError.setVisibility(View.GONE);
        if (!errores.isEmpty() || listaCargo.getSelectedItem() == null || listaTipoDocumento.getSelectedItem() == null) {
            if (errores.isEmpty()) {
                mostrarError("Espera a que carguen los tipos de documento y los cargos.");
            }
            return;
        }

        Empleado nuevo = new Empleado();
        nuevo.setIdTipoDocumento(((Catalogo) listaTipoDocumento.getSelectedItem()).getId());
        nuevo.setNumeroDocumento(texto(R.id.campoDocumento));
        nuevo.setPrimerNombre(texto(R.id.campoPrimerNombre));
        nuevo.setSegundoNombre(texto(R.id.campoSegundoNombre));
        nuevo.setPrimerApellido(texto(R.id.campoPrimerApellido));
        nuevo.setSegundoApellido(texto(R.id.campoSegundoApellido));
        nuevo.setTelefono(texto(R.id.campoCelular).replace(" ", ""));
        nuevo.setCorreo(texto(R.id.campoCorreo));
        nuevo.setIdCargo(((Catalogo) listaCargo.getSelectedItem()).getId());
        nuevo.setFechaContratacion(texto(R.id.campoFecha));
        nuevo.setSalario(Double.parseDouble(texto(R.id.campoSalario)));

        findViewById(R.id.botonGuardar).setEnabled(false);
        Tareas.ejecutar(() -> api.registrarEmpleado(nuevo), new Tareas.Respuesta<Empleado>() {
            @Override
            public void exito(Empleado creado) {
                Toast.makeText(RegistroEmpleadoActivity.this,
                        creado.getNombreCompleto() + " quedó registrado con éxito.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void error(ApiException error) {
                findViewById(R.id.botonGuardar).setEnabled(true);
                mostrarError(error.getMessage());
            }
        });
    }

    private void marcar(int idCapa, String error) {
        ((TextInputLayout) findViewById(idCapa)).setError(error);
    }

    private void mostrarError(String mensaje) {
        textoError.setText(mensaje);
        textoError.setVisibility(View.VISIBLE);
    }

    private String texto(int idCampo) {
        TextInputEditText campo = findViewById(idCampo);
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
