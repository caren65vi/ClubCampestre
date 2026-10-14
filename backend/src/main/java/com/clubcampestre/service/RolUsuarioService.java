package com.clubcampestre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clubcampestre.dto.RolUsuarioDTO;
import com.clubcampestre.entity.RolUsuario;
import com.clubcampestre.repository.RolUsuarioRepository;

@Service
public class RolUsuarioService {

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    private RolUsuarioDTO convertirADTO(RolUsuario r) {
        RolUsuarioDTO dto = new RolUsuarioDTO();
        dto.setIdRol(r.getIdRol());
        dto.setNombreRol(r.getNombreRol());
        dto.setDescripcionRol(r.getDescripcionRol());
        return dto;
    }

    public List<RolUsuarioDTO> listarRoles() {
        return rolUsuarioRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
