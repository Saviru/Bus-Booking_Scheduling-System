package com.busSystem.BookingSchedule.operationsManager.repository;

import com.busSystem.BookingSchedule.operationsManager.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByStatus(String status);
    
    @Query("SELECT r FROM Route r WHERE r.origin LIKE %:search% OR r.destination LIKE %:search%")
    List<Route> findByOriginOrDestinationContaining(@Param("search") String search);
    
    List<Route> findByOriginAndDestination(String origin, String destination);
}