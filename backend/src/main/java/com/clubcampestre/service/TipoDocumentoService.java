package com.clubcampestre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.TipoDocumentoDTO;
import com.clubcampestre.entity.TipoDocumento;
import com.clubcampestre.repository.TipoDocumentoRepository;

/** Lógica de negocio del catálogo de tipos de documento. */
@Service
public class TipoDocumentoService {

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    private TipoDocumentoDTO convertirADTO(TipoDocumento t) {
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.setIdTipoDocumento(t.getIdTipoDocumento());
        dto.setNombreTipoDocumento(t.getNombreTipoDocumento());
        dto.setDescripcion(t.getDescripcion());
        return dto;
    }

    public List<TipoDocumentoDTO> listarTiposDocumento() {
        return tipoDocumentoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
