package observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import model.Reserva;

@Component
public class AdministradorNotificador implements Observador {
    
    private static final Logger log = LoggerFactory.getLogger(AdministradorNotificador.class);
    
    @Override
    public void actualizar(Reserva reserva, String evento) {
        log.info("Notificacion al Administrador: Reserva {} - ID: {}, Usuario: {}, Cancha: {}",
                evento,
                reserva.getId(),
                reserva.getUsuario().getEmail(),
                reserva.getCancha().getNombre());
        
        registrarEnDashboard(reserva, evento);
    }
    
    private void registrarEnDashboard(Reserva reserva, String evento) {
        log.debug("Dashboard actualizado: Nueva actividad de reserva {}", evento);
    }
}