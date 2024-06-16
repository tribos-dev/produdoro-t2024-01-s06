package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;

    @Override
    public Tarefa salva(Tarefa tarefa) {
        log.info("[inicia] TarefaInfraRepository - salva");
        try {
            tarefaSpringMongoDBRepository.save(tarefa);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
        }
        log.info("[finaliza] TarefaInfraRepository - salva");
        return tarefa;
    }
    @Override
    public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
        Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
        return tarefaPorId;
    }

    @Override
    public Integer contarTarefas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - contarTarefas");
        Integer quantidadeTarefas = tarefaSpringMongoDBRepository.countByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - contarTarefas");
        return quantidadeTarefas;
    }

    @Override
    public List<Tarefa> listaTodasTarefasOrdernadas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - listaTodasTarefasOrdernadas");
        List<Tarefa> tarefasOrdenadasAsc = tarefaSpringMongoDBRepository.findByIdUsuarioOrderByPosicaoAsc(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - listaTodasTarefasOrdernadas");
        return tarefasOrdenadasAsc;
    }

    @Override
    public void salvaTodasTarefas(List<Tarefa> tarefas) {
        log.info("[inicia] TarefaInfraRepository - salvaTodasTarefas");
        tarefaSpringMongoDBRepository.saveAll(tarefas);
        log.info("[finaliza] TarefaInfraRepository - salvaTodasTarefas");
    }
}
