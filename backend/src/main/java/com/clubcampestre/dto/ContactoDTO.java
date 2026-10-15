package com.clubcampestre.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Contacto adicional de un empleado (teléfono o correo de emergencia). */
@Getter
@Setter
public class ContactoDTO {

    @NotNull(message = "El tipo de contacto es obligatorio")
    private Integer idTipoContacto;
    private String nombreTipoContacto; // solo lectura

    @Size(max = 20)
    private String indicativo;

    @NotBlank(message = "El valor del contacto es obligatorio")
    @Size(max = 150)
    private String valor;
}
