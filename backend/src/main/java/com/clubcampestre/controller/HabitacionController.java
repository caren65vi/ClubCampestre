package com.clubcampestre.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.HabitacionDTO;
import com.clubcampestre.service.HabitacionService;

/** Controlador REST de habitaciones del club (/api/habitaciones). */
@RestController
@RequestMapping("/api/habitaciones")

public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    /** GET /api/habitaciones - Lista las habitaciones. */
    @GetMapping
    public List<HabitacionDTO> listar() {
        return habitacionService.listarTodas();
    }

    /** GET /api/habitaciones/{numero} - Consulta una habitación. */
    @GetMapping("/{numero}")
    public HabitacionDTO buscar(@PathVariable Integer numero) {
        return habitacionService.buscarPorId(numero);
    }

    /** POST /api/habitaciones - Crea una habitación. */
    @PostMapping
    public HabitacionDTO crear(@RequestBody HabitacionDTO dto) {
        return habitacionService.crear(dto);
    }

    /** PUT /api/habitaciones/{numero} - Actualiza una habitación. */
    @PutMapping("/{numero}")
    public HabitacionDTO actualizar(@PathVariable Integer numero, @RequestBody HabitacionDTO dto) {
        return habitacionService.actualizar(numero, dto);
    }

    /** PATCH /api/habitaciones/{numero}/estado - Cambia el estado de la habitación. */
    @PatchMapping("/{numero}/estado")
    public HabitacionDTO cambiarEstado(@PathVariable Integer numero, @RequestBody Map<String, String> body) {
        return habitacionService.cambiarEstado(numero, body.get("estado"));
    }

    /** DELETE /api/habitaciones/{numero} - Elimina (desactiva) una habitación. */
    @DeleteMapping("/{numero}")
    public void eliminar(@PathVariable Integer numero) {
        habitacionService.eliminar(numero);
    }
}
