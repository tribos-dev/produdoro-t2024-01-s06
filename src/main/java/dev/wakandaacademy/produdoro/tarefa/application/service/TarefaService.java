package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaDetalhadaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;
public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
    void usuarioDeletaTodasTarefas(String emailUsuario, UUID idUsuario);
    void marcarTarefaConcluida(String usuario, UUID idTarefa);
    List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(String email, UUID idUsuario);

}
