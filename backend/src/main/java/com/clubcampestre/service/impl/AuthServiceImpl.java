package com.clubcampestre.service.impl;

import com.clubcampestre.dto.LoginRequest;
import com.clubcampestre.dto.LoginResponse;
import com.clubcampestre.entity.UsuarioTrabajador;
import com.clubcampestre.exception.CredencialesInvalidasException;
import com.clubcampestre.repository.UsuarioTrabajadorRepository;
import com.clubcampestre.security.JwtUtil;
import com.clubcampestre.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioTrabajadorRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<UsuarioTrabajador> usuarioOpt
                = usuarioRepository.findByNombreUsuario(request.getNombreUsuario());

        UsuarioTrabajador usuario = usuarioOpt
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        }

        String rol = usuario.getRolUsuario().getNombreRol();
        String token = jwtUtil.generarToken(usuario.getNombreUsuario(), rol, usuario.getIdUsuario());

        return new LoginResponse(usuario.getIdUsuario(), usuario.getNombreUsuario(), rol, token);
    }
}
