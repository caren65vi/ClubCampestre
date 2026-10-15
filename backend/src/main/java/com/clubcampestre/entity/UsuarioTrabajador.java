package com.clubcampestre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Entidad JPA de la tabla usuario: cuenta de acceso de un empleado (contraseña en BCrypt). */
@Entity
@Table(name = "usuario")
@Getter
@Setter

public class UsuarioTrabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario", length = 50, nullable = false)
    private String nombreUsuario;

    @Column(name = "contrasena_hash", length = 100, nullable = false)
    private String contrasena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolUsuario rolUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_documento_empleado", referencedColumnName = "numero_documento", nullable = false)
    private Empleado empleado;
}
