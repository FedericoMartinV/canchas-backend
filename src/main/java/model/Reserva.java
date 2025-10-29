package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import observer.Observador;

@Entity
@Table(name = "reservas")
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @NotNull(message = "La cancha es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(nullable = false)
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin es obligatoria")
    @Column(nullable = false)
    private LocalTime horaFin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;
    
    @Transient
    private List<Observador> observadores = new ArrayList<>();
    
    // Constructores
    public Reserva() {
    }
    
    public Reserva(String id, Usuario usuario, Cancha cancha, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin, EstadoReserva estado) {
        this.id = id;
        this.usuario = usuario;
        this.cancha = cancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.observadores = new ArrayList<>();
    }
    
    // Métodos del patrón Observer
    public void agregarObservador(Observador observador) {
        observadores.add(observador);
    }
    
    public void removerObservador(Observador observador) {
        observadores.remove(observador);
    }
    
    public void confirmar() {
        this.estado = EstadoReserva.CONFIRMADA;
        notificar("CONFIRMADA");
    }
    
    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
        notificar("CANCELADA");
    }
    
    private void notificar(String evento) {
        for (Observador obs : observadores) {
            obs.actualizar(this, evento);
        }
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Cancha getCancha() {
        return cancha;
    }
    
    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    
    public LocalTime getHoraFin() {
        return horaFin;
    }
    
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
    
    public EstadoReserva getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
    
    public List<Observador> getObservadores() {
        return observadores;
    }
    
    public void setObservadores(List<Observador> observadores) {
        this.observadores = observadores;
    }
    
    // Enum interno
    public enum EstadoReserva {
        PENDIENTE, CONFIRMADA, CANCELADA
    }
}