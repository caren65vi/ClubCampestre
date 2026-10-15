package com.clubcampestre.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Asignación de una tarifa a una habitación desde una fecha de aplicación. */
@Getter
@Setter
public class TarifaHabitacionDTO {

    @NotNull(message = "El id de tarifa es obligatorio")
    private Integer idTarifa;

    @NotNull(message = "El número de habitación es obligatorio")
    private Integer numeroHabitacion;

    @NotNull(message = "La fecha de aplicación es obligatoria")
    private LocalDateTime fechaAplicacionTarifa;

    private BigDecimal valorTarifa;
    private String nombreTemporada;
}
