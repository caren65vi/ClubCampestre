package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.CargoDTO;
import com.clubcampestre.service.CargoService;

/** Controlador REST del catálogo de cargos (/api/cargos). */
@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    /** GET /api/cargos - Lista los cargos con su área. */
    @GetMapping
    public List<CargoDTO> listar() {
        return cargoService.listarCargos();
    }
}
