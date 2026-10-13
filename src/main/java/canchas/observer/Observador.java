package canchas.observer;

import canchas.model.Reserva;

public interface Observador {
    void actualizar(Reserva reserva, String evento);
}