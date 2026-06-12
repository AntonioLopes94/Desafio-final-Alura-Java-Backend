package com.example.reserva_de_salas_alura.controller;

import com.example.reserva_de_salas_alura.entity.Reserva;
import com.example.reserva_de_salas_alura.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaService reservaService;
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> buscarTodasReservas(){
        return reservaService.getTodasReservas();
    }

    @GetMapping("/{id}")
    public Reserva buscarReservaPorId(@PathVariable Long id){
        return reservaService.buscarReservaPorId(id);
    }

    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody Reserva reserva){
        Reserva reservaCriada = reservaService.criarReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> alterarReserva(@PathVariable Long id, @RequestBody Reserva reserva){
        Reserva reservaAlterada = reservaService.alterarReserva(id, reserva);
        return ResponseEntity.status(HttpStatus.OK).body(reservaAlterada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReserva(@PathVariable Long id){
        reservaService.deletarReserva(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
