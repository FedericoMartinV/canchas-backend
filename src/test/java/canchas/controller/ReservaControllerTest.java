package canchas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import canchas.exception.GlobalExceptionHandler;
import canchas.exception.HorarioInvalidoException;
import canchas.exception.ReservaSolapadaException;
import canchas.model.Cancha;
import canchas.model.Reserva;
import canchas.model.Usuario;
import canchas.service.ReservaService;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController reservaController;

    private Usuario usuarioMock;
    private Cancha canchaMock;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(reservaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        usuarioMock = new Usuario("usr-1", "Carlos", "Rodriguez", "carlos@example.com", "261-1234567");
        canchaMock = new Cancha("cancha-1", "Cancha Central", "Av. San Martin 500", "Futbol 5", 15000.0, true);
    }

    private Reserva crearReservaMock(String id, Reserva.EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setUsuario(usuarioMock);
        reserva.setCancha(canchaMock);
        reserva.setFecha(LocalDate.of(2026, 10, 15));
        reserva.setHoraInicio(LocalTime.of(11, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));
        reserva.setEstado(estado);
        return reserva;
    }

    @Test
    @DisplayName("Criterio: Dos POST con horarios cruzados -> el segundo es rechazado con 400 y mensaje claro")
    void testCrearReservaSolapada_RetornaBadRequest() throws Exception {
        when(reservaService.crear(eq("usr-1"), eq("cancha-1"), any(LocalDate.class), eq("20:00"), eq("21:00")))
                .thenThrow(new ReservaSolapadaException("La cancha ya tiene una reserva activa que se solapa con el horario solicitado (20:00 - 21:00)"));

        String bodyJson = """
                {
                    "usuarioId": "usr-1",
                    "canchaId": "cancha-1",
                    "fecha": "2026-10-15",
                    "horaInicio": "20:00",
                    "horaFin": "21:00"
                }
                """;

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("La cancha ya tiene una reserva activa que se solapa con el horario solicitado (20:00 - 21:00)"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Criterio: Un POST con horaFin anterior o igual a horaInicio es rechazado con 400")
    void testCrearReservaHorarioInvalido_RetornaBadRequest() throws Exception {
        when(reservaService.crear(eq("usr-1"), eq("cancha-1"), any(LocalDate.class), eq("21:00"), eq("20:00")))
                .thenThrow(new HorarioInvalidoException("La hora de fin (20:00) debe ser posterior a la hora de inicio (21:00)"));

        String bodyJson = """
                {
                    "usuarioId": "usr-1",
                    "canchaId": "cancha-1",
                    "fecha": "2026-10-15",
                    "horaInicio": "21:00",
                    "horaFin": "20:00"
                }
                """;

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("La hora de fin (20:00) debe ser posterior a la hora de inicio (21:00)"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Criterio: POST de reserva válida retorna 201 CREATED con ReservaResponseDTO y sin observadores")
    void testCrearReservaValida_RetornaCreated() throws Exception {
        Reserva reserva = crearReservaMock("res-1", Reserva.EstadoReserva.PENDIENTE);

        when(reservaService.crear(eq("usr-1"), eq("cancha-1"), any(LocalDate.class), eq("11:00"), eq("12:00")))
                .thenReturn(reserva);

        String bodyJson = """
                {
                    "usuarioId": "usr-1",
                    "canchaId": "cancha-1",
                    "fecha": "2026-10-15",
                    "horaInicio": "11:00",
                    "horaFin": "12:00"
                }
                """;

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("res-1"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.fecha").value("2026-10-15"))
                .andExpect(jsonPath("$.horaInicio").value("11:00:00"))
                .andExpect(jsonPath("$.horaFin").value("12:00:00"))
                .andExpect(jsonPath("$.usuario.id").value("usr-1"))
                .andExpect(jsonPath("$.usuario.nombre").value("Carlos"))
                .andExpect(jsonPath("$.usuario.email").value("carlos@example.com"))
                .andExpect(jsonPath("$.cancha.id").value("cancha-1"))
                .andExpect(jsonPath("$.cancha.nombre").value("Cancha Central"))
                .andExpect(jsonPath("$.cancha.tipo").value("Futbol 5"))
                .andExpect(jsonPath("$.cancha.precioPorHora").value(15000.0))
                .andExpect(jsonPath("$.observadores").doesNotExist());
    }

    @Test
    @DisplayName("Criterio: GET /api/reservas devuelve lista de ReservaResponseDTO sin observadores")
    void testObtenerTodas_RetornaListaDTO() throws Exception {
        when(reservaService.obtenerTodas()).thenReturn(List.of(crearReservaMock("res-1", Reserva.EstadoReserva.CONFIRMADA)));

        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res-1"))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$[0].usuario.id").value("usr-1"))
                .andExpect(jsonPath("$[0].cancha.id").value("cancha-1"))
                .andExpect(jsonPath("$[0].observadores").doesNotExist());
    }

    @Test
    @DisplayName("Criterio: PATCH /api/reservas/{id}/confirmar devuelve DTO con estado CONFIRMADA")
    void testConfirmarReserva_RetornaDTOActualizado() throws Exception {
        Reserva confirmada = crearReservaMock("res-1", Reserva.EstadoReserva.CONFIRMADA);
        when(reservaService.confirmarReserva("res-1")).thenReturn(confirmada);

        mockMvc.perform(patch("/api/reservas/res-1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-1"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.observadores").doesNotExist());
    }

    @Test
    @DisplayName("Criterio: PATCH /api/reservas/{id}/cancelar devuelve DTO con estado CANCELADA")
    void testCancelarReserva_RetornaDTOActualizado() throws Exception {
        Reserva cancelada = crearReservaMock("res-1", Reserva.EstadoReserva.CANCELADA);
        when(reservaService.cancelarReserva("res-1")).thenReturn(cancelada);

        mockMvc.perform(patch("/api/reservas/res-1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-1"))
                .andExpect(jsonPath("$.estado").value("CANCELADA"))
                .andExpect(jsonPath("$.observadores").doesNotExist());
    }

    @Test
    @DisplayName("Criterio: GET /api/reservas/usuario/{id} devuelve lista de DTOs")
    void testObtenerPorUsuario_RetornaListaDTO() throws Exception {
        when(reservaService.obtenerPorUsuario("usr-1")).thenReturn(List.of(crearReservaMock("res-1", Reserva.EstadoReserva.PENDIENTE)));

        mockMvc.perform(get("/api/reservas/usuario/usr-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res-1"))
                .andExpect(jsonPath("$[0].observadores").doesNotExist());
    }

    @Test
    @DisplayName("Criterio: GET /api/reservas/cancha/{id} devuelve lista de DTOs")
    void testObtenerPorCancha_RetornaListaDTO() throws Exception {
        when(reservaService.obtenerPorCancha("cancha-1")).thenReturn(List.of(crearReservaMock("res-1", Reserva.EstadoReserva.PENDIENTE)));

        mockMvc.perform(get("/api/reservas/cancha/cancha-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res-1"))
                .andExpect(jsonPath("$[0].observadores").doesNotExist());
    }
}
