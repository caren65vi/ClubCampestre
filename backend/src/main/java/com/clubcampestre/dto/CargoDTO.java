package com.clubcampestre.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargoDTO {

    private Integer idCargo;
    private String nombreCargo;
    private String descripcionCargo;
    private String nivelJerarquico;
    private BigDecimal salarioBase;
    private Integer idArea;
    private String nombreArea;
    private String descripcionArea;
}
