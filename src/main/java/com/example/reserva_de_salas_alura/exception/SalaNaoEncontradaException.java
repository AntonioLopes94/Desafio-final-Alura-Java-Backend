package com.example.reserva_de_salas_alura.exception;

public class SalaNaoEncontradaException extends RuntimeException{
    public SalaNaoEncontradaException(){
        super("Sala não encontrada");
    }
}
