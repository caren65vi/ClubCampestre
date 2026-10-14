package com.clubcampestre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubcampestre.dto.CargoDTO;
import com.clubcampestre.entity.Cargo;
import com.clubcampestre.repository.CargoRepository;

@Service
public class CargoService {

    @Autowired
    private CargoRepository cargoRepository;

    private CargoDTO convertirADTO(Cargo c) {
        CargoDTO dto = new CargoDTO();
        dto.setIdCargo(c.getIdCargo());
        dto.setNombreCargo(c.getNombreCargo());
        dto.setDescripcionCargo(c.getDescripcionCargo());
        dto.setNivelJerarquico(c.getNivelJerarquico());
        dto.setSalarioBase(c.getSalarioBase());
        dto.setIdArea(c.getAreaTrabajo().getIdAreaTrabajo());
        dto.setNombreArea(c.getAreaTrabajo().getNombreAreaTrabajo());
        dto.setDescripcionArea(c.getAreaTrabajo().getDescripcion());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CargoDTO> listarCargos() {
        return cargoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}
