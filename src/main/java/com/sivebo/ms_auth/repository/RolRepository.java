package com.sivebo.ms_auth.repository;

import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.Rol.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(NombreRol nombre);
}
