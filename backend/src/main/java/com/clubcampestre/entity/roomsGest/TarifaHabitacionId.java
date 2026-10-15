package com.clubcampestre.entity.roomsGest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Embeddable;

/** Llave primaria compuesta de TarifaHabitacion. */
@Embeddable
public class TarifaHabitacionId implements Serializable {

    private Integer idTarifa;
    private Integer numeroHabitacion;
    private LocalDateTime fechaAplicacionTarifa;

    public TarifaHabitacionId() {
    }

    public TarifaHabitacionId(Integer idTarifa, Integer numeroHabitacion, LocalDateTime fechaAplicacionTarifa) {
        this.idTarifa = idTarifa;
        this.numeroHabitacion = numeroHabitacion;
        this.fechaAplicacionTarifa = fechaAplicacionTarifa;
    }

    public Integer getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Integer idTarifa) {
        this.idTarifa = idTarifa;
    }

    public Integer getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(Integer numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public LocalDateTime getFechaAplicacionTarifa() {
        return fechaAplicacionTarifa;
    }

    public void setFechaAplicacionTarifa(LocalDateTime fechaAplicacionTarifa) {
        this.fechaAplicacionTarifa = fechaAplicacionTarifa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TarifaHabitacionId)) {
            return false;
        }
        TarifaHabitacionId that = (TarifaHabitacionId) o;
        return Objects.equals(idTarifa, that.idTarifa)
                && Objects.equals(numeroHabitacion, that.numeroHabitacion)
                && Objects.equals(fechaAplicacionTarifa, that.fechaAplicacionTarifa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTarifa, numeroHabitacion, fechaAplicacionTarifa);
    }
}
