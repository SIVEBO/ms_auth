package com.sivebo.ms_auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolRequestDTO {

        @NotBlank(message = "El nombre del rol es obligatorio")
        String nombreRol;

        String descripcion;
}
