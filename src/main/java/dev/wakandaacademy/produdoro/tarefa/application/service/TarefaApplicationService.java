package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaNovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaDetalhadaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        Integer novaPosicao = tarefaRepository.contarTarefas(tarefaRequest.getIdUsuario());
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest,novaPosicao));
        log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
        return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
    }

    @Override
    public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - detalhaTarefa");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
        return tarefa;
    }

    @Override
    public void incrementaPomodoro(String emailDoUsuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
        Tarefa tarefa = detalhaTarefa(emailDoUsuario, idTarefa);
        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(emailDoUsuario);
        mudaStatusDeAcordoComPomodoros(tarefa, usuario);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
    }
    
    @Override
    private void mudaStatusDeAcordoComPomodoros(Tarefa tarefa, Usuario usuario) {
        if (!usuario.getStatus().equals(StatusUsuario.FOCO)) {
            usuario.mudaStatusParaFoco(usuario.getIdUsuario());
            usuarioRepository.salva(usuario);
            throw APIException.build(HttpStatus.CONFLICT, "Usuário não está com o status em 'FOCO', portanto não pode incrementar pomodoro.");
        }
        boolean sePassaram4Pomodoros = tarefa.incrementaPomodoro() % 4 == 0;
        if (sePassaram4Pomodoros) usuario.mudaStatusParaPausaLonga(usuario.getIdUsuario());
        else usuario.mudaStatusParaPausaCurta(usuario.getIdUsuario());
        usuarioRepository.salva(usuario);

    @Transactional
    @Override
    public void modificaOrdemDeUmaTarefa(String emailUsuario, UUID idTarefa, TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest) {
        log.info("[inicia] TarefaApplicationService - modificaOrdemDeUmaTarefa");
        Tarefa tarefa = detalhaTarefa(emailUsuario,idTarefa);
        List<Tarefa> tarefas = tarefaRepository.listaTodasTarefasOrdernadas(tarefa.getIdUsuario());
        tarefa.mudaOrdemTarefa(tarefas,tarefaNovaPosicaoRequest);
        tarefaRepository.salvaTodasTarefas(tarefas);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - modificaOrdemDeUmaTarefa");
    }

    @Override
    public void marcarTarefaConcluida(String usuario, UUID idTarefa) {
        log.info("[start] TarefaApplicationService - marcarTarefaConcluida");
        Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
        tarefa.concluiTarefa();
        tarefaRepository.salva(tarefa);
        log.info("[finish] TarefaApplicationService - marcarTarefaConcluida");
    }
    
    @Override    
    public List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(String email, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - listaTodasTarefasDoUsuario");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(email);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        usuarioPorEmail.validaUsuario(idUsuario);
        log.info("[valida] - Usuário validado com sucesso");
        List<Tarefa> tarefas = tarefaRepository.buscaTarefasPorIdUsuario(idUsuario);
        log.info("[finaliza] TarefaApplicationService - listaTodasTarefasDoUsuario");
        return TarefaDetalhadaListResponse.converte(tarefas);
    }
}
