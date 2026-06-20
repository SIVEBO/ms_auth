package com.sivebo.ms_auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sivebo.ms_auth.dto.request.LoginRequestDTO;
import com.sivebo.ms_auth.dto.request.RegisterRequestDTO;
import com.sivebo.ms_auth.dto.response.AuthResponseDTO;
import com.sivebo.ms_auth.dto.response.UsuarioResponseDTO;
import com.sivebo.ms_auth.exception.DuplicateResourceException;
import com.sivebo.ms_auth.exception.EntityNotFoundException;
import com.sivebo.ms_auth.exception.InvalidCredentialsException;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.model.TokenSesion;
import com.sivebo.ms_auth.model.Usuario;
import com.sivebo.ms_auth.repository.RolRepository;
import com.sivebo.ms_auth.repository.TokenSesionRepository;
import com.sivebo.ms_auth.repository.UsuarioRepository;
import com.sivebo.ms_auth.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock RolRepository rolRepository;
    @Mock TokenSesionRepository tokenSesionRepository;
    @Mock BCryptPasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;

    @InjectMocks UsuarioService service;

    private static final Rol ROL_OPERADOR = new Rol(1L, "OPERADOR", "Operador de bodega");

    private static final Usuario USUARIO = new Usuario(
            1L, "testuser", "$2a$hash", "test@mail.com",
            ROL_OPERADOR, 10L, true, LocalDateTime.of(2026, 1, 1, 0, 0));

    private static final Date EXPIRATION = new Date(System.currentTimeMillis() + 3_600_000L);

    @Test
    void registrar_usuarioNuevo_guardaYRetornaDTO() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "testuser", "pass123", "test@mail.com", "OPERADOR", 10L);

        when(usuarioRepository.existsByUsername("testuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(rolRepository.findByNombreRol("OPERADOR")).thenReturn(Optional.of(ROL_OPERADOR));
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(USUARIO);

        UsuarioResponseDTO result = service.registrar(dto);

        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("OPERADOR", result.getNombreRol());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_usernameDuplicado_lanzaDuplicateResourceException() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "testuser", "pass123", "new@mail.com", "OPERADOR", null);

        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.registrar(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_emailDuplicado_lanzaDuplicateResourceException() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "newuser", "pass123", "test@mail.com", "OPERADOR", null);

        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.registrar(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_rolNoExiste_lanzaEntityNotFoundException() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "newuser", "pass123", null, "SUPERADMIN", null);

        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(rolRepository.findByNombreRol("SUPERADMIN")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.registrar(dto));
    }

    @Test
    void login_credencialesValidas_retornaToken() {
        LoginRequestDTO dto = new LoginRequestDTO("testuser", "pass123");

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(USUARIO));
        when(passwordEncoder.matches("pass123", "$2a$hash")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "OPERADOR")).thenReturn("jwt-token");
        when(jwtUtil.extractExpiration("jwt-token")).thenReturn(EXPIRATION);
        when(tokenSesionRepository.save(any(TokenSesion.class))).thenReturn(new TokenSesion());

        AuthResponseDTO result = service.login(dto);

        assertEquals("Login exitoso", result.getMensaje());
        assertEquals("testuser", result.getUsername());
        assertEquals("jwt-token", result.getToken());
        verify(tokenSesionRepository).save(any(TokenSesion.class));
    }

    @Test
    void login_usuarioNoExiste_lanzaInvalidCredentialsException() {
        LoginRequestDTO dto = new LoginRequestDTO("noexiste", "pass123");
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> service.login(dto));
    }

    @Test
    void login_passwordIncorrecta_lanzaInvalidCredentialsException() {
        LoginRequestDTO dto = new LoginRequestDTO("testuser", "wrongpass");

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(USUARIO));
        when(passwordEncoder.matches("wrongpass", "$2a$hash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> service.login(dto));
        verify(tokenSesionRepository, never()).save(any());
    }

    @Test
    void login_usuarioInactivo_lanzaInvalidCredentialsException() {
        Usuario inactivo = new Usuario(2L, "inactivo", "$2a$hash", null, ROL_OPERADOR, null, false, LocalDateTime.now());
        LoginRequestDTO dto = new LoginRequestDTO("inactivo", "pass123");

        when(usuarioRepository.findByUsername("inactivo")).thenReturn(Optional.of(inactivo));
        when(passwordEncoder.matches("pass123", "$2a$hash")).thenReturn(true);

        assertThrows(InvalidCredentialsException.class, () -> service.login(dto));
        verify(tokenSesionRepository, never()).save(any());
    }

    @Test
    void listarUsuarios_retornaTodosLosMapeados() {
        when(usuarioRepository.findAll()).thenReturn(List.of(USUARIO));

        List<UsuarioResponseDTO> result = service.listarUsuarios();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void getById_encontrado_retornaDTO() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(USUARIO));

        UsuarioResponseDTO result = service.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getById_noExiste_lanzaEntityNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void logout_tokenValido_eliminaSesion() {
        TokenSesion sesion = new TokenSesion();
        when(tokenSesionRepository.findByToken("jwt-token")).thenReturn(Optional.of(sesion));
        doNothing().when(tokenSesionRepository).delete(sesion);

        service.logout("jwt-token");

        verify(tokenSesionRepository).delete(sesion);
    }

    @Test
    void logout_tokenNoExiste_lanzaEntityNotFoundException() {
        when(tokenSesionRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.logout("invalid-token"));
        verify(tokenSesionRepository, never()).delete(any());
    }
}
