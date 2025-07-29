package com.bustickets.busticketsystem.service;

import com.bustickets.busticketsystem.model.BusRoute;
import com.bustickets.busticketsystem.repository.BusRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BusRouteService {

    @Autowired
    private BusRouteRepository busRouteRepository;

    public List<BusRoute> getAllRoutes() {
        return busRouteRepository.findAll();
    }

    public BusRoute saveRoute(BusRoute busRoute) {
        return busRouteRepository.save(busRoute);
    }

    public Optional<BusRoute> getRouteById(Long id) {
        return busRouteRepository.findById(id);
    }
}