package canchas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import canchas.model.Cancha;

@Schema(description = "Datos resumidos de la cancha en una reserva")
public class CanchaResumenDTO {

    @Schema(description = "UUID de la cancha", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
    private String id;

    @Schema(description = "Nombre de la cancha", example = "Cancha Central")
    private String nombre;

    @Schema(description = "Tipo de cancha (ej: Futbol 5, Futbol 7, Futbol 11)", example = "Futbol 5")
    private String tipo;

    @Schema(description = "Precio por hora de alquiler", example = "15000.0")
    private Double precioPorHora;

    public CanchaResumenDTO() {
    }

    public CanchaResumenDTO(String id, String nombre, String tipo, Double precioPorHora) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precioPorHora = precioPorHora;
    }

    public static CanchaResumenDTO fromEntity(Cancha cancha) {
        if (cancha == null) {
            return null;
        }
        return new CanchaResumenDTO(
                cancha.getId(),
                cancha.getNombre(),
                cancha.getTipo(),
                cancha.getPrecioPorHora()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(Double precioPorHora) {
        this.precioPorHora = precioPorHora;
    }
}
