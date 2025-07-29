package com.bustickets.busticketsystem.service;

import com.bustickets.busticketsystem.model.BusRoute;
import com.bustickets.busticketsystem.model.BusSchedule;
import com.bustickets.busticketsystem.repository.BusScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BusScheduleService {

    @Autowired
    private BusScheduleRepository busScheduleRepository;

    public List<BusSchedule> getAllSchedules() {
        return busScheduleRepository.findAll();
    }

    public List<BusSchedule> getSchedulesByRoute(BusRoute busRoute) {
        return busScheduleRepository.findByBusRoute(busRoute);
    }

    public BusSchedule saveSchedule(BusSchedule busSchedule) {
        return busScheduleRepository.save(busSchedule);
    }

    public Optional<BusSchedule> getScheduleById(Long id) {
        return busScheduleRepository.findById(id);
    }
}