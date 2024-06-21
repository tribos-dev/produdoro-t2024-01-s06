package dev.wakandaacademy.produdoro.usuario.domain;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsuarioTest {

    @Test
    public void validaUsuarioDeveRetornarException() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = UUID.randomUUID();

        APIException exception = assertThrows(APIException.class, () -> usuario.validaUsuario(idUsuario));
        assertThrows(APIException.class, () -> usuario.validaUsuario(idUsuario));
        org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
        org.junit.jupiter.api.Assertions.assertEquals("Credencial de autenticação não é valida!", exception.getMessage());
    }
}