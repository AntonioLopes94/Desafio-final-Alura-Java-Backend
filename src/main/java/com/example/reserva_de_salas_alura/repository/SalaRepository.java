package com.example.reserva_de_salas_alura.repository;

import com.example.reserva_de_salas_alura.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}
