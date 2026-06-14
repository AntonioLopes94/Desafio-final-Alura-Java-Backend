package com.example.reserva_de_salas_alura.dto;

import java.time.LocalDateTime;

public record ReservaConfirmadaEvento(
        Long reservaId,
        Long salaId,
        String nomePessoa,
        LocalDateTime inicio,
        LocalDateTime fim
) {
}
