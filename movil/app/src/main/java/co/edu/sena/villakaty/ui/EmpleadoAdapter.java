package co.edu.sena.villakaty.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.edu.sena.villakaty.R;
import co.edu.sena.villakaty.modelo.Empleado;

/**
 * Adaptador del RecyclerView: dibuja cada empleado con el diseño item_empleado.xml (patrón ViewHolder).
 */
public class EmpleadoAdapter extends RecyclerView.Adapter<EmpleadoAdapter.Fila> {

    /** Acción al tocar un empleado de la lista. */
    public interface AlSeleccionar {
        void seleccionar(Empleado empleado);
    }

    private final List<Empleado> empleados = new ArrayList<>();
    private final AlSeleccionar alSeleccionar;

    public EmpleadoAdapter(AlSeleccionar alSeleccionar) {
        this.alSeleccionar = alSeleccionar;
    }

    public void mostrar(List<Empleado> nuevos) {
        empleados.clear();
        empleados.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext()).inflate(R.layout.item_empleado, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Empleado e = empleados.get(posicion);
        fila.iniciales.setText(e.getIniciales());
        fila.nombre.setText(e.getNombreCompleto());
        fila.cargo.setText(e.getNombreCargo() + " • " + e.getNombreArea());
        fila.documento.setText(e.getNombreTipoDocumento() + " " + e.getNumeroDocumento());
        EstadoVista.pintar(fila.estado, e.getEstado());
        fila.itemView.setOnClickListener(v -> alSeleccionar.seleccionar(e));
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

    static class Fila extends RecyclerView.ViewHolder {
        final TextView iniciales;
        final TextView nombre;
        final TextView cargo;
        final TextView documento;
        final TextView estado;

        Fila(View vista) {
            super(vista);
            iniciales = vista.findViewById(R.id.textoIniciales);
            nombre = vista.findViewById(R.id.textoNombre);
            cargo = vista.findViewById(R.id.textoCargo);
            documento = vista.findViewById(R.id.textoDocumento);
            estado = vista.findViewById(R.id.textoEstado);
        }
    }
}
