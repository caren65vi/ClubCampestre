package com.clubcampestre.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clubcampestre.entity.UsuarioTrabajador;

/** Repositorio JPA de usuarios del sistema. */
public interface UsuarioTrabajadorRepository extends JpaRepository<UsuarioTrabajador, String> {

    Optional<UsuarioTrabajador> findByNombreUsuario(String nombreUsuario);
}
