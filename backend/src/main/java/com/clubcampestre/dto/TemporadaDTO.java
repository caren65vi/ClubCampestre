package com.clubcampestre.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemporadaDTO {

    private Integer idTemporada; // solo lectura

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotBlank(message = "El tipo de temporada es obligatorio")
    private String tipoTemporada; 

    @NotBlank(message = "El nombre de la temporada es obligatorio")
    @Size(max = 80)
    private String nombreTemporada;

    @Size(max = 200)
    private String descripcion;
}
