package com.clubcampestre.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

/** Entidad JPA de la tabla historial_empleado: bitácora de auditoría de cada empleado. */
@Entity
@Table(name = "historial_empleado")
@Getter
@Setter

public class HistorialEmpleado {

    public static final String TIPO_ESTADO = "estado";
    public static final String TIPO_EDICION = "edicion";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_documento_empleado", referencedColumnName = "numero_documento", nullable = false)
    private Empleado empleado;

    @Column(name = "tipo_evento", length = 10, nullable = false)
    private String tipoEvento;

    @Column(name = "estado_anterior", length = 10)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 10, nullable = false)
    private String estadoNuevo;

    @Column(name = "fecha_efectiva", nullable = false)
    private LocalDate fechaEfectiva;

    @Column(name = "motivo", length = 300, nullable = false)
    private String motivo;

    @Column(name = "usuario_responsable", length = 50)
    private String usuarioResponsable;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
