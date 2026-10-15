package com.clubcampestre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.TarifaDTO;
import com.clubcampestre.entity.Tarifa;
import com.clubcampestre.entity.Temporada;
import com.clubcampestre.exception.RecursoNoEncontradoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.TarifaRepository;
import com.clubcampestre.repository.TemporadaRepository;

/** Lógica de negocio de tarifas. */
@Service
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private TemporadaRepository temporadaRepository;

    private TarifaDTO convertirADTO(Tarifa t) {
        TarifaDTO dto = new TarifaDTO();
        dto.setIdTarifa(t.getIdTarifa());
        dto.setValor(t.getValor());
        dto.setFechaVigencia(t.getFechaVigencia());
        dto.setIdTemporada(t.getTemporada().getIdTemporada());
        dto.setNombreTemporada(t.getTemporada().getNombreTemporada());
        return dto;
    }

    public List<TarifaDTO> listarTodas() {
        return tarifaRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public TarifaDTO buscarPorId(Integer id) {
        return convertirADTO(obtenerOLanzarError(id));
    }

    public TarifaDTO crear(TarifaDTO dto) {
        Temporada temporada = temporadaRepository.findById(dto.getIdTemporada())
                .orElseThrow(() -> new SolicitudInvalidaException("Temporada no válida"));

        Tarifa t = new Tarifa();
        t.setValor(dto.getValor());
        t.setFechaVigencia(dto.getFechaVigencia());
        t.setTemporada(temporada);

        return convertirADTO(tarifaRepository.save(t));
    }

    private Tarifa obtenerOLanzarError(Integer id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarifa no encontrada"));
    }
}
