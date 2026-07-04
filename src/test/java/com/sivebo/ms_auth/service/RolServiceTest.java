package com.sivebo.ms_auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sivebo.ms_auth.dto.request.RolRequestDTO;
import com.sivebo.ms_auth.dto.response.RolResponseDTO;
import com.sivebo.ms_auth.exception.DuplicateResourceException;
import com.sivebo.ms_auth.exception.EntityNotFoundException;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.repository.RolRepository;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock RolRepository rolRepository;

    @InjectMocks RolService service;

    private static final Rol OPERADOR = new Rol(1L, "OPERADOR", "Operador de bodega");
    private static final Rol ADMIN = new Rol(2L, "ADMIN", "Administrador");

    @Test
    void getAllRetornaTodosLosRoles() {
        when(rolRepository.findAll()).thenReturn(List.of(OPERADOR, ADMIN));

        List<RolResponseDTO> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("OPERADOR", result.get(0).getNombreRol());
        assertEquals("ADMIN", result.get(1).getNombreRol());
    }

    @Test
    void getAllSinRolesRetornaListaVacia() {
        when(rolRepository.findAll()).thenReturn(List.of());

        assertTrue(service.getAll().isEmpty());
    }

    @Test
    void getByIdEncontradoRetornaDTO() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(OPERADOR));

        Optional<RolResponseDTO> result = service.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("OPERADOR", result.get().getNombreRol());
    }

    @Test
    void getByIdNoExisteRetornaVacio() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.getById(99L).isEmpty());
    }

    @Test
    void createRolNuevoGuardaYRetornaDTO() {
        RolRequestDTO dto = new RolRequestDTO("SUPERVISOR", "Supervisor de turno");
        Rol rolGuardado = new Rol(3L, "SUPERVISOR", "Supervisor de turno");

        when(rolRepository.existsByNombreRol("SUPERVISOR")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenReturn(rolGuardado);

        RolResponseDTO result = service.create(dto);

        assertEquals(3L, result.getId());
        assertEquals("SUPERVISOR", result.getNombreRol());
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void createNombreDuplicadoLanzaDuplicateResourceException() {
        RolRequestDTO dto = new RolRequestDTO("OPERADOR", "otro desc");
        when(rolRepository.existsByNombreRol("OPERADOR")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(dto));
        verify(rolRepository, never()).save(any());
    }

    @Test
    void updateEncontradoActualizaYRetornaDTO() {
        RolRequestDTO dto = new RolRequestDTO("OPERADORV2", "Versión actualizada");
        Rol rolActualizado = new Rol(1L, "OPERADORV2", "Versión actualizada");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(new Rol(1L, "OPERADOR", "Operador")));
        when(rolRepository.save(any(Rol.class))).thenReturn(rolActualizado);

        Optional<RolResponseDTO> result = service.update(1L, dto);

        assertTrue(result.isPresent());
        assertEquals("OPERADORV2", result.get().getNombreRol());
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void updateNoExisteRetornaVacio() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RolResponseDTO> result = service.update(99L, new RolRequestDTO("X", null));

        assertTrue(result.isEmpty());
        verify(rolRepository, never()).save(any());
    }

    @Test
    void deleteByIdRolExisteEliminaYRetornaTrue() {
        when(rolRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rolRepository).deleteById(1L);

        assertTrue(service.deleteById(1L));
        verify(rolRepository).deleteById(1L);
    }

    @Test
    void deleteByIdNoExisteLanzaEntityNotFoundException() {
        when(rolRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deleteById(99L));
        verify(rolRepository, never()).deleteById(any());
    }
}
