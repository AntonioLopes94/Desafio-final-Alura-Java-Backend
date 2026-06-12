package com.example.reserva_de_salas_alura.entity;

import com.example.reserva_de_salas_alura.enums.StatusReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomePessoa;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;
}
