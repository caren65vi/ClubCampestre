package com.clubcampestre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clubcampestre.entity.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    // RF-0062: filtros opcionales por nombre, área y estado
    @Query("SELECT e FROM Empleado e WHERE "
            + "(:nombre IS NULL OR LOWER(e.primerNombre) LIKE LOWER(CONCAT('%', :nombre, '%')) "
            + "  OR LOWER(e.primerApellido) LIKE LOWER(CONCAT('%', :nombre, '%'))) "
            + "AND (:idArea IS NULL OR e.cargo.areaTrabajo.idAreaTrabajo = :idArea) "
            + "AND (:estado IS NULL OR e.estado = :estado)")
    List<Empleado> buscarConFiltros(@Param("nombre") String nombre,
            @Param("idArea") Integer idArea,
            @Param("estado") String estado);
}
