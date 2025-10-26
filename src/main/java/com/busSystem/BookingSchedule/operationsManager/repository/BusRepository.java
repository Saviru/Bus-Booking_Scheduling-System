package com.busSystem.BookingSchedule.operationsManager.repository;

import com.busSystem.BookingSchedule.operationsManager.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBusNumber(String busNumber);
    List<Bus> findByStatus(String status);
    Optional<Bus> findByAssignedDriverId(Long driverId);
    List<Bus> findByAssignedTicketingOfficerId(Long ticketingOfficerId);
}

