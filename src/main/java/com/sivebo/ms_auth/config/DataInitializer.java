package com.sivebo.ms_auth.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.Usuario;
import com.sivebo.ms_auth.repository.RolRepository;
import com.sivebo.ms_auth.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final RolRepository rolRepository;
        private final UsuarioRepository usuarioRepository;
        private final BCryptPasswordEncoder passwordEncoder;

        @Value("${bootstrap.admin.username:admin}")
        private String adminUsername;

        @Value("${bootstrap.admin.password:admin}")
        private String adminPassword;

        @Override
        public void run(String... args) {
                saveIfAbsent("Admin", "Acceso total al sistema");
                saveIfAbsent("Operador", "Acceso operativo en sucursal");
                saveIfAbsent("Cliente", "Acceso limitado de consulta");
                bootstrapAdminUsuario();
        }

        private void saveIfAbsent(String nombreRol, String descripcion) {
                if (!rolRepository.existsByNombreRol(nombreRol)) {
                        rolRepository.save(new Rol(null, nombreRol, descripcion));
                        log.info(">>> Rol '{}' creado.", nombreRol);
                }
        }

        private void bootstrapAdminUsuario() {
                if (usuarioRepository.existsByUsername(adminUsername)) {
                        return;
                }
                Rol rolAdmin = rolRepository.findByNombreRol("Admin")
                                .orElseThrow(() -> new EntityNotFoundException("Rol 'Admin' no encontrado"));

                Usuario admin = new Usuario(
                                null,
                                adminUsername,
                                passwordEncoder.encode(adminPassword),
                                null,
                                rolAdmin,
                                null,
                                true,
                                LocalDateTime.now());
                usuarioRepository.save(admin);
                log.warn(">>> Usuario admin inicial '{}' creado. CAMBIE la contraseña por defecto en producción.",
                                adminUsername);
        }
}
