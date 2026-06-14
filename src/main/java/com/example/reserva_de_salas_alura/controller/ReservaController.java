package com.example.reserva_de_salas_alura.controller;

import com.example.reserva_de_salas_alura.dto.ReservaConfirmadaEvento;
import com.example.reserva_de_salas_alura.entity.Reserva;
import com.example.reserva_de_salas_alura.exception.ReservaNaoEncontradaException;
import com.example.reserva_de_salas_alura.service.ReservaEventoService;
import com.example.reserva_de_salas_alura.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaService reservaService;
    private final ReservaEventoService reservaEventoService;
    public ReservaController(ReservaService reservaService, ReservaEventoService reservaEventoService) {
        this.reservaService = reservaService;
        this.reservaEventoService = reservaEventoService;
    }

    @GetMapping
    public List<Reserva> buscarTodasReservas(){
        return reservaService.getTodasReservas();
    }

    @GetMapping("/{id}")
    public Reserva buscarReservaPorId(@PathVariable Long id){
        return reservaService.buscarReservaPorId(id);
    }

    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody Reserva reserva){
        Reserva reservaCriada = reservaService.criarReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> alterarReserva(@PathVariable Long id, @RequestBody Reserva reserva){
        Reserva reservaAlterada = reservaService.alterarReserva(id, reserva);
        return ResponseEntity.ok(reservaAlterada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReserva(@PathVariable Long id){
        reservaService.deletarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{reservaId}/confirmar")
    public Mono<ResponseEntity<Reserva>> confirmarReserva(@PathVariable Long reservaId){
        return reservaService.confirmarReserva(reservaId)
                .map(ResponseEntity::ok)
                .onErrorResume(
                        ReservaNaoEncontradaException.class,
                        erro -> Mono.just(
                                ResponseEntity.notFound().build()
                        )
                );
    }

    @GetMapping(
            value = "/eventos/confirmadas",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ReservaConfirmadaEvento>> acompanharReservasConfirmadas(){
        return reservaEventoService.acompanhar()
                .map(evento -> ServerSentEvent
                        .builder(evento)
                        .event("reserva-confirmada")
                        .id(evento.reservaId().toString())
                        .build());
    }

}
