package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.*;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {
    // @Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    // @MockBean
    @Mock
    TarefaRepository tarefaRepository;
    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        Integer novaPosicao = tarefaRepository.contarTarefas(getTarefaRequest().getIdUsuario());
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, novaPosicao));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveDeletarTodasAsTarefasDoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasPorIdUsuario(usuario.getIdUsuario())).thenReturn(tarefas);

        tarefaApplicationService.usuarioDeletaTodasTarefas(usuario.getEmail(), usuario.getIdUsuario());

        verify(tarefaRepository, times(1)).usuarioDeletaTodasTarefas(usuario.getIdUsuario());
        // assertEquals(); //buscar todas as tarefas do usuario
    }

    @Test
    void deveRetornarBadRequestQuandoUsuarioNaoTiverTarefasCadastradas() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefaVazia();

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasPorIdUsuario(usuario.getIdUsuario())).thenReturn(tarefas);

        APIException exception = assertThrows(APIException.class,
                () -> tarefaApplicationService.usuarioDeletaTodasTarefas(usuario.getEmail(), usuario.getIdUsuario()));

        assertEquals(exception.getMessage(), "Usuário não possui tarefa(as) cadastrada(as)");
        assertEquals(exception.getStatusException(), HttpStatus.BAD_REQUEST);
        verify(tarefaRepository, never()).usuarioDeletaTodasTarefas(any(UUID.class));

    }

    @Test
    void deveRetornarUnauthorizedQuandoTokenNaoPertencerAoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        Usuario usuarioInvalido = DataHelper.createUsuarioInvalido();

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);

        APIException exception = assertThrows(APIException.class,
                () -> tarefaApplicationService.usuarioDeletaTodasTarefas(usuario.getEmail(),
                        usuarioInvalido.getIdUsuario()));

        assertEquals(exception.getMessage(), "Usuário(a) não autorizado(a) para a requisição solicitada!");
        assertEquals(exception.getStatusException(), HttpStatus.UNAUTHORIZED);
        verify(tarefaRepository, never()).usuarioDeletaTodasTarefas(any(UUID.class));
    }

    @Test
    void usuarioNaoEncontradoAoTentarExcluirTodasAsTarefas() {
        Usuario usuarioEmail = DataHelper.createUsuario();
        Usuario usuarioInvalido = DataHelper.createUsuarioInvalido();

        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail.getEmail())).thenReturn(usuarioEmail);

        when(usuarioRepository.buscaUsuarioPorId(usuarioInvalido.getIdUsuario()))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Usuario não encontrado!"));

        APIException exception = assertThrows(APIException.class,
                () -> tarefaApplicationService.usuarioDeletaTodasTarefas(usuarioEmail.getEmail(),
                        usuarioInvalido.getIdUsuario()));

        assertEquals(exception.getMessage(), "Usuario não encontrado!");
        assertEquals(exception.getStatusException(), HttpStatus.NOT_FOUND);
        verify(tarefaRepository, never()).usuarioDeletaTodasTarefas(any(UUID.class));
    }

    @Test
    void deveAlterarStatusDaTarefaParaConcluidaQuandoTarefaValida() {
        Tarefa tarefaRequest = DataHelper.createTarefa();
        Usuario usuarioEmail = DataHelper.createUsuario();
        Usuario usuarioResponse = DataHelper.createUsuario();

        when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmail.getEmail())).thenReturn(usuarioResponse);
        when(tarefaRepository.buscaTarefaPorId(tarefaRequest.getIdTarefa())).thenReturn(Optional.of(tarefaRequest));

        tarefaApplicationService.marcarTarefaConcluida(usuarioEmail.getEmail(), tarefaRequest.getIdTarefa());

        assertEquals(tarefaRequest.getStatus(), StatusTarefa.CONCLUIDA);
        assertEquals(usuarioEmail.getIdUsuario(), tarefaRequest.getIdUsuario());
        verify(tarefaRepository, times(1)).salva(any(Tarefa.class));
    }

    @Test
    void deveRetornarBadRequestQuandoIdTarefaForInvalido() {
        Tarefa tarefaRequest = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario();

        when(tarefaRepository.buscaTarefaPorId(tarefaRequest.getIdTarefa())).thenReturn(Optional.empty());
        assertThrows(APIException.class,
                () -> tarefaApplicationService.marcarTarefaConcluida(usuario.getEmail(), tarefaRequest.getIdTarefa()));

        verify(tarefaRepository, never()).salva(any(Tarefa.class));
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    void deveEditarTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        EditaTarefaRequest editaTarefaRequest = DataHelper.createEditaTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.editaTarefa(usuario.getEmail(), tarefa.getIdTarefa(), editaTarefaRequest);
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(tarefaRepository, times(1)).buscaTarefaPorId(tarefa.getIdTarefa());
        assertEquals("TAREFA2", tarefa.getDescricao());
    }

    @Test
    void naoDeveEditarTarefa() {
        UUID idTarefaInvalido = UUID.randomUUID();
        String usuario = "Messi";
        EditaTarefaRequest editaTarefaRequest = DataHelper.createEditaTarefa();
        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
        assertThrows(APIException.class,
                () -> tarefaApplicationService.editaTarefa(usuario, idTarefaInvalido, editaTarefaRequest));
        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefaInvalido);
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
        assertEquals("A posição da tarefa não pode ser igual ou superior a quantidade de tarefas do usuário.",
                exception.getBodyException().getMessage());
    }

    @Test
    void deveListarTodasTarefasDoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        String email = usuario.getEmail();
        UUID idUsuario = usuario.getIdUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasPorIdUsuario(idUsuario)).thenReturn(tarefas);
        List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario = tarefaApplicationService
                .listaTodasTarefasDoUsuario(email, idUsuario);

        assertNotNull(listaTodasTarefasDoUsuario);
        assertEquals(8, listaTodasTarefasDoUsuario.size());
        verify(tarefaRepository, times(1)).buscaTarefasPorIdUsuario(idUsuario);
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(email);
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(idUsuario);
    }

    @Test
    void deveRetornarListaDeTarefasVazia() {
        Usuario usuario = DataHelper.createUsuario();
        String email = usuario.getEmail();
        UUID idUsuario = usuario.getIdUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefaVazia();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasPorIdUsuario(any())).thenReturn(tarefas);
        List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario = tarefaApplicationService
                .listaTodasTarefasDoUsuario(email, idUsuario);

        assertNotNull(listaTodasTarefasDoUsuario);
        assertEquals(0, listaTodasTarefasDoUsuario.size());
        verify(tarefaRepository, times(1)).buscaTarefasPorIdUsuario(idUsuario);
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(email);
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(idUsuario);
    }

    @Test
    void deveGerarExceptionDeCredencialInvalida() {
        Usuario usuario = mock(Usuario.class);
        String email = usuario.getEmail();
        UUID idUsuario = UUID.randomUUID();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        doThrow(APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é valida!"))
                .when(usuario).validaUsuario(idUsuario);

        APIException exception = assertThrows(APIException.class, () -> {
            tarefaApplicationService.listaTodasTarefasDoUsuario(email, idUsuario);
        });
        assertEquals("Credencial de autenticação não é valida!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
    }
}
