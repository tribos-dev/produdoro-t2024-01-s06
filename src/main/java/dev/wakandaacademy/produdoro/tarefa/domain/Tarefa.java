package dev.wakandaacademy.produdoro.tarefa.domain;

import java.util.List;
import java.util.UUID;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaNovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Document(collection = "Tarefa")
public class Tarefa {
	@Id
	private UUID idTarefa;
	@NotBlank
	private String descricao;
	@Indexed
	private UUID idUsuario;
	@Indexed
	private UUID idArea;
	@Indexed
	private UUID idProjeto;
	private StatusTarefa status;
	private StatusAtivacaoTarefa statusAtivacao;
	private int contagemPomodoro;
	private Integer posicao;

	public Tarefa(TarefaRequest tarefaRequest, Integer novaPosicao) {
		this.idTarefa = UUID.randomUUID();
		this.idUsuario = tarefaRequest.getIdUsuario();
		this.descricao = tarefaRequest.getDescricao();
		this.idArea = tarefaRequest.getIdArea();
		this.idProjeto = tarefaRequest.getIdProjeto();
		this.status = StatusTarefa.A_FAZER;
		this.statusAtivacao = StatusAtivacaoTarefa.INATIVA;
		this.contagemPomodoro = 1;
		this.posicao = novaPosicao;
	}

	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if (!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário não é dono da Tarefa solicitada!");
		}
	}

	public int incrementaPomodoro() {
		return ++contagemPomodoro;
	}

	public void editaTarefa(EditaTarefaRequest editaTarefaRequest) {
		this.descricao = editaTarefaRequest.getDescricao();
	}

	public void mudaOrdemTarefa(List<Tarefa> tarefas, TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest) {
		verificaNovaPosicao(tarefas, tarefaNovaPosicaoRequest);
		alteraOrdemTarefa(tarefas, tarefaNovaPosicaoRequest);
	}

	private void alteraOrdemTarefa(List<Tarefa> tarefas, TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest) {
		if (tarefaNovaPosicaoRequest.getNovaPosicao() < this.posicao) {
			for (int i = tarefaNovaPosicaoRequest.getNovaPosicao(); i < this.posicao; i++) {
				tarefas.get(i).posicao++;
			}
		} else if (tarefaNovaPosicaoRequest.getNovaPosicao() > this.posicao) {
			for (int i = this.posicao + 1; i <= tarefaNovaPosicaoRequest.getNovaPosicao(); i++) {
				tarefas.get(i).posicao--;
			}
		}
		this.posicao = tarefaNovaPosicaoRequest.getNovaPosicao();
	}

	private void verificaNovaPosicao(List<Tarefa> tarefas, TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest) {
		if (tarefaNovaPosicaoRequest.getNovaPosicao() >= tarefas.size()
				|| tarefaNovaPosicaoRequest.getNovaPosicao().equals(this.posicao)) {
			String mensagem = (tarefaNovaPosicaoRequest.getNovaPosicao() >= tarefas.size())
					? "A posição da tarefa não pode ser igual ou superior a quantidade de tarefas do usuário."
					: String.format("A tarefa já está na posição %s.", tarefaNovaPosicaoRequest.getNovaPosicao());
			throw APIException.build(HttpStatus.BAD_REQUEST, mensagem);
		}
	}

	public void concluiTarefa() {
		if (status.equals(StatusTarefa.CONCLUIDA))
			throw APIException.build(HttpStatus.CONFLICT, "Tarefa já foi concluida!");
		status = StatusTarefa.CONCLUIDA;
	}

    public void atualizaPosicao(int novaPosicao) {
		this.posicao = novaPosicao;
    }
}
