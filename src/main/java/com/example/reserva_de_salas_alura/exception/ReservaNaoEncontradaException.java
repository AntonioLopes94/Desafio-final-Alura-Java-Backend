package com.example.reserva_de_salas_alura.exception;

public class ReservaNaoEncontradaException extends RuntimeException {
    public ReservaNaoEncontradaException() {
        super("Reserva não encontrada");
    }
}
