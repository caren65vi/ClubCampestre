package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clubcampestre.dto.TemporadaDTO;
import com.clubcampestre.service.TemporadaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/temporadas")
public class TemporadaController {

    @Autowired
    private TemporadaService temporadaService;

    @GetMapping
    public List<TemporadaDTO> listar() {
        return temporadaService.listarTodas();
    }

    @GetMapping("/{id}")
    public TemporadaDTO buscar(@PathVariable Integer id) {
        return temporadaService.buscarPorId(id);
    }

    @PostMapping
    public TemporadaDTO crear(@Valid @RequestBody TemporadaDTO dto) {
        return temporadaService.crear(dto);
    }

    @PutMapping("/{id}")
    public TemporadaDTO actualizar(@PathVariable Integer id, @Valid @RequestBody TemporadaDTO dto) {
        return temporadaService.actualizar(id, dto);
    }
}
