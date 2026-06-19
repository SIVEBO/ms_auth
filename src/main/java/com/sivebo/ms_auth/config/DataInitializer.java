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
                if (rolRepository.count() > 0) {
                        log.info(">>> Roles ya cargados. Se omite inicialización.");
                        return;
                }

                log.info(">>> Cargando roles iniciales...");

                rolRepository.save(new Rol(null, "Admin", "Acceso total al sistema"));
                rolRepository.save(new Rol(null, "Operador", "Acceso operativo en sucursal"));
                rolRepository.save(new Rol(null, "Cliente", "Acceso limitado de consulta"));

                log.info(">>> Roles iniciales cargados exitosamente.");
        }
}
