package com.clubcampestre.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Datos de una tarifa de hospedaje. */
@Getter
@Setter
public class TarifaDTO {

    private Integer idTarifa; // solo lectura

    @NotNull(message = "El valor de la tarifa es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor de la tarifa debe ser mayor a 0")
    private BigDecimal valor;

    @NotNull(message = "La fecha de vigencia es obligatoria")
    private LocalDate fechaVigencia;

    @NotNull(message = "La temporada es obligatoria")
    private Integer idTemporada;

    private String nombreTemporada; // solo lectura
}
