package com.busSystem.BookingSchedule.operationManager.repository;

import com.busSystem.BookingSchedule.operationManager.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByStatus(String status);
    
    @Query("SELECT r FROM Route r WHERE r.origin LIKE %:location% OR r.destination LIKE %:location% OR r.stops LIKE %:location%")
    List<Route> findByLocation(@Param("location") String location);
    
    boolean existsByRouteId(String routeId);
}