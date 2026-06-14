package com.example.reserva_de_salas_alura.repository;

import com.example.reserva_de_salas_alura.entity.Reserva;
import com.example.reserva_de_salas_alura.enums.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsBySalaIdAndDataInicioLessThanAndDataFimGreaterThan(
            Long salaId,
            LocalDateTime dataFim,
            LocalDateTime dataInicio
    );

    boolean existsBySalaIdAndIdNotAndDataInicioLessThanAndDataFimGreaterThan(
            Long salaId,
            Long reservaId,
            LocalDateTime limiteFim,
            LocalDateTime limiteInicio
    );


    Page<Reserva> findBySalaIdAndStatusNotAndDataInicioLessThanAndDataFimGreaterThan(
            Long salaId,
            StatusReserva statusIgnorado,
            LocalDateTime fimConsultado,
            LocalDateTime inicioConsultado,
            Pageable pageable
    );
}
