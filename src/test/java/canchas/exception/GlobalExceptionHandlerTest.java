package canchas.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("ReservaSolapadaException retorna HttpStatus.BAD_REQUEST con mensaje y timestamp")
    void testHandleReservaSolapadaException() {
        ReservaSolapadaException ex = new ReservaSolapadaException("La cancha ya tiene una reserva activa");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDomainValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("La cancha ya tiene una reserva activa", response.getBody().get("mensaje"));
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("HorarioInvalidoException retorna HttpStatus.BAD_REQUEST con mensaje y timestamp")
    void testHandleHorarioInvalidoException() {
        HorarioInvalidoException ex = new HorarioInvalidoException("La hora de fin debe ser posterior a la de inicio");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDomainValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("La hora de fin debe ser posterior a la de inicio", response.getBody().get("mensaje"));
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
