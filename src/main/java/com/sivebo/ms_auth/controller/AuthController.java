package com.sivebo.ms_auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_auth.dto.request.LoginRequestDTO;
import com.sivebo.ms_auth.dto.request.RegisterRequestDTO;
import com.sivebo.ms_auth.dto.response.AuthResponseDTO;
import com.sivebo.ms_auth.dto.response.UsuarioResponseDTO;
import com.sivebo.ms_auth.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y logout de usuarios")
public class AuthController {

        private final UsuarioService usuarioService;

        @Operation(summary = "Registrar usuario", description = "registra un usuario asignando rol y sucursal")
        @PostMapping("/register")
        public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
                log.info(">>> POST /api/v1/auth/register - username: {}", dto.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
        }

        @Operation(summary = "Iniciar sesión", description = "retorna un token JWT válido")
        @PostMapping("/login")
        public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
                log.info(">>> POST /api/v1/auth/login - username: {}", dto.getUsername());
                return ResponseEntity.ok(usuarioService.login(dto));
        }

        @Operation(summary = "Cerrar sesión", description = "invalida el token JWT activo")
        @PostMapping("/logout")
        public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
                String token = authHeader.replace("Bearer ", "");
                usuarioService.logout(token);
                log.info(">>> POST /api/v1/auth/logout");
                return ResponseEntity.ok("Sesión cerrada exitosamente");
        }
}
