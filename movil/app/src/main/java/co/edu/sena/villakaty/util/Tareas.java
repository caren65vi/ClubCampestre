package co.edu.sena.villakaty.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.edu.sena.villakaty.datos.ApiException;

/**
 * Ejecuta las llamadas a la API en un hilo de fondo y entrega el resultado en el hilo principal
 * (Android no permite usar la red en el hilo de la interfaz).
 */
public final class Tareas {

    private static final ExecutorService HILOS = Executors.newFixedThreadPool(2);
    private static final Handler HILO_PRINCIPAL = new Handler(Looper.getMainLooper());

    private Tareas() {
    }

    /** Trabajo que se ejecuta en segundo plano. */
    public interface Trabajo<T> {
        T ejecutar() throws ApiException;
    }

    /** Respuesta que se recibe en el hilo principal. */
    public interface Respuesta<T> {
        void exito(T resultado);

        void error(ApiException error);
    }

    public static <T> void ejecutar(Trabajo<T> trabajo, Respuesta<T> respuesta) {
        HILOS.execute(() -> {
            try {
                T resultado = trabajo.ejecutar();
                HILO_PRINCIPAL.post(() -> respuesta.exito(resultado));
            } catch (ApiException e) {
                HILO_PRINCIPAL.post(() -> respuesta.error(e));
            }
        });
    }
}
