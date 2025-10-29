package dto;

import java.time.LocalDate;

public class ReservaRequestDTO {
    private String usuarioId;
    private String canchaId;
    private LocalDate fecha;
    private String horaInicio;
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