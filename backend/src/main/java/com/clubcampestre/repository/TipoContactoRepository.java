package com.clubcampestre.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.TipoContacto;

@Repository
public interface TipoContactoRepository extends JpaRepository<TipoContacto, Integer> {

    Optional<TipoContacto> findByNombreTipoContactoIgnoreCase(String nombre);
}
