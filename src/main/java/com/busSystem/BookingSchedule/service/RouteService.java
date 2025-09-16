package com.busSystem.BookingSchedule.service;

import com.busSystem.BookingSchedule.model.Route;
import com.busSystem.BookingSchedule.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    // Read operations (existing)
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
    
    // Enhanced CRUD operations for Operations Manager
    
    // Create
    public boolean createRoute(Route route) {
        try {
            // Check if route code already exists
            if (routeRepository.existsByRouteCode(route.getRouteCode())) {
                return false; // Route code already exists
            }
            
            // Validate route data
            if (!isValidRoute(route)) {
                return false;
            }
            
            route.setStatus(Route.RouteStatus.ACTIVE);
            route.setCreatedAt(LocalDateTime.now());
            route.setUpdatedAt(LocalDateTime.now());
            
            routeRepository.save(route);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Read - Enhanced operations for management
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    public List<Route> getRoutesByStatus(Route.RouteStatus status) {
        return routeRepository.findByStatus(status);
    }
    
    public Optional<Route> findByRouteCode(String routeCode) {
        return routeRepository.findByRouteCode(routeCode);
    }
    
    public List<Route> searchAllRoutes(String search) {
        // Search across all routes regardless of status for management purposes
        return routeRepository.findAll().stream()
                .filter(route -> route.getOrigin().toLowerCase().contains(search.toLowerCase()) ||
                               route.getDestination().toLowerCase().contains(search.toLowerCase()) ||
                               route.getRouteCode().toLowerCase().contains(search.toLowerCase()))
                .toList();
    }
    
    // Update
    public boolean updateRoute(Route route) {
        try {
            if (route.getId() != null && routeRepository.existsById(route.getId())) {
                // Check if route code is being changed and if it conflicts
                Optional<Route> existingRoute = routeRepository.findById(route.getId());
                if (existingRoute.isPresent()) {
                    Route existing = existingRoute.get();
                    if (!existing.getRouteCode().equals(route.getRouteCode()) &&
                        routeRepository.existsByRouteCode(route.getRouteCode())) {
                        return false; // Route code conflict
                    }
                }
                
                if (!isValidRoute(route)) {
                    return false;
                }
                
                route.setUpdatedAt(LocalDateTime.now());
                routeRepository.save(route);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean updateRouteStatus(Long id, Route.RouteStatus status) {
        Optional<Route> route = routeRepository.findById(id);
        if (route.isPresent()) {
            route.get().setStatus(status);
            route.get().setUpdatedAt(LocalDateTime.now());
            routeRepository.save(route.get());
            return true;
        }
        return false;
    }
    
    // Soft Delete
    public boolean deactivateRoute(Long id) {
        return updateRouteStatus(id, Route.RouteStatus.INACTIVE);
    }
    
    public boolean suspendRoute(Long id) {
        return updateRouteStatus(id, Route.RouteStatus.SUSPENDED);
    }
    
    public boolean reactivateRoute(Long id) {
        return updateRouteStatus(id, Route.RouteStatus.ACTIVE);
    }
    
    // Hard Delete (for testing purposes only)
    public boolean deleteRoute(Long id) {
        try {
            if (routeRepository.existsById(id)) {
                routeRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Bulk operations
    public boolean bulkUpdateStatus(List<Long> routeIds, Route.RouteStatus status) {
        try {
            for (Long id : routeIds) {
                updateRouteStatus(id, status);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Validation
    private boolean isValidRoute(Route route) {
        if (route == null) return false;
        if (route.getRouteCode() == null || route.getRouteCode().trim().isEmpty()) return false;
        if (route.getOrigin() == null || route.getOrigin().trim().isEmpty()) return false;
        if (route.getDestination() == null || route.getDestination().trim().isEmpty()) return false;
        if (route.getDistance() == null || route.getDistance() <= 0) return false;
        if (route.getDuration() == null || route.getDuration() <= 0) return false;
        if (route.getBaseFare() == null || route.getBaseFare().doubleValue() <= 0) return false;
        
        // Validate origin != destination
        if (route.getOrigin().trim().equalsIgnoreCase(route.getDestination().trim())) {
            return false;
        }
        
        return true;
    }
    
    // Validation helpers
    public boolean isRouteCodeAvailable(String routeCode) {
        return !routeRepository.existsByRouteCode(routeCode);
    }
    
    public boolean isRouteCodeAvailable(String routeCode, Long excludeId) {
        Optional<Route> existing = routeRepository.findByRouteCode(routeCode);
        return existing.isEmpty() || existing.get().getId().equals(excludeId);
    }
    
    // Conflict validation
    public boolean hasConflicts(Route route) {
        // Check for routes with same origin and destination
        List<Route> conflictingRoutes = routeRepository
            .findActiveRoutesByOriginAndDestination(route.getOrigin(), route.getDestination());
        
        // If updating existing route, exclude it from conflict check
        if (route.getId() != null) {
            return conflictingRoutes.stream().anyMatch(r -> !r.getId().equals(route.getId()));
        }
        
        return !conflictingRoutes.isEmpty();
    }
    
    // Statistics
    public long getTotalRoutes() {
        return routeRepository.count();
    }
    
    public long getActiveRouteCount() {
        return routeRepository.findByStatus(Route.RouteStatus.ACTIVE).size();
    }
    
    public long getInactiveRouteCount() {
        return routeRepository.findByStatus(Route.RouteStatus.INACTIVE).size();
    }
    
    public long getSuspendedRouteCount() {
        return routeRepository.findByStatus(Route.RouteStatus.SUSPENDED).size();
    }
}