package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clubcampestre.dto.TarifaHabitacionDTO;
import com.clubcampestre.service.TarifaHabitacionService;

import jakarta.validation.Valid;

/** Controlador REST que asigna tarifas a habitaciones (/api/tarifas-habitacion). */
@RestController
@RequestMapping("/api/tarifas-habitacion")
public class TarifaHabitacionController {

    @Autowired
    private TarifaHabitacionService tarifaHabitacionService;

    /** POST /api/tarifas-habitacion - Asigna una tarifa a una habitación. */
    @PostMapping
    public TarifaHabitacionDTO asignar(@Valid @RequestBody TarifaHabitacionDTO dto) {
        return tarifaHabitacionService.asignar(dto);
    }

    /** GET /api/tarifas-habitacion/habitacion/{numero}/historial - Historial de tarifas de la habitación. */
    @GetMapping("/habitacion/{numeroHabitacion}/historial")
    public List<TarifaHabitacionDTO> historial(@PathVariable Integer numeroHabitacion) {
        return tarifaHabitacionService.historial(numeroHabitacion);
    }

    /** GET /api/tarifas-habitacion/habitacion/{numero}/vigente - Tarifa vigente de la habitación. */
    @GetMapping("/habitacion/{numeroHabitacion}/vigente")
    public TarifaHabitacionDTO vigente(@PathVariable Integer numeroHabitacion) {
        return tarifaHabitacionService.vigente(numeroHabitacion);
    }
}
