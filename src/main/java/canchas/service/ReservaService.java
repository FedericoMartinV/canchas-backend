package canchas.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import canchas.exception.HorarioInvalidoException;
import canchas.exception.ReservaSolapadaException;
import canchas.model.Cancha;
import canchas.model.Reserva;
import canchas.model.Usuario;
import canchas.observer.AdministradorNotificador;
import canchas.observer.UsuarioNotificador;
import canchas.repository.ReservaRepository;

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
    
    @Transactional
    public Reserva crear(String usuarioId, String canchaId, LocalDate fecha, 
                         String horaInicio, String horaFin) {
        LocalTime inicio = LocalTime.parse(horaInicio);
        LocalTime fin = LocalTime.parse(horaFin);

        if (!fin.isAfter(inicio)) {
            throw new HorarioInvalidoException("La hora de fin (" + horaFin + ") debe ser posterior a la hora de inicio (" + horaInicio + ")");
        }

        Usuario usuario = usuarioService.obtenerPorId(usuarioId);
        Cancha cancha = canchaService.obtenerPorId(canchaId);

        List<Reserva> reservasExistentes = reservaRepository.findByCanchaIdAndFecha(canchaId, fecha);

        boolean solapada = reservasExistentes.stream()
                .filter(r -> r.getEstado() != Reserva.EstadoReserva.CANCELADA)
                .anyMatch(r -> haySolapamiento(inicio, fin, r.getHoraInicio(), r.getHoraFin()));

        if (solapada) {
            throw new ReservaSolapadaException("La cancha ya tiene una reserva activa que se solapa con el horario solicitado (" + horaInicio + " - " + horaFin + ")");
        }
        
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        
        // Agregar observadores
        reserva.agregarObservador(usuarioNotificador);
        reserva.agregarObservador(administradorNotificador);
        
        return reservaRepository.save(reserva);
    }

    private boolean haySolapamiento(LocalTime inicioNueva, LocalTime finNueva,
                                   LocalTime inicioExistente, LocalTime finExistente) {
        return inicioNueva.isBefore(finExistente) && finNueva.isAfter(inicioExistente);
    }
    
    @Transactional
    public Reserva confirmarReserva(String id) {
        Reserva reserva = obtenerPorId(id);
        reserva.agregarObservador(usuarioNotificador);
        reserva.agregarObservador(administradorNotificador);
        reserva.confirmar();
        return reservaRepository.save(reserva);
    }
    
    @Transactional
    public Reserva cancelarReserva(String id) {
        Reserva reserva = obtenerPorId(id);
        reserva.agregarObservador(usuarioNotificador);
        reserva.agregarObservador(administradorNotificador);
        reserva.cancelar();
        return reservaRepository.save(reserva);
    }
    
    @Transactional
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