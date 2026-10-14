package com.clubcampestre.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistorialEmpleadoDTO {

    private Integer idHistorial;
    private String tipoEvento; // estado | edicion
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDate fechaEfectiva;
    private String motivo;
    private LocalDateTime fechaRegistro;

    private String usuarioResponsable; // nombre de usuario de quien hizo el cambio
    private String nombreResponsable;
    private String cargoResponsable;
}
