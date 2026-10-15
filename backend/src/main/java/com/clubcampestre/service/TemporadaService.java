package com.clubcampestre.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.TemporadaDTO;
import com.clubcampestre.entity.Temporada;
import com.clubcampestre.exception.RecursoNoEncontradoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.TemporadaRepository;

/** Lógica de negocio de temporadas (valida que la fecha fin no sea anterior a la de inicio). */
@Service
public class TemporadaService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("Baja", "Media", "Alta", "Premium");

    @Autowired
    private TemporadaRepository temporadaRepository;

    private TemporadaDTO convertirADTO(Temporada t) {
        TemporadaDTO dto = new TemporadaDTO();
        dto.setIdTemporada(t.getIdTemporada());
        dto.setFechaInicio(t.getFechaInicio());
        dto.setFechaFin(t.getFechaFin());
        dto.setTipoTemporada(t.getTipoTemporada());
        dto.setNombreTemporada(t.getNombreTemporada());
        dto.setDescripcion(t.getDescripcion());
        return dto;
    }

    private void validar(TemporadaDTO dto) {
        if (!TIPOS_VALIDOS.contains(dto.getTipoTemporada())) {
            throw new SolicitudInvalidaException("Tipo de temporada inválido. Valores permitidos: " + TIPOS_VALIDOS);
        }
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new SolicitudInvalidaException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    public List<TemporadaDTO> listarTodas() {
        return temporadaRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TemporadaDTO buscarPorId(Integer id) {
        return convertirADTO(obtenerOLanzarError(id));
    }

    public TemporadaDTO crear(TemporadaDTO dto) {
        validar(dto);
        Temporada t = new Temporada();
        t.setFechaInicio(dto.getFechaInicio());
        t.setFechaFin(dto.getFechaFin());
        t.setTipoTemporada(dto.getTipoTemporada());
        t.setNombreTemporada(dto.getNombreTemporada());
        t.setDescripcion(dto.getDescripcion());
        return convertirADTO(temporadaRepository.save(t));
    }

    public TemporadaDTO actualizar(Integer id, TemporadaDTO dto) {
        validar(dto);
        Temporada t = obtenerOLanzarError(id);
        t.setFechaInicio(dto.getFechaInicio());
        t.setFechaFin(dto.getFechaFin());
        t.setTipoTemporada(dto.getTipoTemporada());
        t.setNombreTemporada(dto.getNombreTemporada());
        t.setDescripcion(dto.getDescripcion());
        return convertirADTO(temporadaRepository.save(t));
    }

    private Temporada obtenerOLanzarError(Integer id) {
        return temporadaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Temporada no encontrada"));
    }
}
