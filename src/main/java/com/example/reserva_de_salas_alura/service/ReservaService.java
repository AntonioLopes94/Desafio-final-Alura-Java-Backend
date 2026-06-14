package com.example.reserva_de_salas_alura.service;

import com.example.reserva_de_salas_alura.dto.DisponibilidadeResponse;
import com.example.reserva_de_salas_alura.dto.ReservaConfirmadaEvento;
import com.example.reserva_de_salas_alura.dto.ReservaConflitanteResponse;
import com.example.reserva_de_salas_alura.entity.Reserva;
import com.example.reserva_de_salas_alura.entity.Sala;
import com.example.reserva_de_salas_alura.enums.StatusReserva;
import com.example.reserva_de_salas_alura.exception.ReservaNaoEncontradaException;
import com.example.reserva_de_salas_alura.repository.ReservaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaService salaService;
    private final ReservaEventoService reservaEventoService;

    public ReservaService(ReservaRepository reservaRepository, SalaService salaService, ReservaEventoService reservaEventoService) {
        this.reservaRepository = reservaRepository;
        this.salaService = salaService;
        this.reservaEventoService = reservaEventoService;
    }

    public Mono<DisponibilidadeResponse> consultarDisponibilidade(
            Long salaId,
            LocalDateTime inicio,
            LocalDateTime fim,
            int pagina,
            int tamanho
    ){
        return Mono.fromCallable(() -> {
            validarConsultaDisponibilidade(inicio, fim, pagina, tamanho);
            salaService.buscarSalaById(salaId);

            Page<Reserva> conflitos = buscarConflitos(
                    salaId,
                    inicio,
                    fim,
                    pagina,
                    tamanho
            );

            return montarDisponibilidadeResponse(
                    salaId,
                    conflitos
            );
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Page<Reserva> buscarConflitos(Long salaId, LocalDateTime inicio, LocalDateTime fim, int pagina, int tamanho) {
        return reservaRepository
                .findBySalaIdAndStatusNotAndDataInicioLessThanAndDataFimGreaterThan(
                        salaId,
                        StatusReserva.CANCELADA,
                        fim,
                        inicio,
                        PageRequest.of(pagina, tamanho)
                );
    }
    private ReservaConflitanteResponse converterConflito(
            Reserva reserva
    ) {
        return new ReservaConflitanteResponse(
                reserva.getId(),
                reserva.getDataInicio(),
                reserva.getDataFim()
        );
    }
    private DisponibilidadeResponse montarDisponibilidadeResponse(
            Long salaId,
            Page<Reserva> paginaDeConflitos
    ) {
        boolean disponivel =
                paginaDeConflitos.getTotalElements() == 0;

        List<ReservaConflitanteResponse> conflitos =
                paginaDeConflitos.getContent()
                        .stream()
                        .map(this::converterConflito)
                        .toList();

        return new DisponibilidadeResponse(
                salaId,
                disponivel,
                conflitos,
                paginaDeConflitos.getNumber(),
                paginaDeConflitos.getSize(),
                paginaDeConflitos.getTotalElements(),
                paginaDeConflitos.getTotalPages()
        );
    }

    private void validarConsultaDisponibilidade(LocalDateTime inicio, LocalDateTime fim, int pagina, int tamanho) {
        if(!inicio.isBefore(fim)){
            throw new IllegalArgumentException("A data de inicio deve ser anterior a data de fim");
        }

        if(pagina < 0){
            throw new IllegalArgumentException("A página não pode ser negativa");
        }

        if(tamanho < 1 || tamanho > 100){
            throw new IllegalArgumentException("O tamanho da página deve estar entre 1 e 100");
        }

    }

    public List<Reserva> getTodasReservas(){
        return reservaRepository.findAll();
    }

    public Reserva buscarReservaPorId(Long id){
        return reservaRepository.findById(id)
                .orElseThrow(ReservaNaoEncontradaException::new);
    }

    public Reserva criarReserva(Reserva reserva) {
        Long salaId = reserva.getSala().getId();
        Sala salaDaReserva = salaService.buscarSalaById(salaId);
        reserva.setSala(salaDaReserva);

        LocalDateTime dataInicio = reserva.getDataInicio();
        if (reserva.getDataFim() == null) {
            reserva.setDataFim(reserva.getDataInicio().plusHours(1));
        }
        LocalDateTime dataFim = reserva.getDataFim();
        verificarDisponibilidadeNaCriacao(salaId, dataInicio, dataFim);

        reserva.setStatus(StatusReserva.PENDENTE);

        return reservaRepository.save(reserva);
    }

    public Mono<Reserva> confirmarReserva(Long reservaId){
        return Mono.fromCallable(() -> {
            Reserva reserva = buscarReservaPorId(reservaId);
            if (reserva.getStatus() == StatusReserva.CONFIRMADA){
                return reserva;
            }
            reserva.setStatus(StatusReserva.CONFIRMADA);
            Reserva reservaConfirmada = reservaRepository.save(reserva);

            ReservaConfirmadaEvento evento = new ReservaConfirmadaEvento(
                    reservaConfirmada.getId(),
                    reservaConfirmada.getSala().getId(),
                    reservaConfirmada.getNomePessoa(),
                    reservaConfirmada.getDataInicio(),
                    reservaConfirmada.getDataFim()
            );
            reservaEventoService.publicar(evento);

            return reservaConfirmada;
        }).subscribeOn(Schedulers.boundedElastic());

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
