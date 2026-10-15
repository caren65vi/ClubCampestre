package com.clubcampestre.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Datos de una habitación y sus características. */
@Getter
@Setter
public class HabitacionDTO {

    @NotNull(message = "El número de habitación es obligatorio")
    @Min(value = 1, message = "El número de habitación debe ser mayor a 0")
    private Integer numeroHabitacion;

    @Size(max = 100, message = "La ubicación no debe superar 100 caracteres")
    private String ubicacion;

    private String descripcion;

    private String estado; // solo lectura

    @NotNull(message = "El tipo de habitación es obligatorio")
    private Integer idTipoHabitacion;

    private Integer capacidad;   
    private String nombreTipo;   

    private List<Integer> idsCaracteristicas;     
    private List<String> nombresCaracteristicas;  
}
