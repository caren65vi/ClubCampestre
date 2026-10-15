package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clubcampestre.dto.TemporadaDTO;
import com.clubcampestre.service.TemporadaService;

import jakarta.validation.Valid;

/** Controlador REST de temporadas (alta, media, baja, premium) (/api/temporadas). */
@RestController
@RequestMapping("/api/temporadas")
public class TemporadaController {

    @Autowired
    private TemporadaService temporadaService;

    /** GET /api/temporadas - Lista las temporadas. */
    @GetMapping
    public List<TemporadaDTO> listar() {
        return temporadaService.listarTodas();
    }

    /** GET /api/temporadas/{id} - Consulta una temporada. */
    @GetMapping("/{id}")
    public TemporadaDTO buscar(@PathVariable Integer id) {
        return temporadaService.buscarPorId(id);
    }

    /** POST /api/temporadas - Crea una temporada. */
    @PostMapping
    public TemporadaDTO crear(@Valid @RequestBody TemporadaDTO dto) {
        return temporadaService.crear(dto);
    }

    /** PUT /api/temporadas/{id} - Actualiza una temporada. */
    @PutMapping("/{id}")
    public TemporadaDTO actualizar(@PathVariable Integer id, @Valid @RequestBody TemporadaDTO dto) {
        return temporadaService.actualizar(id, dto);
    }
}
