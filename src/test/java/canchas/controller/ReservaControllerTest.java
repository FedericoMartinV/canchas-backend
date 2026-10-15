package canchas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(reservaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
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
    @DisplayName("Criterio: POST de reserva válida (o adyacente) retorna 201 CREATED")
    void testCrearReservaValida_RetornaCreated() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setId("res-1");
        reserva.setFecha(LocalDate.of(2026, 10, 15));
        reserva.setHoraInicio(LocalTime.of(11, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);

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
                .andExpect(jsonPath("$.horaInicio").value("11:00:00"))
                .andExpect(jsonPath("$.horaFin").value("12:00:00"));
    }
}
