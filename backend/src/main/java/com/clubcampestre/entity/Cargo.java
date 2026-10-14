package com.clubcampestre.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cargo")
@Getter
@Setter
public class Cargo {

    @Id
    @Column(name = "id_cargo")
    private Integer idCargo;

    @Column(name = "nombre_cargo", length = 50, nullable = false)
    private String nombreCargo;

    @Column(name = "descripcion_cargo", length = 100)
    private String descripcionCargo;

    @Column(name = "salario_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal salarioBase = BigDecimal.ZERO;

    @Column(name = "nivel_jerarquico", length = 50, nullable = false)
    private String nivelJerarquico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area", nullable = false)
    private AreaTrabajo areaTrabajo;
}