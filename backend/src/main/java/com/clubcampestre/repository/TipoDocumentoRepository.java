package com.clubcampestre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.TipoDocumento;

/** Repositorio JPA de tipos de documento. */
@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Integer> {
}
