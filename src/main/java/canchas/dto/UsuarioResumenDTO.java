package canchas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import canchas.model.Usuario;

@Schema(description = "Datos resumidos del usuario en una reserva")
public class UsuarioResumenDTO {

    @Schema(description = "UUID del usuario", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "Nombre del usuario", example = "Carlos")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Rodriguez")
    private String apellido;

    @Schema(description = "Email del usuario", example = "carlos@example.com")
    private String email;

    public UsuarioResumenDTO() {
    }

    public UsuarioResumenDTO(String id, String nombre, String apellido, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public static UsuarioResumenDTO fromEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResumenDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail()
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
