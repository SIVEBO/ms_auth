package com.sivebo.ms_auth.utils;

import com.sivebo.ms_auth.dto.response.RolResponseDTO;
import com.sivebo.ms_auth.dto.response.UsuarioResponseDTO;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.Usuario;

public class MapToDTO {

        protected RolResponseDTO mapRolToDTO(Rol rol) {
                return new RolResponseDTO(
                                rol.getId(),
                                rol.getNombreRol(),
                                rol.getDescripcion());
        }

        protected UsuarioResponseDTO mapUsuarioToDTO(Usuario usuario) {
                return new UsuarioResponseDTO(
                                usuario.getId(),
                                usuario.getUsername(),
                                usuario.getEmail(),
                                usuario.getRol().getNombreRol(),
                                usuario.getNombreSucursal(),
                                usuario.getActivo(),
                                usuario.getCreatedAt());
        }
}
