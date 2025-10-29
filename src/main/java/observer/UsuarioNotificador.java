package observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import model.Reserva;

@Component
public class UsuarioNotificador implements Observador {
    
    private static final Logger log = LoggerFactory.getLogger(UsuarioNotificador.class);
    
    @Override
    public void actualizar(Reserva reserva, String evento) {
        log.info("Email Notificacion al Usuario: {} {} - Reserva {} para cancha '{}' el {} de {}:00 a {}:00",
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getApellido(),
                evento,
                reserva.getCancha().getNombre(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin());
        
        enviarEmail(reserva, evento);
    }
    
    private void enviarEmail(Reserva reserva, String evento) {
        String mensaje = String.format(
            "Hola %s,\n\nTu reserva ha sido %s.\n" +
            "Cancha: %s\nFecha: %s\nHorario: %s - %s\n\nNos vemos en la cancha!",
            reserva.getUsuario().getNombre(),
            evento.toLowerCase(),
            reserva.getCancha().getNombre(),
            reserva.getFecha(),
            reserva.getHoraInicio(),
            reserva.getHoraFin()
        );
        log.debug("Email enviado a {}: {}", reserva.getUsuario().getEmail(), mensaje);
    }
}