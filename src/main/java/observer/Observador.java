package observer;

import model.Reserva;

public interface Observador {
    void actualizar(Reserva reserva, String evento);
}