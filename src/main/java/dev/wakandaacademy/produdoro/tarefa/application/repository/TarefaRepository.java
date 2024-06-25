package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	void deletaTarefa(Tarefa tarefa);
    void usuarioDeletaTodasTarefas(UUID idUsuario);
    Integer contarTarefas(UUID idUsuario);
    List<Tarefa> listaTodasTarefasOrdernadas(UUID idUsuario);
    void salvaTodasTarefas(List<Tarefa> tarefas);
    List<Tarefa> buscaTarefasPorIdUsuario(UUID idUsuario);
    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
    void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);
    void atualizaPosicoesDasTarefas(List<Tarefa> tarefasDoUsuario);
}
