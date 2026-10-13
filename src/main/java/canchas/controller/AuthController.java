package canchas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import canchas.dto.LoginRequestDTO;
import canchas.dto.LoginResponseDTO;
import canchas.model.Usuario;
import canchas.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AutenticaciÃ³n", description = "Registro e inicio de sesiÃ³n de usuarios. âš ï¸ La validaciÃ³n de contraseÃ±a aÃºn no estÃ¡ implementada (pendiente Spring Security).")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UsuarioRepository usuarioRepository;
    
    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Operation(
        summary = "Iniciar sesiÃ³n",
        description = """
            Autentica un usuario por email y devuelve sus datos bÃ¡sicos con el rol asignado.
            
            **âš ï¸ Importante:** la contraseÃ±a NO se valida en esta versiÃ³n. Cualquier usuario con
            ese email podrÃ¡ autenticarse. Si el email contiene la palabra `admin`, el rol devuelto
            serÃ¡ `ADMIN`; de lo contrario serÃ¡ `USER`.
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequestDTO.class),
                examples = @ExampleObject(
                    name = "Login usuario normal",
                    value = """
                        { "email": "juan.perez@email.com" }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve datos del usuario y rol"),
        @ApiResponse(responseCode = "500", description = "Usuario no encontrado con ese email", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Por ahora sin password validation (lo haremos despuÃ©s con Spring Security)
        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        response.setTelefono(usuario.getTelefono());
        response.setRol("USER"); // Temporal
        
        // Si el email contiene "admin", es administrador
        if (usuario.getEmail().contains("admin")) {
            response.setRol("ADMIN");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema. El email debe ser Ãºnico.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(
                    name = "Nuevo usuario",
                    value = """
                        {
                          "nombre": "Juan",
                          "apellido": "PÃ©rez",
                          "email": "juan.perez@email.com",
                          "telefono": "1234567890"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "500", description = "El email ya estÃ¡ registrado", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya estÃ¡ registrado");
        }
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }
}