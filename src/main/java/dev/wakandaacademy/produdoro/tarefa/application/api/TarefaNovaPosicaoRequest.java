package dev.wakandaacademy.produdoro.tarefa.application.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@ToString
public class TarefaNovaPosicaoRequest {

    @NotNull
    @PositiveOrZero
    private Integer novaPosicao;

    public TarefaNovaPosicaoRequest(@JsonProperty("novaPosicao") Integer novaPosicao) {
        this.novaPosicao = novaPosicao;
    }
}