package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaNovaPosicaoRequest;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import org.springframework.http.HttpStatus;

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
        Integer novaPosicao = tarefaRepository.contarTarefas(getTarefaRequest().getIdUsuario());
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request,novaPosicao));

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
    void deveModificarOrdemDeUmaTarefa() {
        UUID idTarefa = UUID.randomUUID();
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = Tarefa.builder().idTarefa(idTarefa).idUsuario(usuario.getIdUsuario()).posicao(2).build();
        TarefaNovaPosicaoRequest request = new TarefaNovaPosicaoRequest(0);

        List<Tarefa> tarefas = Arrays.asList(
                Tarefa.builder().idTarefa(UUID.randomUUID()).idUsuario(usuario.getIdUsuario()).posicao(0).build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).idUsuario(usuario.getIdUsuario()).posicao(1).build(),
                tarefa);

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.listaTodasTarefasOrdernadas(usuario.getIdUsuario())).thenReturn(tarefas);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.salva(any(Tarefa.class))).thenReturn(tarefa);

        tarefaApplicationService.modificaOrdemDeUmaTarefa(usuario.getEmail(), idTarefa, request);

        verify(tarefaRepository, times(1)).salva(tarefa);
        verify(tarefaRepository, times(1)).salvaTodasTarefas(tarefas);
        assertEquals(0, tarefa.getPosicao());
        assertEquals(1, tarefas.get(0).getPosicao());
        assertEquals(2, tarefas.get(1).getPosicao());
    }

    @Test
    void deveLancarExcecaoQuandoNovaPosicaoForInvalida() {
        UUID idTarefa = UUID.randomUUID();
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = Tarefa.builder().idTarefa(idTarefa).idUsuario(usuario.getIdUsuario()).posicao(2).build();
        TarefaNovaPosicaoRequest request = new TarefaNovaPosicaoRequest(10);

        List<Tarefa> tarefas = Arrays.asList(
                Tarefa.builder().idTarefa(UUID.randomUUID()).idUsuario(usuario.getIdUsuario()).posicao(0).build(),
                Tarefa.builder().idTarefa(UUID.randomUUID()).idUsuario(usuario.getIdUsuario()).posicao(1).build(),
                tarefa);

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.listaTodasTarefasOrdernadas(usuario.getIdUsuario())).thenReturn(tarefas);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));

        APIException exception = assertThrows(APIException.class, () -> {
            tarefaApplicationService.modificaOrdemDeUmaTarefa(usuario.getEmail(), idTarefa, request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("A posição da tarefa não pode ser igual ou superior a quantidade de tarefas do usuário.", exception.getBodyException().getMessage());
    }
}

