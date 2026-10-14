package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.TipoContactoDTO;
import com.clubcampestre.service.TipoContactoService;

@RestController
@RequestMapping("/api/tipos-contacto")
public class TipoContactoController {

    @Autowired
    private TipoContactoService tipoContactoService;

    @GetMapping
    public List<TipoContactoDTO> listar() {
        return tipoContactoService.listarTiposContacto();
    }
}
