package com.sivebo.ms_auth.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens_sesion")
public class TokenSesion {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @ManyToOne
        @JoinColumn(name = "id_usuario", nullable = false)
        Usuario usuario;

        @Column(name = "token", nullable = false, unique = true, length = 512)
        String token;

        @Column(name = "fecha_expiracion", nullable = false)
        LocalDateTime fechaExpiracion;

        @Column(name = "created_at", nullable = false)
        LocalDateTime createdAt;
}
