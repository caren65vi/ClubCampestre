package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.AreaTrabajo;

@Repository
public interface AreaTrabajoRepository extends JpaRepository<AreaTrabajo, Integer> {
}
