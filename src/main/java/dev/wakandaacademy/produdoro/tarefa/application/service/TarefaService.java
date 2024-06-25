package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaNovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaDetalhadaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;

public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);

    Tarefa detalhaTarefa(String usuario, UUID idTarefa);

    void deletaTarefa(String usuario, UUID idTarefa);

    void modificaOrdemDeUmaTarefa(String emailUsuario, UUID idTarefa,
            TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest);

    void marcarTarefaConcluida(String usuario, UUID idTarefa);

    void incrementaPomodoro(String emailDoUsuario, UUID idTarefa);

    void editaTarefa(String usuario, UUID idTarefa, EditaTarefaRequest editaTarefaRequest);

    void usuarioDeletaTodasTarefas(String emailUsuario, UUID idUsuario);

    List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(String email, UUID idUsuario);
}
