package com.example.reserva_de_salas_alura.service;

import com.example.reserva_de_salas_alura.dto.ReservaConfirmadaEvento;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ReservaEventoService {
    private final Sinks.Many<ReservaConfirmadaEvento> eventos =
            Sinks.many()
                    .multicast()
                    .onBackpressureBuffer();

    public void publicar(ReservaConfirmadaEvento evento) {
        eventos.tryEmitNext(evento);
    }

    public Flux<ReservaConfirmadaEvento> acompanhar(){
        return eventos.asFlux();
    }

}
