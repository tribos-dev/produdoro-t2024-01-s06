package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	@Override
	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void incrementaPomodoro(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaPomodoro");
		String emailDoUsuario = getUsuarioByToken(token);
		tarefaService.incrementaPomodoro(emailDoUsuario, idTarefa);
		log.info("[finaliza] TarefaRestController - incrementaPomodoro");
	}

	@Override
	public void editaTarefa(String token, UUID idTarefa, EditaTarefaRequest editaTarefaRequest) {
		String usuario = getUsuarioByToken(token);
		tarefaService.editaTarefa(usuario, idTarefa, editaTarefaRequest);
		log.info("[finaliza]  TarefaRestController - editaTarefa");
	}

	@Override
	public void deletaTodasTarefas(String token, UUID idUsuario) {
		log.info("[start] TarefaRestController - deletaTodasTarefas");
		String emailUsuario = getUsuarioByToken(token);
		tarefaService.usuarioDeletaTodasTarefas(emailUsuario, idUsuario);
		log.info("[finish] TarefaRestController - deletaTodasTarefas");
	}

	@Override
	public void modificaOrdemDeUmaTarefa(String token, UUID idTarefa,
			TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest) {
		log.info("[inicia] TarefaRestController - modificaOrdemDeUmaTarefa");
		String emailUsuario = getUsuarioByToken(token);
		tarefaService.modificaOrdemDeUmaTarefa(emailUsuario, idTarefa, tarefaNovaPosicaoRequest);
		log.info("[finaliza] TarefaRestController - modificaOrdemDeUmaTarefa");
	}

	@Override
	public void marcarTarefaConcluida(String token, UUID idTarefa) {
		log.info("[start] TarefaRestController - marcarTarefaConcluida");
		String usuario = getUsuarioByToken(token);
		tarefaService.marcarTarefaConcluida(usuario, idTarefa);
		log.info("[finish] TarefaRestController - marcarTarefaConcluida");
	}

	@Override
	public List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(UUID idUsuario, String token) {
		log.info("[inicia] TarefaRestController - listaTodasTarefasDoUsuario");
		String email = getUsuarioByToken(token);
		List<TarefaDetalhadaListResponse> tarefas = tarefaService.listaTodasTarefasDoUsuario(email, idUsuario);
		log.info("[finaliza] TarefaRestController - listaTodasTarefasDoUsuario");
		return tarefas;
	}

    @Override
    public void defineTarefaComoAtiva(UUID idTarefa, String token) {
        log.info("[inicia] TarefaRestController - defineTarefaComoAtiva");
		String email = getUsuarioByToken(token);
		tarefaService.defineTarefaComoAtiva(idTarefa, email);
        log.info("[finaliza] TarefaRestController - defineTarefaComoAtiva");
    }
	@Override
	public void deletaTarefaConcluidas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTarefaConcluidas");
		String email = getUsuarioByToken(token);
		tarefaService.deletaTarefasConcluidas(email, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTarefasConcluidas");

	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void deletaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - deletaTarefa");
		log.info("[idTarefa] {}", idTarefa);
		String usuario = getUsuarioByToken(token);
		tarefaService.deletaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - deletaTarefa");
	}

}
