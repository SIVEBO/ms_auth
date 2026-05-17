package com.sivebo.ms_auth.dto;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean activo;
}
