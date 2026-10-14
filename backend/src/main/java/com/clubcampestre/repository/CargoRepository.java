package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.Cargo;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {
}
