package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "canchas")
public class Cancha {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "La ubicación es obligatoria")
    @Column(nullable = false)
    private String ubicacion;
    
    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false)
    private String tipo;
    
    @Positive(message = "El precio debe ser positivo")
    @Column(nullable = false)
    private Double precioPorHora;
    
    @Column(nullable = false)
    private Boolean iluminacion;
    
    // Constructores
    public Cancha() {
    }
    
    public Cancha(String id, String nombre, String ubicacion, String tipo, 
                  Double precioPorHora, Boolean iluminacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.precioPorHora = precioPorHora;
        this.iluminacion = iluminacion;
    }
    
    // Getters y Setters
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
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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
    
    public Boolean getIluminacion() {
        return iluminacion;
    }
    
    public void setIluminacion(Boolean iluminacion) {
        this.iluminacion = iluminacion;
    }
}