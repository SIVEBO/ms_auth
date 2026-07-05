package com.sivebo.ms_auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRequestDTO {

        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password;

        @Email(message = "Email no válido")
        String email;

        String nombreRol;

        String nombreSucursal;

        Boolean activo;
}
