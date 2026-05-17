package com.sivebo.ms_auth.service;

import com.sivebo.ms_auth.dto.*;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.Rol.NombreRol;
import com.sivebo.ms_auth.model.Usuario;
import com.sivebo.ms_auth.repository.RolRepository;
import com.sivebo.ms_auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse registrar(RegisterRequest request) {
        log.info("[ms_auth] Intentando registrar usuario: {}", request.getUsername());

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            log.warn("[ms_auth] Username ya en uso: {}", request.getUsername());
            throw new RuntimeException("El username ya está en uso: " + request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("[ms_auth] Email ya en uso: {}", request.getEmail());
            throw new RuntimeException("El email ya está en uso: " + request.getEmail());
        }

        NombreRol nombreRol = NombreRol.CAJERO;
        if (request.getRol() != null && !request.getRol().isBlank()) {
            try {
                nombreRol = NombreRol.valueOf(request.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("[ms_auth] Rol inválido recibido: {}", request.getRol());
                throw new RuntimeException("Rol inválido: " + request.getRol() + ". Valores: ADMIN, CAJERO");
            }
        }

        final NombreRol rolFinal = nombreRol;
        Rol rol = rolRepository.findByNombre(rolFinal)
                .orElseGet(() -> {
                    log.info("[ms_auth] Creando rol nuevo en BD: {}", rolFinal);
                    return rolRepository.save(new Rol(null, rolFinal));
                });

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(new HashSet<>(Set.of(rol)))
                .activo(true)
                .build();

        UsuarioResponse response = toResponse(usuarioRepository.save(usuario));
        log.info("[ms_auth] Usuario registrado exitosamente id={}, rol={}", response.getId(), rolFinal);
        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("[ms_auth] Intento de login para: {}", request.getUsername());

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("[ms_auth] Usuario no encontrado: {}", request.getUsername());
                    return new RuntimeException("Usuario no encontrado");
                });

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.warn("[ms_auth] Contraseña incorrecta para: {}", request.getUsername());
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!usuario.isActivo()) {
            log.warn("[ms_auth] Usuario inactivo: {}", request.getUsername());
            throw new RuntimeException("Usuario inactivo");
        }

        log.info("[ms_auth] Login exitoso para: {}", request.getUsername());
        return AuthResponse.builder()
                .mensaje("Login exitoso")
                .username(usuario.getUsername())
                .token("JWT_PLACEHOLDER")
                .build();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        log.info("[ms_auth] Listando todos los usuarios");
        List<UsuarioResponse> lista = usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("[ms_auth] Total usuarios encontrados: {}", lista.size());
        return lista;
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
