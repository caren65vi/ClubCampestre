package co.edu.sena.villakaty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.sena.villakaty.R;
import co.edu.sena.villakaty.datos.ApiCliente;
import co.edu.sena.villakaty.datos.ApiException;
import co.edu.sena.villakaty.datos.SesionManager;
import co.edu.sena.villakaty.util.Tareas;

/**
 * Inicio de sesión del colaborador contra el back-end (POST /api/auth/login).
 * Si ya hay una sesión guardada, pasa directo a la lista de empleados.
 */
public class LoginActivity extends AppCompatActivity {

    private SesionManager sesion;
    private TextInputLayout capaUsuario;
    private TextInputLayout capaContrasena;
    private TextInputEditText campoUsuario;
    private TextInputEditText campoContrasena;
    private TextView textoError;
    private MaterialButton botonIngresar;
    private ProgressBar cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sesion = new SesionManager(this);
        if (sesion.haySesion()) {
            abrirEmpleados();
            return;
        }
        setContentView(R.layout.activity_login);

        capaUsuario = findViewById(R.id.capaUsuario);
        capaContrasena = findViewById(R.id.capaContrasena);
        campoUsuario = findViewById(R.id.campoUsuario);
        campoContrasena = findViewById(R.id.campoContrasena);
        textoError = findViewById(R.id.textoError);
        botonIngresar = findViewById(R.id.botonIngresar);
        cargando = findViewById(R.id.cargando);

        botonIngresar.setOnClickListener(v -> ingresar());
    }

    /** Valida los campos y llama a la API en segundo plano. */
    private void ingresar() {
        String usuario = texto(campoUsuario);
        String contrasena = texto(campoContrasena);
        capaUsuario.setError(usuario.isEmpty() ? "Escribe tu usuario empresarial." : null);
        capaContrasena.setError(contrasena.isEmpty() ? "Escribe tu contraseña." : null);
        textoError.setVisibility(View.GONE);
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            return;
        }

        mostrarCargando(true);
        ApiCliente api = new ApiCliente(sesion);
        Tareas.ejecutar(() -> {
            api.iniciarSesion(usuario, contrasena);
            return Boolean.TRUE;
        }, new Tareas.Respuesta<Boolean>() {
            @Override
            public void exito(Boolean resultado) {
                abrirEmpleados();
            }

            @Override
            public void error(ApiException error) {
                mostrarCargando(false);
                textoError.setText(error.getMessage());
                textoError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void abrirEmpleados() {
        startActivity(new Intent(this, EmpleadosActivity.class));
        finish();
    }

    private void mostrarCargando(boolean visible) {
        cargando.setVisibility(visible ? View.VISIBLE : View.GONE);
        botonIngresar.setEnabled(!visible);
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
