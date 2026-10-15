package com.clubcampestre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Entidad JPA de la tabla tipo_contacto: catálogo de medios de contacto. */
@Entity
@Table(name = "tipo_contacto")
@Getter
@Setter

public class TipoContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_contacto")
    private Integer idTipoContacto;

    @Column(name = "nombre_tipo_contacto", length = 50, nullable = false)
    private String nombreTipoContacto;

    @Column(name = "formato_esperado", length = 80, nullable = false)
    private String formatoEsperado;

    @Column(name = "descripcion_tipo_contacto", length = 100)
    private String descripcion;
}
