package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/editaTarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void editaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idTarefa, @RequestBody @Valid EditaTarefaRequest editaTarefaRequest);

    @DeleteMapping("/{idUsuario}/exclusao-todas-tarefas")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deletaTodasTarefas(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idUsuario);

    @PatchMapping("/{idTarefa}/nova-posicao")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void modificaOrdemDeUmaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idTarefa,
            @RequestBody @Valid TarefaNovaPosicaoRequest tarefaNovaPosicaoRequest);

    @PatchMapping("/{idTarefa}/concluida")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void marcarTarefaConcluida(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idTarefa);

    @GetMapping("/lista-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(@PathVariable UUID idUsuario,
            @RequestHeader(name = "Authorization", required = true) String token);
}
