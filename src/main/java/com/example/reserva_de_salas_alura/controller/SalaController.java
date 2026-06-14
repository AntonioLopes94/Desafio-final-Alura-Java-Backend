package com.example.reserva_de_salas_alura.controller;

import com.example.reserva_de_salas_alura.dto.DisponibilidadeResponse;
import com.example.reserva_de_salas_alura.entity.Sala;
import com.example.reserva_de_salas_alura.exception.SalaNaoEncontradaException;
import com.example.reserva_de_salas_alura.service.ReservaService;
import com.example.reserva_de_salas_alura.service.SalaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {
    private final SalaService salaService;
    private final ReservaService reservaService;

    public SalaController(SalaService salaService, ReservaService reservaService) {
        this.salaService = salaService;
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Sala> buscarTodasSalas(){
        return salaService.buscarSalasList();
    }

    @GetMapping("/{salaId}")
    public Sala buscarSalaById(@PathVariable Long salaId){
        return salaService.buscarSalaById(salaId);
    }

    @PostMapping
    public ResponseEntity<Sala> criarSala(@RequestBody Sala sala){
        Sala salaCriada = salaService.criarSala(sala);
        return ResponseEntity.status(HttpStatus.CREATED).body(salaCriada);
    }

    @PutMapping("/{salaId}")
    public ResponseEntity<Sala> alterarSala(@PathVariable Long salaId, @RequestBody Sala sala){
        Sala salaAlterada = salaService.alterarSala(salaId, sala);
        return ResponseEntity.status(HttpStatus.OK).body(salaAlterada);
    }

    @DeleteMapping("/{salaId}")
    public ResponseEntity<Void> deletarSala(@PathVariable Long salaId){
        Sala salaDeletada = salaService.deletarSala(salaId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{salaId}/disponibilidade")
    public Mono<ResponseEntity<DisponibilidadeResponse>> consultarDisponibilidade(
            @PathVariable Long salaId,
            @RequestParam
            @DateTimeFormat(pattern = "dd.MM.yy:HH")
            LocalDateTime inicio,
            @RequestParam
            @DateTimeFormat(pattern = "dd.MM.yy:HH")
            LocalDateTime fim,
            @RequestParam(defaultValue = "0")
            int pagina,
            @RequestParam(defaultValue = "10")
            int tamanho
            ){
        return reservaService.consultarDisponibilidade(salaId, inicio, fim, pagina, tamanho)
                .map(ResponseEntity::ok)
                .onErrorResume(
                        IllegalArgumentException.class,
                        erro -> Mono.just(ResponseEntity.badRequest().build())
                )
                .onErrorResume(
                        SalaNaoEncontradaException.class,
                        erro -> Mono.just(ResponseEntity.notFound().build())
                );
    }
}
