package com.clubcampestre.dto;

import lombok.Getter;
import lombok.Setter;

/** Datos de un rol del sistema. */
@Getter
@Setter
public class RolUsuarioDTO {

    private Integer idRol;
    private String nombreRol;
    private String descripcionRol;
}
