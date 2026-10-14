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

@Entity

@Table(name = "contacto")
@Getter
@Setter

public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    private Integer idContacto;

    @Column(name = "valor", length = 150, nullable = false)
    private String valor;

    @Column(name = "indicativo", length = 20)
    private String indicativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_contacto", nullable = false)
    private TipoContacto tipoContacto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_documento_empleado", referencedColumnName = "numero_documento")
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_documento_cliente", referencedColumnName = "numero_documento")
    private Cliente cliente;

}
