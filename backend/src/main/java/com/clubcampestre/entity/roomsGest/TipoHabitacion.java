package com.clubcampestre.entity.roomsGest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tipo_habitacion")
@Getter
@Setter
public class TipoHabitacion {

    @Id
    @Column(name = "id_tipo_habitacion")
    private Integer idTipoHabitacion;

    @Column(name = "nombre_tipo", length = 50, nullable = false)
    private String nombreTipo;

    @Column(name = "descripcion_tipo")
    private String descripcionTipo;

    @Column(name = "capacidad_estandar")
    private Integer capacidadEstandar;
}