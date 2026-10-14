package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.roomsGest.Caracteristica;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {
}
