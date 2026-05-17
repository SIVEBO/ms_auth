package com.sivebo.ms_auth.service;

import com.sivebo.ms_auth.dto.*;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.Rol.NombreRol;
import com.sivebo.ms_auth.model.Usuario;
import com.sivebo.ms_auth.repository.RolRepository;
import com.sivebo.ms_auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("El username ya está en uso: " + request.getUsername());

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && usuarioRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("El email ya está en uso: " + request.getEmail());

        NombreRol nombreRol = NombreRol.CAJERO;
        if (request.getRol() != null && !request.getRol().isBlank()) {
            try {
                nombreRol = NombreRol.valueOf(request.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Rol inválido: " + request.getRol() + ". Valores: ADMIN, CAJERO");
            }
        }

        final NombreRol rolFinal = nombreRol;
        Rol rol = rolRepository.findByNombre(rolFinal)
                .orElseGet(() -> rolRepository.save(new Rol(null, rolFinal)));

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(new HashSet<>(Set.of(rol)))
                .activo(true)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
            throw new RuntimeException("Contraseña incorrecta");

        if (!usuario.isActivo())
            throw new RuntimeException("Usuario inactivo");

        return AuthResponse.builder()
                .mensaje("Login exitoso")
                .username(usuario.getUsername())
                .token("JWT_PLACEHOLDER")
                .build();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UsuarioResponse toResponse(Usuario u) {
        Set<String> roles = u.getRoles().stream()
                .map(r -> r.getNombre().name())
                .collect(Collectors.toSet());
        return UsuarioResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .roles(roles)
                .activo(u.isActivo())
                .build();
    }
}
