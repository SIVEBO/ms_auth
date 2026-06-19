package com.sivebo.ms_auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

        @NotBlank(message = "El username es obligatorio")
        String username;

        @NotBlank(message = "La contraseña es obligatoria")
        String password;
}
