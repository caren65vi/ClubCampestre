package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.roomsGest.TipoHabitacion;

/** Repositorio JPA de tipos de habitación. */
@Repository
public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {
}
