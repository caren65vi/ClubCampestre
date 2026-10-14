package com.clubcampestre.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.roomsGest.TarifaHabitacion;
import com.clubcampestre.entity.roomsGest.TarifaHabitacionId;

@Repository
public interface TarifaHabitacionRepository extends JpaRepository<TarifaHabitacion, TarifaHabitacionId> {

    @Query("SELECT th FROM TarifaHabitacion th "
            + "WHERE th.id.numeroHabitacion = :numeroHabitacion "
            + "ORDER BY th.id.fechaAplicacionTarifa DESC")
    List<TarifaHabitacion> historialPorHabitacion(@Param("numeroHabitacion") Integer numeroHabitacion);

    
    @Query("SELECT th FROM TarifaHabitacion th "
            + "WHERE th.id.numeroHabitacion = :numeroHabitacion "
            + "AND th.id.fechaAplicacionTarifa <= :ahora "
            + "ORDER BY th.id.fechaAplicacionTarifa DESC")
    List<TarifaHabitacion> vigentesHasta(@Param("numeroHabitacion") Integer numeroHabitacion,
            @Param("ahora") LocalDateTime ahora);

    default Optional<TarifaHabitacion> tarifaVigente(Integer numeroHabitacion) {
        List<TarifaHabitacion> resultados = vigentesHasta(numeroHabitacion, LocalDateTime.now());
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }
}
