package com.clubcampestre.entity.roomsGest;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Entidad JPA que relaciona habitaciones con sus características (muchos a muchos). */
@Entity
@Table(name = "habitacion_caracteristica")
@Getter
@Setter
public class HabitacionCaracteristica {

    @EmbeddedId
    private HabitacionCaracteristicaId id = new HabitacionCaracteristicaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("numeroHabitacion")
    @JoinColumn(name = "numero_habitacion")
    private Habitacion habitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCaracteristica")
    @JoinColumn(name = "id_caracteristica")
    private Caracteristica caracteristica;

    @Column(name = "fecha_aplicacion")
    private LocalDateTime fechaAplicacion;
}
