package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.TipoContactoDTO;
import com.clubcampestre.service.TipoContactoService;

/** Controlador REST del catálogo de tipos de contacto (/api/tipos-contacto). */
@RestController
@RequestMapping("/api/tipos-contacto")
public class TipoContactoController {

    @Autowired
    private TipoContactoService tipoContactoService;

    /** GET /api/tipos-contacto - Lista los tipos de contacto. */
    @GetMapping
    public List<TipoContactoDTO> listar() {
        return tipoContactoService.listarTiposContacto();
    }
}
