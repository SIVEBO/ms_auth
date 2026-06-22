package com.sivebo.ms_auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_auth.dto.request.UpdateUsuarioRequestDTO;
import com.sivebo.ms_auth.dto.response.UsuarioResponseDTO;
import com.sivebo.ms_auth.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Consulta y gestión de usuarios registrados")
public class UsuarioController {

        private final UsuarioService usuarioService;

        @Operation(summary = "Listar todos los usuarios registrados")
        @GetMapping
        public List<UsuarioResponseDTO> listar() {
                return usuarioService.listarUsuarios();
        }

        @Operation(summary = "Obtener usuario por id", description = "Usado para validacion entre microservicios via WebClient")
        @GetMapping("/{id}")
        public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
                return ResponseEntity.ok(usuarioService.getById(id));
        }

        @Operation(summary = "Actualizar usuario", description = "Permite actualizar email, password, rol, sucursal y estado")
        @PutMapping("/{id}")
        public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
                        @Valid @RequestBody UpdateUsuarioRequestDTO dto) {
                return ResponseEntity.ok(usuarioService.actualizar(id, dto));
        }
}
