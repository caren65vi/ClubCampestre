package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
}