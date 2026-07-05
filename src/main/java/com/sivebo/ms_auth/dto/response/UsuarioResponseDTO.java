package com.sivebo.ms_auth.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

        Long id;
        String username;
        String email;
        String nombreRol;
        String nombreSucursal;
        Boolean activo;
        LocalDateTime createdAt;
}
