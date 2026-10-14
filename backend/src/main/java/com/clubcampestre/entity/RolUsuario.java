package com.clubcampestre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rol")
@Getter
@Setter
public class RolUsuario {

    @Id
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", length = 50, nullable = false)
    private String nombreRol;

    @Column(name = "descripcion_rol", length = 100)
    private String descripcionRol;
}
