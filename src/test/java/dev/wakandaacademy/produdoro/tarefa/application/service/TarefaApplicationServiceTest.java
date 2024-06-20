package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import org.webjars.NotFoundException;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;
    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }



    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    void deveIncrementarPomodoro() {
        // Dado
        Tarefa tarefa = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario(StatusUsuario.FOCO);
        int pomodoroAntes = tarefa.getContagemPomodoro();

        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefa.getIdTarefa());

        // Verifique
        int pomodoroDepois = tarefa.getContagemPomodoro();
        verify(tarefaRepository, times(1)).salva(any());
        assertEquals(pomodoroAntes + 1, pomodoroDepois);
    }

    @Test
    void naoDeveEncontrarTarefa() {
        // Dado
        Usuario usuario = DataHelper.createUsuario(StatusUsuario.FOCO);

        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);

        // Verifique
        assertThrows(NotFoundException.class, () -> tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("Quando um usuário que não é dono da tarefa tentar incrementá-la um pomodoro, deve ser lançada uma APIException e o status 401 deve ser definido na response.")
    void deveLancarAPIException() {
        // Dado
        Usuario usuario1 = DataHelper.createUsuario(StatusUsuario.FOCO);
        Usuario usuario2 = DataHelper.createUsuario2(StatusUsuario.FOCO);
        Tarefa tarefa = DataHelper.createTarefa(usuario2.getIdUsuario());

        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario1);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        // Verifique
        assertThrows(APIException.class, () -> tarefaApplicationService.incrementaPomodoro(usuario1.getEmail(), tarefa.getIdTarefa()));
    }
}
