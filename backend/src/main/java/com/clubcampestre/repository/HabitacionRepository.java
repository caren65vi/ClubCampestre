package com.clubcampestre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.roomsGest.Habitacion;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    List<Habitacion> findByActivoTrue();

    @Query("""
            SELECT h FROM Habitacion h
            WHERE h.activo = true
              AND (:numero IS NULL OR h.numeroHabitacion = :numero)
              AND (:idTipoHabitacion IS NULL OR h.tipoHabitacion.idTipoHabitacion = :idTipoHabitacion)
              AND (:estado IS NULL OR h.estado = :estado)
            """)
    List<Habitacion> buscarConFiltros(
            @Param("numero") Integer numero,
            @Param("idTipoHabitacion") Integer idTipoHabitacion,
            @Param("estado") String estado);
}
