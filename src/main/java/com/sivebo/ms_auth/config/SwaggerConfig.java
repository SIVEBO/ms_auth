package com.sivebo.ms_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                final String schemeName = "bearerAuth";
                return new OpenAPI()
                                .info(new Info()
                                                .title("API 2026 Microservicio de Autenticación")
                                                .version("1.0")
                                                .description("Documentacion de la API para el sistema de autenticación y usuarios"))
                                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                                .components(new Components()
                                                .addSecuritySchemes(schemeName, new SecurityScheme()
                                                                .name(schemeName)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")));
        }
}
