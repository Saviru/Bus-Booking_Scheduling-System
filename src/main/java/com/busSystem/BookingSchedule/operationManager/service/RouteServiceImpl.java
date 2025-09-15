package com.busSystem.BookingSchedule.operationManager.service;

import com.busSystem.BookingSchedule.operationManager.model.Route;
import com.busSystem.BookingSchedule.operationManager.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    
    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }
    
    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    @Override
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }
    
    @Override
    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }
    
    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
    
    @Override
    public List<Route> getRoutesByStatus(String status) {
        return routeRepository.findByStatus(status);
    }
    
    @Override
    public List<Route> searchRoutesByLocation(String location) {
        return routeRepository.findByLocation(location);
    }
    
    @Override
    public boolean isRouteIdExists(String routeId) {
        return routeRepository.existsByRouteId(routeId);
    }
}