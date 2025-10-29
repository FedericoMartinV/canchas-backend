package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.Cancha;
import repository.CanchaRepository;

@Service
public class CanchaService {
    
    private final CanchaRepository canchaRepository;
    
    public CanchaService(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }
    
    public List<Cancha> obtenerTodas() {
        return canchaRepository.findAll();
    }
    
    public Cancha obtenerPorId(String id) {
        return canchaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada con ID: " + id));
    }
    
    public Cancha crear(Cancha cancha) {
        return canchaRepository.save(cancha);
    }
    
    public Cancha actualizar(String id, Cancha cancha) {
        Cancha existente = obtenerPorId(id);
        existente.setNombre(cancha.getNombre());
        existente.setUbicacion(cancha.getUbicacion());
        existente.setTipo(cancha.getTipo());
        existente.setPrecioPorHora(cancha.getPrecioPorHora());
        existente.setIluminacion(cancha.getIluminacion());
        return canchaRepository.save(existente);
    }
    
    public void eliminar(String id) {
        canchaRepository.deleteById(id);
    }
    
    public List<Cancha> buscarPorTipo(String tipo) {
        return canchaRepository.findByTipo(tipo);
    }
}