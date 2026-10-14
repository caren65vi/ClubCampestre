package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.RolUsuario;

@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Integer> {
}
