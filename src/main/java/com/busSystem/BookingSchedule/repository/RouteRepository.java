package com.busSystem.BookingSchedule.repository;

import com.busSystem.BookingSchedule.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Optional<Route> findByRouteCode(String routeCode);
    
    List<Route> findByStatus(Route.RouteStatus status);
    
    @Query("SELECT r FROM Route r WHERE r.status = 'ACTIVE'")
    List<Route> findAllActiveRoutes();
    
    @Query("SELECT r FROM Route r WHERE r.origin = :origin AND r.destination = :destination AND r.status = 'ACTIVE'")
    List<Route> findActiveRoutesByOriginAndDestination(@Param("origin") String origin, 
                                                      @Param("destination") String destination);
    
    @Query("SELECT r FROM Route r WHERE (r.origin LIKE %:search% OR r.destination LIKE %:search%) AND r.status = 'ACTIVE'")
    List<Route> searchActiveRoutes(@Param("search") String search);
    
    @Query("SELECT DISTINCT r.origin FROM Route r WHERE r.status = 'ACTIVE' ORDER BY r.origin")
    List<String> findAllActiveOrigins();
    
    @Query("SELECT DISTINCT r.destination FROM Route r WHERE r.status = 'ACTIVE' ORDER BY r.destination")
    List<String> findAllActiveDestinations();
    
    boolean existsByRouteCode(String routeCode);
}