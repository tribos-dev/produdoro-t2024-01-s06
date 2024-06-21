package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {
	
	@InjectMocks
	UsuarioApplicationService usuarioApplicationService;
	
	@Mock
	UsuarioRepository usuarioRepository;
	
	@Test
	void deveMudarStatusParaPausaCurta() {
		//Dado
		Usuario usuario = DataHelper.createUsuario();
		
		//Quando
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
		
		//Então
		verify(usuarioRepository, times(1)).salva(usuario);
		assertEquals(StatusUsuario.PAUSA_CURTA, usuario.getStatus());
	}
	
	@Test
	void naoDeveMudarStatusParaPausaCurta_QuandoIdUsuarioNaoIdentificado() {
		Usuario usuario = DataHelper.createUsuario();
		UUID idUsuario = UUID.fromString("ce138189-3651-4c12-950e-24fe7b7a4417");
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		APIException e = assertThrows(APIException.class, 
				() -> usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), idUsuario));
		assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusException());
	}
    @Test
    void mudaStatusParaPausaLongaTest() {
        Usuario usuario = DataHelper.createUsuarioFoco();
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
        Usuario usuario = DataHelper.createUsuarioFoco();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        APIException exception = assertThrows(APIException.class, usuario::validaSeUsuarioJaEstaEmPausaLonga);
        assertEquals("Usuario ja esta em Pausa Longa", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }

}