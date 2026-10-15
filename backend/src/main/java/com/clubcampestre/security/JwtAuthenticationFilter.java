package com.clubcampestre.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que se ejecuta en cada petición: lee el encabezado Authorization: Bearer,
 * valida el token JWT y registra al usuario autenticado con su rol.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /** Si la petición trae un token válido, autentica al usuario; si no, la deja seguir sin autenticar. */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.validarYObtenerClaims(token);
                String nombreUsuario = claims.getSubject();
                String rol = claims.get("rol", String.class);

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()));
                var authentication = new UsernamePasswordAuthenticationToken(nombreUsuario, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext(); // token inválido o vencido: sigue sin autenticar
            }
        }

        filterChain.doFilter(request, response);
    }
}
