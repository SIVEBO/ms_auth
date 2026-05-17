package com.sivebo.ms_auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String mensaje;
    private String username;
    private String token; // placeholder JWT
}
