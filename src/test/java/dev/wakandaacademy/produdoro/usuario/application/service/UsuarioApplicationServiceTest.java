package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
<<<<<<< HEAD

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

=======

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

>>>>>>> feature/PROD-257-BE-Usuario-muda-status-pausa-longa
@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {
    @InjectMocks
    UsuarioApplicationService usuarioApplicationService;
    @Mock
    UsuarioRepository usuarioRepository;
<<<<<<< HEAD
=======

    @Test
    void mudaStatusParaPausaLongaTest() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(usuario);
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
    }

    @Test
    void validaSeUsuarioJaEstaEmPausaLonga() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        APIException exception = assertThrows(APIException.class, usuario::validaSeUsuarioJaEstaEmPausaLonga);
        assertEquals("Usuario ja esta em Pausa Longa", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }
>>>>>>> feature/PROD-257-BE-Usuario-muda-status-pausa-longa

    @Test
    void mudaStatusParaFocoTest() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(usuario);
        assertEquals(StatusUsuario.FOCO, usuario.getStatus());
    }

    @Test
    void validaSeUsuarioJaEstaEmFoco() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
        APIException exception = assertThrows(APIException.class, usuario::validaSeUsuarioJaEstaEmFoco);
        assertEquals("Usuario ja esta em Foco", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }
}