package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.RolUsuarioDTO;
import com.clubcampestre.service.RolUsuarioService;

/** Controlador REST del catálogo de roles de usuario (/api/roles). */
@RestController
@RequestMapping("/api/roles")
public class RolUsuarioController {

    @Autowired
    private RolUsuarioService rolUsuarioService;

    /** GET /api/roles - Lista los roles del sistema. */
    @GetMapping
    public List<RolUsuarioDTO> listar() {
        return rolUsuarioService.listarRoles();
    }
}
