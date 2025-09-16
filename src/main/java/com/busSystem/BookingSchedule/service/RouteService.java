package com.busSystem.BookingSchedule.service;

import com.busSystem.BookingSchedule.model.Route;
import com.busSystem.BookingSchedule.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    public List<Route> getAllActiveRoutes() {
        return routeRepository.findAllActiveRoutes();
    }
    
    public Optional<Route> findById(Long id) {
        return routeRepository.findById(id);
    }
    
    public List<Route> findRoutesByOriginAndDestination(String origin, String destination) {
        return routeRepository.findActiveRoutesByOriginAndDestination(origin, destination);
    }
    
    public List<Route> searchRoutes(String search) {
        return routeRepository.searchActiveRoutes(search);
    }
    
    public List<String> getAllOrigins() {
        return routeRepository.findAllActiveOrigins();
    }
    
    public List<String> getAllDestinations() {
        return routeRepository.findAllActiveDestinations();
    }
}