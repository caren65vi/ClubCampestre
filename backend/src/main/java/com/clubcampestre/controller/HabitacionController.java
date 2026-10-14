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

@RestController
@RequestMapping("/api/habitaciones")

public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    @GetMapping
    public List<HabitacionDTO> listar() {
        return habitacionService.listarTodas();
    }

    @GetMapping("/{numero}")
    public HabitacionDTO buscar(@PathVariable Integer numero) {
        return habitacionService.buscarPorId(numero);
    }

    @PostMapping
    public HabitacionDTO crear(@RequestBody HabitacionDTO dto) {
        return habitacionService.crear(dto);
    }

    @PutMapping("/{numero}")
    public HabitacionDTO actualizar(@PathVariable Integer numero, @RequestBody HabitacionDTO dto) {
        return habitacionService.actualizar(numero, dto);
    }

    @PatchMapping("/{numero}/estado")
    public HabitacionDTO cambiarEstado(@PathVariable Integer numero, @RequestBody Map<String, String> body) {
        return habitacionService.cambiarEstado(numero, body.get("estado"));
    }

    @DeleteMapping("/{numero}")
    public void eliminar(@PathVariable Integer numero) {
        habitacionService.eliminar(numero);
    }
}
