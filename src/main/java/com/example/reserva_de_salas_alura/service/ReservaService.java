package com.example.reserva_de_salas_alura.service;

import com.example.reserva_de_salas_alura.entity.Reserva;
import com.example.reserva_de_salas_alura.entity.Sala;
import com.example.reserva_de_salas_alura.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaService salaService;

    public ReservaService(ReservaRepository reservaRepository, SalaService salaService) {
        this.reservaRepository = reservaRepository;
        this.salaService = salaService;
    }

    public List<Reserva> getTodasReservas(){
        return reservaRepository.findAll();
    }

    public Reserva buscarReservaPorId(Long id){
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
    }

    public Reserva criarReserva(Reserva reserva) {
        Long salaId = reserva.getSala().getId();
        Sala salaDaReserva = salaService.buscarSalaById(salaId);
        reserva.setSala(salaDaReserva);

        LocalDateTime dataInicio = reserva.getDataInicio();
        if(reserva.getDataFim() == null){
            reserva.setDataFim(reserva.getDataInicio().plusHours(1));

        }
        LocalDateTime dataFim = reserva.getDataFim();
        verificarDisponibilidadeNaCriacao(salaId, dataInicio, dataFim);

        return reservaRepository.save(reserva);

    }

    public Reserva alterarReserva(Long reservaId, Reserva reserva) {
        Long salaId = reserva.getSala().getId();
        Sala salaDaReserva = salaService.buscarSalaById(salaId);
        Reserva reservaAlterada = buscarReservaPorId(reservaId);

        LocalDateTime dataInicio = reserva.getDataInicio();
        LocalDateTime dataFim = reserva.getDataFim();

        if(dataFim == null){
            dataFim = dataInicio.plusHours(1);
        }

        verificarDisponibilidadeNaAlteracao(
                salaId,
                reservaId,
                dataInicio,
                dataFim
        );

        reservaAlterada.setNomePessoa(reserva.getNomePessoa());
        reservaAlterada.setSala(salaDaReserva);
        reservaAlterada.setDataInicio(dataInicio);
        reservaAlterada.setDataFim(dataFim);

        return reservaRepository.save(reservaAlterada);
    }

    public void deletarReserva(Long id) {
        Reserva reservaDeletada = buscarReservaPorId(id);
        reservaRepository.delete(reservaDeletada);
    }

    public void verificarDisponibilidadeNaCriacao(Long salaId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        boolean existeConflito = reservaRepository
                .existsBySalaIdAndDataInicioLessThanAndDataFimGreaterThan(
                        salaId, dataFim.plusHours(1), dataInicio.minusHours(1));
        if (existeConflito) {
            throw new RuntimeException("Sala indisponível para esse horário");
        }
    }
    void verificarDisponibilidadeNaAlteracao(
            Long salaId,
            Long reservaId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        boolean existeConflito = reservaRepository
                .existsBySalaIdAndIdNotAndDataInicioLessThanAndDataFimGreaterThan(
                        salaId,
                        reservaId,
                        dataFim.plusHours(1),
                        dataInicio.minusHours(1)
                );

        if (existeConflito) {
            throw new RuntimeException("Sala indisponível para esse horário");
        }
    }
}
