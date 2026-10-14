package com.clubcampestre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "area_trabajo")
@Getter
@Setter

public class AreaTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area")
    private Integer idAreaTrabajo;

    @Column(name = "nombre_area", length = 50, nullable = false)
    private String nombreAreaTrabajo;

    @Column(name = "descripcion_area", length = 200)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lider", referencedColumnName = "numero_documento", nullable = false)
    private Empleado lider;

}
