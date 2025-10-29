package controller;

import java.util.List;

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

@RestController
@RequestMapping("/api/canchas")
@CrossOrigin(origins = "*")
public class CanchaController {

	private final CanchaService canchaService;

	public CanchaController(CanchaService canchaService) {
		this.canchaService = canchaService;
	}

	@GetMapping
	public ResponseEntity<List<Cancha>> obtenerTodas() {
		return ResponseEntity.ok(canchaService.obtenerTodas());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Cancha> obtenerPorId(@PathVariable String id) {
		return ResponseEntity.ok(canchaService.obtenerPorId(id));
	}

	@PostMapping
	public ResponseEntity<Cancha> crear(@Valid @RequestBody Cancha cancha) {
		return ResponseEntity.status(HttpStatus.CREATED).body(canchaService.crear(cancha));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Cancha> actualizar(@PathVariable String id, @Valid @RequestBody Cancha cancha) {
		return ResponseEntity.ok(canchaService.actualizar(id, cancha));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable String id) {
		canchaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/tipo/{tipo}")
	public ResponseEntity<List<Cancha>> buscarPorTipo(@PathVariable String tipo) {
		return ResponseEntity.ok(canchaService.buscarPorTipo(tipo));
	}
}