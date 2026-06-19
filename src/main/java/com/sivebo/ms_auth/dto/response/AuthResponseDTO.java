package com.sivebo.ms_auth.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

        String mensaje;
        String username;
        String token;
        Date fechaExpiracion;
}
