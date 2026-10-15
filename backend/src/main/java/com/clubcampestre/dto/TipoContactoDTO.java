package com.clubcampestre.dto;

import lombok.Getter;
import lombok.Setter;

/** Datos de un tipo de contacto (celular, teléfono fijo, correo). */
@Getter
@Setter
public class TipoContactoDTO {

    private Integer idTipoContacto;
    private String nombreTipoContacto;
    private String formatoEsperado;
}
