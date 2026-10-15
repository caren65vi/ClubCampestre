package com.clubcampestre.entity.roomsGest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Entidad JPA de la tabla caracteristica: comodidades de una habitación (TV, jacuzzi...). */
@Entity
@Table(name = "caracteristica")
@Getter
@Setter

public class Caracteristica {

    @Id
    @Column(name = "id_caracteristica")
    private Integer idCaracteristica;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion_caracteristica", length = 200)
    private String descripcionCaracteristica;

}
