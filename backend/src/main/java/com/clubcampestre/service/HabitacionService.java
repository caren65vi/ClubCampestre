package com.clubcampestre.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.HabitacionDTO;
import com.clubcampestre.entity.roomsGest.Caracteristica;
import com.clubcampestre.entity.roomsGest.Habitacion;
import com.clubcampestre.entity.roomsGest.HabitacionCaracteristica;
import com.clubcampestre.entity.roomsGest.HabitacionCaracteristicaId;
import com.clubcampestre.entity.roomsGest.TipoHabitacion;
import com.clubcampestre.exception.RecursoDuplicadoException;
import com.clubcampestre.exception.RecursoNoEncontradoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.CaracteristicaRepository;
import com.clubcampestre.repository.HabitacionRepository;
import com.clubcampestre.repository.TipoHabitacionRepository;


/** Lógica de negocio de habitaciones y sus características. */
@Service
public class HabitacionService {

    private static final Set<String> ESTADOS_VALIDOS
            = Set.of("disponible", "ocupada", "mantenimiento", "limpieza");

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    private HabitacionDTO convertirADTO(Habitacion h) {
        HabitacionDTO dto = new HabitacionDTO();
        dto.setNumeroHabitacion(h.getNumeroHabitacion());
        dto.setUbicacion(h.getUbicacion());
        dto.setDescripcion(h.getDescripcion());
        dto.setEstado(h.getEstado());
        dto.setIdTipoHabitacion(h.getTipoHabitacion().getIdTipoHabitacion());
        dto.setNombreTipo(h.getTipoHabitacion().getNombreTipo());
        dto.setCapacidad(h.getTipoHabitacion().getCapacidadEstandar());
        dto.setIdsCaracteristicas(
                h.getCaracteristicas().stream()
                        .map(hc -> hc.getCaracteristica().getIdCaracteristica())
                        .collect(Collectors.toList()));
        dto.setNombresCaracteristicas(
                h.getCaracteristicas().stream()
                        .map(hc -> hc.getCaracteristica().getNombre())
                        .collect(Collectors.toList()));
        return dto;
    }

    // Reemplaza por completo el set de características de una habitación
    private void asignarCaracteristicas(Habitacion h, List<Integer> idsCaracteristicas) {
        h.getCaracteristicas().clear(); // dispara orphanRemoval de las anteriores

        if (idsCaracteristicas == null || idsCaracteristicas.isEmpty()) {
            return;
        }

        Set<Integer> idsUnicos = new HashSet<>(idsCaracteristicas);
        List<Caracteristica> encontradas = caracteristicaRepository.findAllById(idsUnicos);
        if (encontradas.size() != idsUnicos.size()) {
            throw new SolicitudInvalidaException("Una o más características indicadas no existen");
        }

        for (Caracteristica c : encontradas) {
            HabitacionCaracteristica hc = new HabitacionCaracteristica();
            hc.setId(new HabitacionCaracteristicaId(h.getNumeroHabitacion(), c.getIdCaracteristica()));
            hc.setHabitacion(h);
            hc.setCaracteristica(c);
            hc.setFechaAplicacion(LocalDateTime.now());
            h.getCaracteristicas().add(hc);
        }
    }

    public List<HabitacionDTO> listarTodas() {
        return habitacionRepository.findByActivoTrue()
                .stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<HabitacionDTO> buscarConFiltros(Integer numero, Integer idTipoHabitacion, String estado) {
        if (estado != null && !ESTADOS_VALIDOS.contains(estado)) {
            throw new SolicitudInvalidaException("Estado de filtro inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
        if (idTipoHabitacion != null && !tipoHabitacionRepository.existsById(idTipoHabitacion)) {
            throw new SolicitudInvalidaException("El tipo de habitación indicado en el filtro no existe");
        }
        List<HabitacionDTO> resultado = habitacionRepository
                .buscarConFiltros(numero, idTipoHabitacion, estado)
                .stream().map(this::convertirADTO).collect(Collectors.toList());
        if (resultado.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay habitaciones que coincidan con los criterios de búsqueda");
        }
        return resultado;
    }

    public HabitacionDTO buscarPorId(Integer numero) {
        return convertirADTO(obtenerActivaOLanzarError(numero));
    }

    public HabitacionDTO crear(HabitacionDTO dto) {
        if (habitacionRepository.existsById(dto.getNumeroHabitacion())) {
            throw new RecursoDuplicadoException("Ya existe una habitación con el número " + dto.getNumeroHabitacion());
        }
        TipoHabitacion tipo = tipoHabitacionRepository.findById(dto.getIdTipoHabitacion())
                .orElseThrow(() -> new SolicitudInvalidaException("Tipo de habitación no válido"));

        Habitacion h = new Habitacion();
        h.setNumeroHabitacion(dto.getNumeroHabitacion());
        h.setUbicacion(dto.getUbicacion());
        h.setDescripcion(dto.getDescripcion());
        h.setTipoHabitacion(tipo);
        h.setEstado("disponible");
        h.setActivo(true);

        Habitacion guardada = habitacionRepository.save(h);
        asignarCaracteristicas(guardada, dto.getIdsCaracteristicas());
        return convertirADTO(habitacionRepository.save(guardada));
    }

    public HabitacionDTO actualizar(Integer numero, HabitacionDTO dto) {
        Habitacion h = obtenerActivaOLanzarError(numero);
        TipoHabitacion tipo = tipoHabitacionRepository.findById(dto.getIdTipoHabitacion())
                .orElseThrow(() -> new SolicitudInvalidaException("Tipo de habitación no válido"));

        h.setUbicacion(dto.getUbicacion());
        h.setDescripcion(dto.getDescripcion());
        h.setTipoHabitacion(tipo);
        asignarCaracteristicas(h, dto.getIdsCaracteristicas());

        return convertirADTO(habitacionRepository.save(h));
    }

    public HabitacionDTO cambiarEstado(Integer numero, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new SolicitudInvalidaException("Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
        Habitacion h = obtenerActivaOLanzarError(numero);
        h.setEstado(nuevoEstado);
        return convertirADTO(habitacionRepository.save(h));
    }

    public void eliminar(Integer numero) {
        Habitacion h = obtenerActivaOLanzarError(numero);
        if ("ocupada".equals(h.getEstado())) {
            throw new SolicitudInvalidaException("No se puede eliminar: la habitación tiene una estadía en curso");
        }
        h.setActivo(false);
        habitacionRepository.save(h);
    }

    private Habitacion obtenerActivaOLanzarError(Integer numero) {
        Habitacion h = habitacionRepository.findById(numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Habitación no encontrada"));
        if (!h.getActivo()) {
            throw new RecursoNoEncontradoException("Habitación no encontrada");
        }
        return h;
    }
}
