package com.sivebo.ms_auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_auth.model.TokenSesion;

public interface TokenSesionRepository extends JpaRepository<TokenSesion, Long> {

        Optional<TokenSesion> findByToken(String token);

        void deleteByToken(String token);
}
