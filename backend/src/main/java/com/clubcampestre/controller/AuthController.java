package com.clubcampestre.controller;

import com.clubcampestre.dto.LoginRequest;
import com.clubcampestre.dto.LoginResponse;
import com.clubcampestre.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controlador REST de autenticación de colaboradores (/api/auth). */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** POST /api/auth/login - Inicia sesión y devuelve el token JWT (401 si las credenciales no son válidas). */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
