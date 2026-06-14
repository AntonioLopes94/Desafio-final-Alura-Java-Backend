package com.example.reserva_de_salas_alura.dto;

import java.time.LocalDateTime;

public record ReservaConflitanteResponse(
        Long reservaId,
        LocalDateTime inicio,
        LocalDateTime fim
) {
}
