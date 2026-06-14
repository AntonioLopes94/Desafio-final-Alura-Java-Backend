package com.example.reserva_de_salas_alura.dto;

import java.util.List;

public record DisponibilidadeResponse(
        Long salaId,
        boolean disponivel,
        List<ReservaConflitanteResponse> conflitos,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {
}
