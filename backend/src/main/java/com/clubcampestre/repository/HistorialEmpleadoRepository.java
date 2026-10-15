package com.clubcampestre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.HistorialEmpleado;

/** Repositorio JPA de la bitácora de empleados. */
@Repository
public interface HistorialEmpleadoRepository extends JpaRepository<HistorialEmpleado, Integer> {

    List<HistorialEmpleado> findByEmpleado_IdEmpleadoOrderByFechaRegistroDescIdHistorialDesc(String idEmpleado);
}
