package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaDetalhadaListResponse;
import lombok.extern.log4j.Log4j2;
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
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveDeletarTodasAsTarefasDoUsuario(){
        Usuario usuario = DataHelper.createUsuario();

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);

        tarefaApplicationService.usuarioDeletaTodasTarefas(usuario.getEmail(), usuario.getIdUsuario());

        verify(tarefaRepository, times(1)).usuarioDeletaTodasTarefas(usuario.getIdUsuario());
       // assertEquals(); //buscar todas as tarefas do usuario
    }


    @Test
    void deveAlterarStatusDaTarefaParaConcluidaQuandoTarefaValida(){
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
    void deveRetornarBadRequestQuandoIdTarefaForInvalido(){
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
        });        assertEquals("Credencial de autenticação não é valida!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
    }
}
