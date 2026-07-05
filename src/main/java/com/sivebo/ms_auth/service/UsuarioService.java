package com.sivebo.ms_auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sivebo.ms_auth.dto.request.LoginRequestDTO;
import com.sivebo.ms_auth.dto.request.RegisterRequestDTO;
import com.sivebo.ms_auth.dto.request.UpdateUsuarioRequestDTO;
import com.sivebo.ms_auth.dto.response.AuthResponseDTO;
import com.sivebo.ms_auth.dto.response.UsuarioResponseDTO;
import com.sivebo.ms_auth.exception.DuplicateResourceException;
import com.sivebo.ms_auth.exception.EntityNotFoundException;
import com.sivebo.ms_auth.exception.InvalidCredentialsException;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.TokenSesion;
import com.sivebo.ms_auth.model.Usuario;
import com.sivebo.ms_auth.repository.RolRepository;
import com.sivebo.ms_auth.repository.TokenSesionRepository;
import com.sivebo.ms_auth.repository.UsuarioRepository;
import com.sivebo.ms_auth.utils.JwtUtil;
import com.sivebo.ms_auth.utils.MapToDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService extends MapToDTO {

        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final TokenSesionRepository tokenSesionRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        public UsuarioResponseDTO registrar(RegisterRequestDTO dto) {
                if (usuarioRepository.existsByUsername(dto.getUsername())) {
                        throw new DuplicateResourceException("El username ya está en uso: " + dto.getUsername());
                }
                if (dto.getEmail() != null && !dto.getEmail().isBlank()
                                && usuarioRepository.existsByEmail(dto.getEmail())) {
                        throw new DuplicateResourceException("El email ya está en uso: " + dto.getEmail());
                }

                Rol rol = rolRepository.findByNombreRol(dto.getNombreRol())
                                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + dto.getNombreRol()));

                Usuario usuario = new Usuario(
                                null,
                                dto.getUsername(),
                                passwordEncoder.encode(dto.getPassword()),
                                dto.getEmail(),
                                rol,
                                dto.getNombreSucursal(),
                                true,
                                LocalDateTime.now());

                log.info(">>> Usuario registrado: {} con rol {}", dto.getUsername(), dto.getNombreRol());
                return mapUsuarioToDTO(usuarioRepository.save(usuario));
        }

        public AuthResponseDTO login(LoginRequestDTO dto) {
                Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                                .orElseThrow(() -> new InvalidCredentialsException("Usuario o contraseña incorrectos"));

                if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
                        throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
                }
                if (!usuario.getActivo()) {
                        throw new InvalidCredentialsException("El usuario se encuentra inactivo");
                }

                String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol().getNombreRol());

                TokenSesion sesion = new TokenSesion(
                                null,
                                usuario,
                                token,
                                jwtUtil.extractExpiration(token).toInstant()
                                                .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                LocalDateTime.now());
                tokenSesionRepository.save(sesion);

                log.info(">>> Login exitoso para usuario: {}", dto.getUsername());

                return new AuthResponseDTO(
                                "Login exitoso",
                                usuario.getUsername(),
                                token,
                                jwtUtil.extractExpiration(token));
        }

        public List<UsuarioResponseDTO> listarUsuarios() {
                return usuarioRepository.findAll()
                                .stream()
                                .map(this::mapUsuarioToDTO)
                                .collect(Collectors.toList());
        }

        public UsuarioResponseDTO getById(Long id) {
                return usuarioRepository.findById(id)
                                .map(this::mapUsuarioToDTO)
                                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        }

        public UsuarioResponseDTO actualizar(Long id, UpdateUsuarioRequestDTO dto) {
                Usuario usuario = usuarioRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

                if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
                }
                if (dto.getEmail() != null) {
                        if (!dto.getEmail().isBlank() && !dto.getEmail().equals(usuario.getEmail())
                                        && usuarioRepository.existsByEmail(dto.getEmail())) {
                                throw new DuplicateResourceException("El email ya está en uso: " + dto.getEmail());
                        }
                        usuario.setEmail(dto.getEmail());
                }
                if (dto.getNombreRol() != null && !dto.getNombreRol().isBlank()) {
                        Rol nuevoRol = rolRepository.findByNombreRol(dto.getNombreRol())
                                        .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + dto.getNombreRol()));
                        usuario.setRol(nuevoRol);
                }
                if (dto.getNombreSucursal() != null) {
                        usuario.setNombreSucursal(dto.getNombreSucursal());
                }
                if (dto.getActivo() != null) {
                        usuario.setActivo(dto.getActivo());
                }

                log.info(">>> Usuario actualizado id={}", id);
                return mapUsuarioToDTO(usuarioRepository.save(usuario));
        }

        public void logout(String token) {
                TokenSesion sesion = tokenSesionRepository.findByToken(token)
                                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada o ya invalidada"));
                tokenSesionRepository.delete(sesion);
                log.info(">>> Logout exitoso, token invalidado");
        }
}
