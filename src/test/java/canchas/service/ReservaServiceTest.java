package canchas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import canchas.exception.HorarioInvalidoException;
import canchas.exception.ReservaSolapadaException;
import canchas.model.Cancha;
import canchas.model.Reserva;
import canchas.model.Usuario;
import canchas.observer.AdministradorNotificador;
import canchas.observer.UsuarioNotificador;
import canchas.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CanchaService canchaService;

    @Mock
    private UsuarioNotificador usuarioNotificador;

    @Mock
    private AdministradorNotificador administradorNotificador;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuarioMock;
    private Cancha canchaMock;
    private LocalDate fechaPrueba;
    private final String usuarioId = "usr-1";
    private final String canchaId = "cancha-1";

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);

        canchaMock = new Cancha();
        canchaMock.setId(canchaId);

        fechaPrueba = LocalDate.of(2026, 10, 15);
    }

    private Reserva crearReservaExistente(String inicio, String fin, Reserva.EstadoReserva estado) {
        Reserva r = new Reserva();
        r.setCancha(canchaMock);
        r.setUsuario(usuarioMock);
        r.setFecha(fechaPrueba);
        r.setHoraInicio(LocalTime.parse(inicio));
        r.setHoraFin(LocalTime.parse(fin));
        r.setEstado(estado);
        return r;
    }

    @Test
    @DisplayName("Criterio 1: Solapamiento total -> debe ser rechazado con ReservaSolapadaException")
    void testSolapamientoTotal_DebeLanzarExcepcion() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva existente = crearReservaExistente("20:00", "21:00", Reserva.EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(existente));

        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "20:00", "21:00")
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterio 1: Solapamiento parcial por el inicio -> debe ser rechazado con ReservaSolapadaException")
    void testSolapamientoParcialInicio_DebeLanzarExcepcion() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva existente = crearReservaExistente("20:00", "21:00", Reserva.EstadoReserva.PENDIENTE);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(existente));

        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "19:30", "20:30")
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterio 1: Solapamiento parcial por el fin -> debe ser rechazado con ReservaSolapadaException")
    void testSolapamientoParcialFin_DebeLanzarExcepcion() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva existente = crearReservaExistente("20:00", "21:00", Reserva.EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(existente));

        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "20:30", "21:30")
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterio 1: Contención completa (nueva contiene a existente y viceversa) -> debe ser rechazado")
    void testContencionCompleta_DebeLanzarExcepcion() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva existente = crearReservaExistente("20:00", "21:00", Reserva.EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(existente));

        // Caso A: La nueva reserva contiene completamente a la existente
        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "19:00", "22:00")
        );

        // Caso B: La nueva reserva está completamente contenida dentro de la existente
        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "20:15", "20:45")
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterio 2: Rango invertido o igual (horaFin <= horaInicio) -> debe ser rechazado con HorarioInvalidoException")
    void testRangoInvertidoOIgual_DebeLanzarExcepcion() {
        // Hora fin anterior a hora inicio
        assertThrows(HorarioInvalidoException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "21:00", "20:00")
        );

        // Hora fin igual a hora inicio
        assertThrows(HorarioInvalidoException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "20:00", "20:00")
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criterio 3: Horarios adyacentes exactos (10-11 y 11-12) -> deben ser aceptados ambos")
    void testAdyacenciaExacta_DebeSerAceptada() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva existente = crearReservaExistente("10:00", "11:00", Reserva.EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(existente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Reserva adyacente posterior: 11:00 a 12:00
        Reserva nuevaPosterior = reservaService.crear(usuarioId, canchaId, fechaPrueba, "11:00", "12:00");
        assertNotNull(nuevaPosterior);
        assertEquals(LocalTime.of(11, 0), nuevaPosterior.getHoraInicio());
        assertEquals(LocalTime.of(12, 0), nuevaPosterior.getHoraFin());

        // Reserva adyacente anterior: 09:00 a 10:00
        Reserva nuevaAnterior = reservaService.crear(usuarioId, canchaId, fechaPrueba, "09:00", "10:00");
        assertNotNull(nuevaAnterior);
        assertEquals(LocalTime.of(9, 0), nuevaAnterior.getHoraInicio());
        assertEquals(LocalTime.of(10, 0), nuevaAnterior.getHoraFin());
    }

    @Test
    @DisplayName("Criterio 4: Reserva CANCELADA previa no impide crear otra reserva en la misma franja")
    void testReservaCancelada_NoImpideNuevaReserva() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva cancelada = crearReservaExistente("20:00", "21:00", Reserva.EstadoReserva.CANCELADA);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(cancelada));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva nueva = reservaService.crear(usuarioId, canchaId, fechaPrueba, "20:00", "21:00");
        assertNotNull(nueva);
        assertEquals(Reserva.EstadoReserva.PENDIENTE, nueva.getEstado());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Caso específico: Cancha con reserva 18:00 a 19:00 permite reservar de 19:00 en adelante y bloquea franja 18:00 a 18:59")
    void testReservaCancha18a19_PermiteDesde19YBloqueaSuperposicion() {
        when(usuarioService.obtenerPorId(usuarioId)).thenReturn(usuarioMock);
        when(canchaService.obtenerPorId(canchaId)).thenReturn(canchaMock);

        Reserva existente18a19 = crearReservaExistente("18:00", "19:00", Reserva.EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByCanchaIdAndFecha(canchaId, fechaPrueba))
                .thenReturn(List.of(existente18a19));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 1. Permitir desde las 19:00 en adelante (ej: 19:00 a 20:00)
        Reserva reservaPosterior = reservaService.crear(usuarioId, canchaId, fechaPrueba, "19:00", "20:00");
        assertNotNull(reservaPosterior);
        assertEquals(LocalTime.of(19, 0), reservaPosterior.getHoraInicio());
        assertEquals(LocalTime.of(20, 0), reservaPosterior.getHoraFin());

        // 2. Bloquear cualquier horario que caiga dentro de 18:00 a 18:59 (ej: 18:30 a 19:30)
        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "18:30", "19:30")
        );

        // 3. Bloquear exactamente 18:00 a 18:59 (solapamiento interno)
        assertThrows(ReservaSolapadaException.class, () ->
                reservaService.crear(usuarioId, canchaId, fechaPrueba, "18:00", "18:59")
        );
    }
}
