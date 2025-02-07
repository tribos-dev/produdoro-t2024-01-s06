package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
        @PostMapping
        @ResponseStatus(code = HttpStatus.CREATED)
        TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

        @GetMapping("/{idTarefa}")
        @ResponseStatus(code = HttpStatus.OK)
        TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                        @PathVariable UUID idTarefa);

        @DeleteMapping(value = "/deleta-tarefa/{idTarefa}")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
        void deletaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                        @PathVariable UUID idTarefa);

    @DeleteMapping("/usuario/{idUsuario}/deleta-tarefas-concluidas")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deletaTarefaConcluidas(@RequestHeader(name = "Authorization", required = true)String token, @PathVariable UUID idUsuario);

        @PatchMapping("/editaTarefa/{idTarefa}")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
        void editaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                        @PathVariable UUID idTarefa, @RequestBody @Valid EditaTarefaRequest editaTarefaRequest);

        @DeleteMapping("/{idUsuario}/exclusao-todas-tarefas")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
        void deletaTodasTarefas(@RequestHeader(name = "Authorization", required = true) String token,
                        @PathVariable UUID idUsuario);

        @PostMapping("/{idTarefa}/incrementa-pomodoro")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
        void incrementaPomodoro(@RequestHeader(name = "Authorization") String token, @PathVariable UUID idTarefa);

        @GetMapping("/lista-tarefas/{idUsuario}")
        @ResponseStatus(code = HttpStatus.OK)
        List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(@PathVariable UUID idUsuario,
            @RequestHeader(name = "Authorization", required = true) String token);

        @PatchMapping("/ativa-tarefa/{idTarefa}")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
         void defineTarefaComoAtiva(@PathVariable UUID idTarefa,
                               @RequestHeader(name = "Authorization", required = true) String token);
        @PatchMapping("/{idTarefa}/nova-posicao")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
        void modificaOrdemDeUmaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                        @PathVariable UUID idTarefa,
                        @RequestBody @Valid TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest);

        @PatchMapping("/{idTarefa}/concluida")
        @ResponseStatus(code = HttpStatus.NO_CONTENT)
        void marcarTarefaConcluida(@RequestHeader(name = "Authorization", required = true) String token,
                        @PathVariable UUID idTarefa);
}
