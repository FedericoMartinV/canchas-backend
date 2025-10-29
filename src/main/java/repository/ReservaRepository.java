package repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {
    List<Reserva> findByUsuarioId(String usuarioId);
    List<Reserva> findByCanchaId(String canchaId);
    List<Reserva> findByFecha(LocalDate fecha);
    
    @Query("SELECT r FROM Reserva r WHERE r.cancha.id = :canchaId AND r.fecha = :fecha")
    List<Reserva> findByCanchaIdAndFecha(String canchaId, LocalDate fecha);
}