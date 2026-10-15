package com.clubcampestre.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Datos de un cargo con su área de trabajo, tal como se envían al frontend. */
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
