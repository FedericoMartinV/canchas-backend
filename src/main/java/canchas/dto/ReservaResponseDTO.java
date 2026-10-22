package canchas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import canchas.model.Reserva;

@Schema(description = "Datos de respuesta para una reserva")
public class ReservaResponseDTO {

    @Schema(description = "UUID de la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "Datos resumidos del usuario que realizó la reserva")
    private UsuarioResumenDTO usuario;

    @Schema(description = "Datos resumidos de la cancha reservada")
    private CanchaResumenDTO cancha;

    @Schema(description = "Fecha de la reserva en formato ISO (YYYY-MM-DD)", example = "2026-09-20")
    private LocalDate fecha;

    @Schema(description = "Hora de inicio en formato HH:mm:ss", example = "18:00:00")
    private LocalTime horaInicio;

    @Schema(description = "Hora de fin en formato HH:mm:ss", example = "19:00:00")
    private LocalTime horaFin;

    @Schema(description = "Estado actual de la reserva", example = "PENDIENTE", allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA"})
    private Reserva.EstadoReserva estado;

    public ReservaResponseDTO() {
    }

    public ReservaResponseDTO(String id, UsuarioResumenDTO usuario, CanchaResumenDTO cancha,
                              LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                              Reserva.EstadoReserva estado) {
        this.id = id;
        this.usuario = usuario;
        this.cancha = cancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public static ReservaResponseDTO fromEntity(Reserva reserva) {
        if (reserva == null) {
            return null;
        }
        return new ReservaResponseDTO(
                reserva.getId(),
                UsuarioResumenDTO.fromEntity(reserva.getUsuario()),
                CanchaResumenDTO.fromEntity(reserva.getCancha()),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getEstado()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UsuarioResumenDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResumenDTO usuario) {
        this.usuario = usuario;
    }

    public CanchaResumenDTO getCancha() {
        return cancha;
    }

    public void setCancha(CanchaResumenDTO cancha) {
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

    public Reserva.EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(Reserva.EstadoReserva estado) {
        this.estado = estado;
    }
}
