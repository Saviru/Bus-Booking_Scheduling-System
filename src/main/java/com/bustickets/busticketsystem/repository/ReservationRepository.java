package com.bustickets.busticketsystem.repository;

import com.bustickets.busticketsystem.model.Reservation;
import com.bustickets.busticketsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
}