package com.clubcampestre.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoDTO {

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20)
    private String numeroDocumento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer idTipoDocumento;
    private String nombreTipoDocumento; 

    @NotBlank(message = "El primer nombre es obligatorio")
    private String primerNombre;
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    private String primerApellido;
    private String segundoApellido;

    @Size(max = 20)
    private String telefono; // celular

    @Size(max = 20)
    private String indicativo;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 150)
    private String correo;

    @Valid
    private List<ContactoDTO> contactosAdicionales;

    // Credenciales de acceso: solo se usan al registrar si crearUsuario es true
    private Boolean crearUsuario;
    private Integer idRol;

    @Size(max = 50)
    private String nombreUsuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    @NotNull(message = "El cargo es obligatorio")
    private Integer idCargo;
    private String nombreCargo;   
    private String nombreArea;    
    private Integer idArea;       

    @NotNull(message = "La fecha de contratación es obligatoria")
    private LocalDate fechaContratacion;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El salario debe ser mayor a 0")
    private BigDecimal salario;

    private String estado;             
    private String fechaCambioEstado; 
}
