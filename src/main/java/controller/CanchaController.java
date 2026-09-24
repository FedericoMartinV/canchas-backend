package controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import model.Cancha;
import service.CanchaService;

@Tag(name = "Canchas", description = "Gestión de canchas de fútbol: alta, modificación, baja y búsqueda por tipo.")
@RestController
@RequestMapping("/api/canchas")
@CrossOrigin(origins = "*")
public class CanchaController {

	private final CanchaService canchaService;

	public CanchaController(CanchaService canchaService) {
		this.canchaService = canchaService;
	}

	@Operation(summary = "Listar todas las canchas", description = "Devuelve la lista completa de canchas registradas.")
	@ApiResponse(responseCode = "200", description = "Lista de canchas obtenida exitosamente")
	@GetMapping
	public ResponseEntity<List<Cancha>> obtenerTodas() {
		return ResponseEntity.ok(canchaService.obtenerTodas());
	}

	@Operation(summary = "Obtener cancha por ID", description = "Busca y devuelve una cancha por su UUID.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cancha encontrada"),
		@ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<Cancha> obtenerPorId(
			@Parameter(description = "UUID de la cancha", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
			@PathVariable String id) {
		return ResponseEntity.ok(canchaService.obtenerPorId(id));
	}

	@Operation(summary = "Crear cancha", description = "Registra una nueva cancha. El campo `tipo` determina su categoría (ej: `futbol5`, `futbol11`).")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Cancha creada exitosamente"),
		@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
	})
	@PostMapping
	public ResponseEntity<Cancha> crear(@Valid @RequestBody Cancha cancha) {
		return ResponseEntity.status(HttpStatus.CREATED).body(canchaService.crear(cancha));
	}

	@Operation(summary = "Actualizar cancha", description = "Actualiza todos los campos de una cancha existente.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cancha actualizada exitosamente"),
		@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
		@ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
	})
	@PutMapping("/{id}")
	public ResponseEntity<Cancha> actualizar(
			@Parameter(description = "UUID de la cancha a actualizar", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
			@PathVariable String id,
			@Valid @RequestBody Cancha cancha) {
		return ResponseEntity.ok(canchaService.actualizar(id, cancha));
	}

	@Operation(summary = "Eliminar cancha", description = "Elimina permanentemente una cancha por su UUID.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Cancha eliminada exitosamente"),
		@ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(
			@Parameter(description = "UUID de la cancha a eliminar", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
			@PathVariable String id) {
		canchaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "Buscar canchas por tipo",
		description = "Filtra canchas según su tipo. Valores típicos: `futbol5`, `futbol11`. El filtro es por coincidencia exacta (case-sensitive) según los valores cargados en la base de datos."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Lista de canchas del tipo indicado (puede ser vacía)")
	})
	@GetMapping("/tipo/{tipo}")
	public ResponseEntity<List<Cancha>> buscarPorTipo(
			@Parameter(description = "Tipo de cancha a filtrar", example = "futbol5")
			@PathVariable String tipo) {
		return ResponseEntity.ok(canchaService.buscarPorTipo(tipo));
	}
}