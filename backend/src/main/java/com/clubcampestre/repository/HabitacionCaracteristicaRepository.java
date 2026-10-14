package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.roomsGest.HabitacionCaracteristica;
import com.clubcampestre.entity.roomsGest.HabitacionCaracteristicaId;

@Repository
public interface HabitacionCaracteristicaRepository
        extends JpaRepository<HabitacionCaracteristica, HabitacionCaracteristicaId> {
}