package co.edu.sena.villakaty.modelo;

/**
 * Elemento de un catálogo (tipo de documento o cargo) para mostrar en las listas desplegables.
 */
public class Catalogo {

    private final int id;
    private final String nombre;

    public Catalogo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /** El Spinner muestra el resultado de toString(). */
    @Override
    public String toString() {
        return nombre;
    }
}
