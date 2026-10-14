package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.TipoDocumentoDTO;
import com.clubcampestre.service.TipoDocumentoService;

@RestController
@RequestMapping("/api/tipos-documento")
public class TipoDocumentoController {

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    @GetMapping
    public List<TipoDocumentoDTO> listar() {
        return tipoDocumentoService.listarTiposDocumento();
    }
}
