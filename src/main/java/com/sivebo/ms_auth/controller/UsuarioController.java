package com.sivebo.ms_auth.controller;

import com.sivebo.ms_auth.dto.UsuarioResponse;
import com.sivebo.ms_auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        log.info("[ms_auth] GET /usuarios");
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
}
