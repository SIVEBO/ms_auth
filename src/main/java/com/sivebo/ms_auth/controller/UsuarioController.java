package com.sivebo.ms_auth.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_auth.dto.response.UsuarioResponseDTO;
import com.sivebo.ms_auth.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Consulta de usuarios registrados")
public class UsuarioController {

        private final UsuarioService usuarioService;

        @Operation(summary = "Listar todos los usuarios registrados")
        @GetMapping
        public List<UsuarioResponseDTO> listar() {
                return usuarioService.listarUsuarios();
        }
}
