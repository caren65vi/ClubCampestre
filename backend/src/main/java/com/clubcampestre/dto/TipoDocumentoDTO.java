package com.clubcampestre.dto;

import lombok.Getter;
import lombok.Setter;

/** Datos de un tipo de documento de identidad (CC, CE, TI, PA, RC). */
@Getter
@Setter
public class TipoDocumentoDTO {

    private Integer idTipoDocumento;
    private String nombreTipoDocumento;
    private String descripcion;
}
