package com.clubcampestre.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Entidad JPA de la tabla temporada: periodos con fecha de inicio y fin. */
@Entity
@Table(name = "temporada")
@Getter
@Setter
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_temporada")
    private Integer idTemporada;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "tipo_temporada", length = 20, nullable = false)
    private String tipoTemporada;

    @Column(name = "nombre_temporada", length = 80, nullable = false)
    private String nombreTemporada;

    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
