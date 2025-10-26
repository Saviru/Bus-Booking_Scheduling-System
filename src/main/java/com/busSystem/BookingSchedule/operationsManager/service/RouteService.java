package com.busSystem.BookingSchedule.operationsManager.service;

import com.busSystem.BookingSchedule.operationsManager.model.Route;
import com.busSystem.BookingSchedule.operationsManager.repository.RouteRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class RouteService extends Service<Route, Long> {

    @Autowired
    private RouteRepository routeRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Route> getAll() {
        return routeRepository.findAll();
    }

    @Override
    public Optional<Route> getById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public Route save(Route route) {
        if (route.getId() == null) {
            route.setCreatedDate(LocalDateTime.now());
        }
        route.setUpdatedDate(LocalDateTime.now());
        return routeRepository.save(route);
    }

    @Override
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return routeRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Route> getRoutesByStatus(String status) {
        return routeRepository.findByStatus(status);
    }

    public List<Route> searchRoutes(String search) {
        return routeRepository.findByOriginOrDestinationContaining(search);
    }
}