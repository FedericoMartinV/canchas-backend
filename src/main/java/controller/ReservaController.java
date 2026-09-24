package controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.ReservaRequestDTO;
import model.Reserva;
import service.ReservaService;

@Tag(name = "Reservas", description = "Gestión de reservas de canchas. Confirmar o cancelar una reserva activa el patrón Observer, notificando al usuario y al administrador.")
@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    @Operation(summary = "Listar todas las reservas", description = "Devuelve la lista completa de reservas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }
    
    @Operation(summary = "Obtener reserva por ID", description = "Busca y devuelve una reserva específica por su UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(
            @Parameter(description = "UUID de la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }
    
    @Operation(
        summary = "Crear una nueva reserva",
        description = """
            Crea una reserva en estado **PENDIENTE**.
            
            - `fecha`: formato ISO `YYYY-MM-DD`
            - `horaInicio` / `horaFin`: formato `HH:mm` (24 h), ej: `"18:00"`, `"19:30"`
            - `usuarioId` y `canchaId`: UUIDs existentes en la base de datos
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservaRequestDTO.class),
                examples = @ExampleObject(
                    name = "Ejemplo reserva fútbol 5",
                    summary = "Reserva de una hora en horario nocturno",
                    value = """
                        {
                          "usuarioId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                          "canchaId":  "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                          "fecha":     "2026-09-20",
                          "horaInicio": "18:00",
                          "horaFin":    "19:00"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario o cancha no encontrados", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaRequestDTO request) {
        Reserva reserva = reservaService.crear(
            request.getUsuarioId(),
            request.getCanchaId(),
            request.getFecha(),
            request.getHoraInicio(),
            request.getHoraFin()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    
    @Operation(
        summary = "Confirmar reserva",
        description = "Cambia el estado de la reserva a **CONFIRMADA**. Dispara el patrón Observer: notifica al usuario por email y registra la actividad para el administrador."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva confirmada y notificaciones enviadas"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Reserva> confirmar(
            @Parameter(description = "UUID de la reserva a confirmar", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String id) {
        return ResponseEntity.ok(reservaService.confirmarReserva(id));
    }
    
    @Operation(
        summary = "Cancelar reserva",
        description = "Cambia el estado de la reserva a **CANCELADA**. Dispara el patrón Observer: notifica al usuario por email y registra la actividad para el administrador."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva cancelada y notificaciones enviadas"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(
            @Parameter(description = "UUID de la reserva a cancelar", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }
    
    @Operation(summary = "Eliminar reserva", description = "Elimina permanentemente una reserva por su UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID de la reserva a eliminar", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Listar reservas por usuario", description = "Devuelve todas las reservas asociadas a un usuario específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerPorUsuario(
            @Parameter(description = "UUID del usuario", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String usuarioId) {
        return ResponseEntity.ok(reservaService.obtenerPorUsuario(usuarioId));
    }
    
    @Operation(summary = "Listar reservas por cancha", description = "Devuelve todas las reservas registradas para una cancha específica.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reservas de la cancha"),
        @ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
    })
    @GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<Reserva>> obtenerPorCancha(
            @Parameter(description = "UUID de la cancha", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
            @PathVariable String canchaId) {
        return ResponseEntity.ok(reservaService.obtenerPorCancha(canchaId));
    }
}