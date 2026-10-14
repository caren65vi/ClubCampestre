package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clubcampestre.dto.TarifaDTO;
import com.clubcampestre.service.TarifaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    @Autowired
    private TarifaService tarifaService;

    @GetMapping
    public List<TarifaDTO> listar() {
        return tarifaService.listarTodas();
    }

    @GetMapping("/{id}")
    public TarifaDTO buscar(@PathVariable Integer id) {
        return tarifaService.buscarPorId(id);
    }

    @PostMapping
    public TarifaDTO crear(@Valid @RequestBody TarifaDTO dto) {
        return tarifaService.crear(dto);
    }
}
