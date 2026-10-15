package co.edu.sena.villakaty.ui;

import android.content.res.ColorStateList;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import co.edu.sena.villakaty.R;
import co.edu.sena.villakaty.modelo.Empleado;

/**
 * Pinta la etiqueta de estado (Activo, Vacaciones, Inactivo) con el mismo color en todas las pantallas.
 */
final class EstadoVista {

    private EstadoVista() {
    }

    static void pintar(TextView etiqueta, String estado) {
        int texto;
        int fondo;
        String nombre;
        if (Empleado.ESTADO_VACACIONES.equals(estado)) {
            texto = R.color.vk_vacaciones;
            fondo = R.color.vk_vacaciones_fondo;
            nombre = "Vacaciones";
        } else if (Empleado.ESTADO_INACTIVO.equals(estado)) {
            texto = R.color.vk_inactivo;
            fondo = R.color.vk_inactivo_fondo;
            nombre = "Inactivo";
        } else {
            texto = R.color.vk_activo;
            fondo = R.color.vk_activo_fondo;
            nombre = "Activo";
        }
        etiqueta.setText(nombre);
        etiqueta.setTextColor(ContextCompat.getColor(etiqueta.getContext(), texto));
        etiqueta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(etiqueta.getContext(), fondo)));
    }
}
