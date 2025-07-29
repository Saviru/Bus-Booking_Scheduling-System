package com.bustickets.busticketsystem.repository;

import com.bustickets.busticketsystem.model.BusRoute;
import com.bustickets.busticketsystem.model.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {
    List<BusSchedule> findByBusRoute(BusRoute busRoute);
}