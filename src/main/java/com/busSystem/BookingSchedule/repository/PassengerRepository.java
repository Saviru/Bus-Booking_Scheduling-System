package com.busSystem.BookingSchedule.repository;

import com.busSystem.BookingSchedule.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    Optional<Passenger> findByUsername(String username);
    Optional<Passenger> findByEmail(String email);
    Optional<Passenger> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT p FROM Passenger p WHERE p.isActive = true")
    List<Passenger> findAllActivePassengers();
    
    @Query("SELECT p FROM Passenger p WHERE p.username = :identifier OR p.email = :identifier")
    Optional<Passenger> findByUsernameOrEmailIdentifier(@Param("identifier") String identifier);
    
    List<Passenger> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);
    
    @Query("SELECT p FROM Passenger p WHERE p.frequentDestinations LIKE %:destination%")
    List<Passenger> findByFrequentDestination(@Param("destination") String destination);
}