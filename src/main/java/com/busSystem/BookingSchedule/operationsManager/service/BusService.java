package com.busSystem.BookingSchedule.operationsManager.service;

import com.busSystem.BookingSchedule.operationsManager.model.Bus;
import com.busSystem.BookingSchedule.operationsManager.repository.BusRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class BusService extends Service<Bus, Long> {

    @Autowired
    private BusRepository busRepository;

    @Override
    public List<Bus> getAll() {
        return busRepository.findAll();
    }

    @Override
    public Optional<Bus> getById(Long id) {
        return busRepository.findById(id);
    }

    @Override
    public Bus save(Bus bus) {
        return busRepository.save(bus);
    }

    @Override
    public void delete(Long id) {
        busRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return busRepository.existsById(id);
    }

    // Business logic methods
    public Optional<Bus> getBusByBusNumber(String busNumber) {
        return busRepository.findByBusNumber(busNumber);
    }

    public List<Bus> getBusesByStatus(String status) {
        return busRepository.findByStatus(status);
    }

    public Optional<Bus> getBusByDriverId(Long driverId) {
        return busRepository.findByAssignedDriverId(driverId);
    }

    public List<Bus> getBusesByTicketingOfficerId(Long ticketingOfficerId) {
        return busRepository.findByAssignedTicketingOfficerId(ticketingOfficerId);
    }
}
