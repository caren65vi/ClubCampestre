package com.clubcampestre.entity.roomsGest;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import com.clubcampestre.entity.Tarifa;

/** Entidad JPA que guarda qué tarifa tiene una habitación desde cierta fecha. */
@Entity
@Table(name = "tarifa_habitacion")
@Getter
@Setter
public class TarifaHabitacion {

    @EmbeddedId
    private TarifaHabitacionId id = new TarifaHabitacionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idTarifa")
    @JoinColumn(name = "id_tarifa")
    private Tarifa tarifa;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("numeroHabitacion")
    @JoinColumn(name = "numero_habitacion")
    private Habitacion habitacion;
}
