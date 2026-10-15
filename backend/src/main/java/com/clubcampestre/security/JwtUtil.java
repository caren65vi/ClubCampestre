package com.clubcampestre.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/** Genera y valida los tokens JWT firmados con la clave JWT_SECRET. */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs = 1000 * 60 * 60 * 8; // 8 horas

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /** Crea un token JWT con el usuario, su rol y su id, firmado con HMAC-SHA. */
    public String generarToken(String nombreUsuario, String rol, Integer idUsuario) {
        return Jwts.builder()
                .subject(nombreUsuario)
                .claim("rol", rol)
                .claim("idUsuario", idUsuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /** Verifica la firma y la vigencia del token y devuelve sus datos (claims). */
    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
