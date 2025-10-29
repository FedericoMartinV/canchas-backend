package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.ReservaRequestDTO;
import model.Reserva;
import service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaRequestDTO request) {
        Reserva reserva = reservaService.crear(
            request.getUsuarioId(),
            request.getCanchaId(),
            request.getFecha(),
            request.getHoraInicio(),
            request.getHoraFin()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Reserva> confirmar(@PathVariable String id) {
        return ResponseEntity.ok(reservaService.confirmarReserva(id));
    }
    
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerPorUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(reservaService.obtenerPorUsuario(usuarioId));
    }
    
    @GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<Reserva>> obtenerPorCancha(@PathVariable String canchaId) {
        return ResponseEntity.ok(reservaService.obtenerPorCancha(canchaId));
    }
}