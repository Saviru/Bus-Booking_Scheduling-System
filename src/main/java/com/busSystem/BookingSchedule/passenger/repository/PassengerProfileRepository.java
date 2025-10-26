package com.busSystem.BookingSchedule.passenger.repository;

import com.busSystem.BookingSchedule.passenger.model.PassengerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PassengerProfileRepository extends JpaRepository<PassengerProfile, Long> {
    Optional<PassengerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}