package dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Datos necesarios para crear una nueva reserva")
public class ReservaRequestDTO {

    @Schema(description = "UUID del usuario que realiza la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String usuarioId;

    @Schema(description = "UUID de la cancha a reservar", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
    private String canchaId;

    @Schema(description = "Fecha de la reserva en formato ISO (YYYY-MM-DD)", example = "2026-09-20")
    private LocalDate fecha;

    @Schema(description = "Hora de inicio en formato HH:mm (24 h)", example = "18:00")
    private String horaInicio;

    @Schema(description = "Hora de fin en formato HH:mm (24 h). Debe ser posterior a horaInicio.", example = "19:00")
    private String horaFin;
    
    // Constructor vacío
    public ReservaRequestDTO() {
    }
    
    // Constructor con todos los parámetros
    public ReservaRequestDTO(String usuarioId, String canchaId, LocalDate fecha, 
                            String horaInicio, String horaFin) {
        this.usuarioId = usuarioId;
        this.canchaId = canchaId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
    
    // Getters y Setters
    public String getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getCanchaId() {
        return canchaId;
    }
    
    public void setCanchaId(String canchaId) {
        this.canchaId = canchaId;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getHoraInicio() {
        return horaInicio;
    }
    
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }
    
    public String getHoraFin() {
        return horaFin;
    }
    
    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}