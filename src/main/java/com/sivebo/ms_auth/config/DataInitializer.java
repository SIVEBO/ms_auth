package com.sivebo.ms_auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.repository.RolRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final RolRepository rolRepository;

        @Override
        public void run(String... args) {
                saveIfAbsent("Admin", "Acceso total al sistema");
                saveIfAbsent("Operador", "Acceso operativo en sucursal");
                saveIfAbsent("Cliente", "Acceso limitado de consulta");
        }

        private void saveIfAbsent(String nombreRol, String descripcion) {
                if (!rolRepository.existsByNombreRol(nombreRol)) {
                        rolRepository.save(new Rol(null, nombreRol, descripcion));
                        log.info(">>> Rol '{}' creado.", nombreRol);
                }
        }
}
