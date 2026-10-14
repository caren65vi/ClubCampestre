package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clubcampestre.dto.TarifaHabitacionDTO;
import com.clubcampestre.service.TarifaHabitacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarifas-habitacion")
public class TarifaHabitacionController {

    @Autowired
    private TarifaHabitacionService tarifaHabitacionService;

    @PostMapping
    public TarifaHabitacionDTO asignar(@Valid @RequestBody TarifaHabitacionDTO dto) {
        return tarifaHabitacionService.asignar(dto);
    }

    @GetMapping("/habitacion/{numeroHabitacion}/historial")
    public List<TarifaHabitacionDTO> historial(@PathVariable Integer numeroHabitacion) {
        return tarifaHabitacionService.historial(numeroHabitacion);
    }

    @GetMapping("/habitacion/{numeroHabitacion}/vigente")
    public TarifaHabitacionDTO vigente(@PathVariable Integer numeroHabitacion) {
        return tarifaHabitacionService.vigente(numeroHabitacion);
    }
}
