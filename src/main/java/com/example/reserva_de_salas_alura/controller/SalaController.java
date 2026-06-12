package com.example.reserva_de_salas_alura.controller;

import com.example.reserva_de_salas_alura.entity.Sala;
import com.example.reserva_de_salas_alura.service.SalaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public List<Sala> buscarTodasSalas(){
        return salaService.buscarSalasList();
    }

    @GetMapping("/{id}")
    public Sala buscarSalaById(@PathVariable Long id){
        return salaService.buscarSalaById(id);
    }

    @PostMapping
    public ResponseEntity<Sala> criarSala(@RequestBody Sala sala){
        Sala salaCriada = salaService.criarSala(sala);
        return ResponseEntity.status(HttpStatus.CREATED).body(salaCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sala> alterarSala(@PathVariable Long id, @RequestBody Sala sala){
        Sala salaAlterada = salaService.alterarSala(id, sala);
        return ResponseEntity.status(HttpStatus.OK).body(salaAlterada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSala(@PathVariable Long id){
        Sala salaDeletada = salaService.deletarSala(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
