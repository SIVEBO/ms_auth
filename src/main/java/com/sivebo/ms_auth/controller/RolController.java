package com.sivebo.ms_auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_auth.dto.request.RolRequestDTO;
import com.sivebo.ms_auth.dto.response.RolResponseDTO;
import com.sivebo.ms_auth.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "CRUD de roles disponibles en el sistema (requiere rol Admin)")
public class RolController {

        private final RolService rolService;

        @Operation(summary = "Listar todos los roles")
        @GetMapping
        public List<RolResponseDTO> getAll() {
                return rolService.getAll();
        }

        @Operation(summary = "Obtener rol por id")
        @GetMapping("/{id}")
        public ResponseEntity<RolResponseDTO> getById(@PathVariable Long id) {
                return rolService.getById(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Crear un nuevo rol")
        @PostMapping
        public ResponseEntity<RolResponseDTO> create(@Valid @RequestBody RolRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED).body(rolService.create(dto));
        }

        @Operation(summary = "Editar un rol existente")
        @PutMapping("/{id}")
        public ResponseEntity<RolResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RolRequestDTO dto) {
                return rolService.update(id, dto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Eliminar un rol")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                rolService.deleteById(id);
                return ResponseEntity.noContent().build();
        }
}
