package com.example.reserva_de_salas_alura.service;

import com.example.reserva_de_salas_alura.entity.Sala;
import com.example.reserva_de_salas_alura.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {
    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public Sala buscarSalaById(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada"));
    }

    public List<Sala> buscarSalasList() {
        return salaRepository.findAll();
    }

    public Sala criarSala(Sala sala) {
        return salaRepository.save(sala);
    }

    public Sala alterarSala(Long id, Sala sala) {
        Sala salaAlterada = buscarSalaById(id);
        salaAlterada.setNome(sala.getNome());
        return salaRepository.save(salaAlterada);
    }

    public Sala deletarSala(Long id) {
        Sala salaDeletada = buscarSalaById(id);
        salaRepository.delete(salaDeletada);
        return salaDeletada;
    }
}
