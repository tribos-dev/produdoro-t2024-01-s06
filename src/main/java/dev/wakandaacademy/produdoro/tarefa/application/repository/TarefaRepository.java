package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	void deletaTarefa(Tarefa tarefa);
    Integer contarTarefas(UUID idUsuario);
    List<Tarefa> listaTodasTarefasOrdernadas(UUID idUsuario);
    void salvaTodasTarefas(List<Tarefa> tarefas);
    List<Tarefa> buscaTarefasPorIdUsuario(UUID idUsuario);
}
