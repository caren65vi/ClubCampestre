package com.clubcampestre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.TipoContactoDTO;
import com.clubcampestre.entity.TipoContacto;
import com.clubcampestre.repository.TipoContactoRepository;

@Service
public class TipoContactoService {

    @Autowired
    private TipoContactoRepository tipoContactoRepository;

    private TipoContactoDTO convertirADTO(TipoContacto t) {
        TipoContactoDTO dto = new TipoContactoDTO();
        dto.setIdTipoContacto(t.getIdTipoContacto());
        dto.setNombreTipoContacto(t.getNombreTipoContacto());
        dto.setFormatoEsperado(t.getFormatoEsperado());
        return dto;
    }

    public List<TipoContactoDTO> listarTiposContacto() {
        return tipoContactoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
