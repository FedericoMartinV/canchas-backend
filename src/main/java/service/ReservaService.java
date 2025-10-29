package service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Cancha;
import model.Reserva;
import model.Usuario;
import observer.AdministradorNotificador;
import observer.UsuarioNotificador;
import repository.ReservaRepository;

@Service
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final CanchaService canchaService;
    private final UsuarioNotificador usuarioNotificador;
    private final AdministradorNotificador administradorNotificador;
    
    public ReservaService(ReservaRepository reservaRepository,
                         UsuarioService usuarioService,
                         CanchaService canchaService,
                         UsuarioNotificador usuarioNotificador,
                         AdministradorNotificador administradorNotificador) {
        this.reservaRepository = reservaRepository;
        this.usuarioService = usuarioService;
        this.canchaService = canchaService;
        this.usuarioNotificador = usuarioNotificador;
        this.administradorNotificador = administradorNotificador;
    }
    
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }
    
    public Reserva obtenerPorId(String id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }
    
    public Reserva crear(String usuarioId, String canchaId, LocalDate fecha, 
                         String horaInicio, String horaFin) {
        Usuario usuario = usuarioService.obtenerPorId(usuarioId);
        Cancha cancha = canchaService.obtenerPorId(canchaId);
        
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(java.time.LocalTime.parse(horaInicio));
        reserva.setHoraFin(java.time.LocalTime.parse(horaFin));
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        
        // Agregar observadores
        reserva.agregarObservador(usuarioNotificador);
        reserva.agregarObservador(administradorNotificador);
        
        return reservaRepository.save(reserva);
    }
    
    public Reserva confirmarReserva(String id) {
        Reserva reserva = obtenerPorId(id);
        reserva.agregarObservador(usuarioNotificador);
        reserva.agregarObservador(administradorNotificador);
        reserva.confirmar();
        return reservaRepository.save(reserva);
    }
    
    public Reserva cancelarReserva(String id) {
        Reserva reserva = obtenerPorId(id);
        reserva.agregarObservador(usuarioNotificador);
        reserva.agregarObservador(administradorNotificador);
        reserva.cancelar();
        return reservaRepository.save(reserva);
    }
    
    public void eliminar(String id) {
        reservaRepository.deleteById(id);
    }
    
    public List<Reserva> obtenerPorUsuario(String usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
    
    public List<Reserva> obtenerPorCancha(String canchaId) {
        return reservaRepository.findByCanchaId(canchaId);
    }
}