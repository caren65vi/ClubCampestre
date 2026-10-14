package com.clubcampestre.entity.roomsGest;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "habitacion")
@Getter
@Setter
public class Habitacion {

    @Id
    @Column(name = "numero_habitacion")
    private Integer numeroHabitacion;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "disponible";

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_habitacion", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitacionCaracteristica> caracteristicas = new ArrayList<>();
}
