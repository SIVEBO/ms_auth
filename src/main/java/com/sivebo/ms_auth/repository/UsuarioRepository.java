package com.sivebo.ms_auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_auth.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

        Optional<Usuario> findByUsername(String username);

        Boolean existsByUsername(String username);

        Boolean existsByEmail(String email);
}
