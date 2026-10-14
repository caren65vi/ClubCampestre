package com.clubcampestre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.TarifaHabitacionDTO;
import com.clubcampestre.entity.Tarifa;
import com.clubcampestre.entity.roomsGest.Habitacion;
import com.clubcampestre.entity.roomsGest.TarifaHabitacion;
import com.clubcampestre.entity.roomsGest.TarifaHabitacionId;
import com.clubcampestre.exception.RecursoDuplicadoException;
import com.clubcampestre.exception.RecursoNoEncontradoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.HabitacionRepository;
import com.clubcampestre.repository.TarifaHabitacionRepository;
import com.clubcampestre.repository.TarifaRepository;

@Service
public class TarifaHabitacionService {

    @Autowired
    private TarifaHabitacionRepository tarifaHabitacionRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    private TarifaHabitacionDTO convertirADTO(TarifaHabitacion th) {
        TarifaHabitacionDTO dto = new TarifaHabitacionDTO();
        dto.setIdTarifa(th.getId().getIdTarifa());
        dto.setNumeroHabitacion(th.getId().getNumeroHabitacion());
        dto.setFechaAplicacionTarifa(th.getId().getFechaAplicacionTarifa());
        dto.setValorTarifa(th.getTarifa().getValor());
        dto.setNombreTemporada(th.getTarifa().getTemporada().getNombreTemporada());
        return dto;
    }

    // Asigna una tarifa a una habitación desde una fecha/hora de aplicación
    public TarifaHabitacionDTO asignar(TarifaHabitacionDTO dto) {
        Tarifa tarifa = tarifaRepository.findById(dto.getIdTarifa())
                .orElseThrow(() -> new SolicitudInvalidaException("Tarifa no válida"));
        Habitacion habitacion = habitacionRepository.findById(dto.getNumeroHabitacion())
            .filter(habitacionEncontrada -> Boolean.TRUE.equals(habitacionEncontrada.getActivo()))
                .orElseThrow(() -> new SolicitudInvalidaException("Habitación no válida"));

        TarifaHabitacionId id = new TarifaHabitacionId(
                dto.getIdTarifa(), dto.getNumeroHabitacion(), dto.getFechaAplicacionTarifa());

        if (tarifaHabitacionRepository.existsById(id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe esa tarifa asignada a esa habitación con esa misma fecha de aplicación");
        }

        TarifaHabitacion th = new TarifaHabitacion();
        th.setId(id);
        th.setTarifa(tarifa);
        th.setHabitacion(habitacion);

        return convertirADTO(tarifaHabitacionRepository.save(th));
    }

    // Historial completo de tarifas de una habitación
    public List<TarifaHabitacionDTO> historial(Integer numeroHabitacion) {
        List<TarifaHabitacionDTO> resultado = tarifaHabitacionRepository
                .historialPorHabitacion(numeroHabitacion)
                .stream().map(this::convertirADTO).collect(Collectors.toList());
        if (resultado.isEmpty()) {
            throw new RecursoNoEncontradoException("La habitación no tiene tarifas asignadas");
        }
        return resultado;
    }

    // Tarifa vigente en este momento para una habitación
    public TarifaHabitacionDTO vigente(Integer numeroHabitacion) {
        return tarifaHabitacionRepository.tarifaVigente(numeroHabitacion)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                "La habitación no tiene ninguna tarifa vigente"));
    }
}
