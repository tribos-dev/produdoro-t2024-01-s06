package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.management.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;
    private final MongoTemplate mongoTemplate;

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
	public void deletaTarefa(Tarefa tarefa) {
		log.info("[inicia] TarefaInfraRepository - deletaTarefa");
		tarefaSpringMongoDBRepository.delete(tarefa);
		log.info("[finaliza] TarefaInfraRepository - deletaTarefa");
	}

    @Override
    public void usuarioDeletaTodasTarefas(UUID idUsuario) {
        log.info("[start] TarefaInfraRepository - deletarTodasTarefas");
        tarefaSpringMongoDBRepository.deleteAllByIdUsuario(idUsuario);
        log.info("[finish] TarefaInfraRepository - deletarTodasTarefas");
    }
    
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
    
    public List<Tarefa> buscaTarefasPorIdUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasPorIdUsuario");
        List<Tarefa> tarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasPorIdUsuario");
        return tarefas;
    }

    @Override
    public List<Tarefa> buscaTarefasConcluidas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasConcluidas");
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("idUsuario").is(idUsuario).and("status").is(StatusTarefa.CONCLUIDA));
        List<Tarefa> tarefasConcluidas = mongoTemplate.find(query, Tarefa.class);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasConcluidas");
        return tarefasConcluidas;
    }

    @Override
    public void deletaVariasTarefas(List<Tarefa> tarefasConcluidas) {
        log.info("[inicia] TarefaInfraRepository - deletaVariasTarefas");
        tarefaSpringMongoDBRepository.deleteAll(tarefasConcluidas);
        log.info("[finaliza] TarefaInfraRepository - deletaVariasTarefas");
    }

    @Override
    public void atualizaPosicoesDasTarefas(List<Tarefa> tarefasDoUsuario) {
        log.info("[inicia] TarefaInfraRepository - atualizaPosicoesDasTarefas");
        int tamanhoDaLista = tarefasDoUsuario.size();
        List<Tarefa> tarefasAtualizadas = IntStream.range(0, tamanhoDaLista)
                        .mapToObj(i-> atualizaTarefaComNovaPosicao(tarefasDoUsuario.get(i), i)).collect(Collectors.toList());
        salvaVariasTarefas(tarefasAtualizadas);
        log.info("[finaliza] TarefaInfraRepository - atualizaPosicoesDasTarefas");
    }

    private void salvaVariasTarefas(List<Tarefa> tarefasAtualizadas) {
        tarefaSpringMongoDBRepository.saveAll(tarefasAtualizadas);
    }

    private Tarefa atualizaTarefaComNovaPosicao(Tarefa tarefa, int novaPosicao) {
        tarefa.atualizaPosicao(novaPosicao);
        return tarefa;
    }

}
