package com.clubcampestre.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioEstadoDTO {

    @NotBlank(message = "El nuevo estado es obligatorio")
    private String estado;

    @NotNull(message = "La fecha efectiva es obligatoria")
    private LocalDate fechaEfectiva;

    @NotBlank(message = "El motivo o acta es obligatorio")
    @Size(max = 300, message = "El motivo no puede superar 300 caracteres")
    private String motivo;
}
