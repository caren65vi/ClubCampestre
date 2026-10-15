package com.clubcampestre.service;

import com.clubcampestre.dto.LoginRequest;
import com.clubcampestre.dto.LoginResponse;

/** Contrato del servicio de autenticación de colaboradores. */
public interface AuthService {

    /** Valida las credenciales y devuelve el token de acceso. */
    LoginResponse login(LoginRequest request);
}
