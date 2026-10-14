package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.Temporada;

@Repository
public interface TemporadaRepository extends JpaRepository<Temporada, Integer> {
}
