package com.sivebo.ms_auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_auth.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

        Optional<Rol> findByNombreRol(String nombreRol);

        Boolean existsByNombreRol(String nombreRol);
}
