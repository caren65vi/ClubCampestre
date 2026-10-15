package com.clubcampestre.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.Contacto;

/** Repositorio JPA de contactos de empleados y clientes. */
@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {

    Optional<Contacto> findByEmpleado_IdEmpleadoAndTipoContacto_IdTipoContacto(
            String idEmpleado, Integer idTipoContacto);
}
