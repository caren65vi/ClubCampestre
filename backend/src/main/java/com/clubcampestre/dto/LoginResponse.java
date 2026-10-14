package com.clubcampestre.dto;

public class LoginResponse {
    private Integer idUsuario;
    private String nombreUsuario;
    private String rol;
    private String token;

    public LoginResponse(Integer idUsuario, String nombreUsuario, String rol, String token) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.token = token;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getRol() { return rol; }
    public String getToken() { return token; }
}