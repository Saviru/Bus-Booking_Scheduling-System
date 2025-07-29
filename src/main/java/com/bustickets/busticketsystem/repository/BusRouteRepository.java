package com.bustickets.busticketsystem.repository;

import com.bustickets.busticketsystem.model.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {
}