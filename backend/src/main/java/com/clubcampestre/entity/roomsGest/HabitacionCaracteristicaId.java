package com.clubcampestre.entity.roomsGest;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class HabitacionCaracteristicaId implements Serializable {

    private Integer numeroHabitacion;
    private Integer idCaracteristica;

    public HabitacionCaracteristicaId() {
    }

    public HabitacionCaracteristicaId(Integer numeroHabitacion, Integer idCaracteristica) {
        this.numeroHabitacion = numeroHabitacion;
        this.idCaracteristica = idCaracteristica;
    }

    public Integer getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(Integer numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public Integer getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(Integer idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HabitacionCaracteristicaId)) {
            return false;
        }
        HabitacionCaracteristicaId that = (HabitacionCaracteristicaId) o;
        return Objects.equals(numeroHabitacion, that.numeroHabitacion)
                && Objects.equals(idCaracteristica, that.idCaracteristica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroHabitacion, idCaracteristica);
    }
}
