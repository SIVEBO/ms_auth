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
@Table(name = "usuarios")
public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @Column(name = "username", nullable = false, unique = true, length = 50)
        String username;

        @Column(name = "password_hash", nullable = false, length = 60, columnDefinition = "CHAR(60)")
        String passwordHash;

        @Column(name = "email", unique = true, length = 100)
        String email;

        @ManyToOne
        @JoinColumn(name = "id_rol", nullable = false)
        Rol rol;

        // Ref Ext -> db_ms_sucursales.SUCURSAL.nombre_sucursal
        @Column(name = "nombre_sucursal", length = 100)
        String nombreSucursal;

        @Column(name = "activo", nullable = false)
        Boolean activo;

        @Column(name = "created_at", nullable = false)
        LocalDateTime createdAt;
}
