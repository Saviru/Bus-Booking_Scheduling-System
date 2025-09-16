package com.busSystem.BookingSchedule.repository;

import com.busSystem.BookingSchedule.model.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Optional<Route> findByRouteCode(String routeCode);
    
    List<Route> findByStatus(Route.RouteStatus status);
    
    @Query("SELECT r FROM Route r WHERE r.status = 'ACTIVE'")
    List<Route> findAllActiveRoutes();
    
    @Query("SELECT r FROM Route r WHERE r.origin = :origin AND r.destination = :destination AND r.status = 'ACTIVE'")
    List<Route> findActiveRoutesByOriginAndDestination(@Param("origin") String origin, 
                                                      @Param("destination") String destination);
    
    @Query("SELECT r FROM Route r WHERE (r.origin LIKE %:search% OR r.destination LIKE %:search%) AND r.status = 'ACTIVE'")
    List<Route> searchActiveRoutes(@Param("search") String search);
    
    @Query("SELECT DISTINCT r.origin FROM Route r WHERE r.status = 'ACTIVE' ORDER BY r.origin")
    List<String> findAllActiveOrigins();
    
    @Query("SELECT DISTINCT r.destination FROM Route r WHERE r.status = 'ACTIVE' ORDER BY r.destination")
    List<String> findAllActiveDestinations();
    
    boolean existsByRouteCode(String routeCode);
    
    // Enhanced queries for Operations Manager
    
    @Query("SELECT r FROM Route r WHERE r.origin = :origin AND r.destination = :destination")
    List<Route> findAllRoutesByOriginAndDestination(@Param("origin") String origin, 
                                                   @Param("destination") String destination);
    
    @Query("SELECT r FROM Route r WHERE r.origin LIKE %:search% OR r.destination LIKE %:search% OR r.routeCode LIKE %:search%")
    List<Route> searchAllRoutes(@Param("search") String search);
    
    @Query("SELECT r FROM Route r WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<Route> findByStatusOrderByCreatedAtDesc(@Param("status") Route.RouteStatus status);
    
    @Query("SELECT r FROM Route r ORDER BY r.createdAt DESC")
    List<Route> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT r FROM Route r WHERE r.origin = :origin ORDER BY r.destination")
    List<Route> findByOriginOrderByDestination(@Param("origin") String origin);
    
    @Query("SELECT r FROM Route r WHERE r.destination = :destination ORDER BY r.origin")
    List<Route> findByDestinationOrderByOrigin(@Param("destination") String destination);
    
    // Statistical queries
    @Query("SELECT COUNT(r) FROM Route r WHERE r.status = 'ACTIVE'")
    long countActiveRoutes();
    
    @Query("SELECT COUNT(r) FROM Route r WHERE r.status = 'INACTIVE'")
    long countInactiveRoutes();
    
    @Query("SELECT COUNT(r) FROM Route r WHERE r.status = 'SUSPENDED'")
    long countSuspendedRoutes();
    
    @Query("SELECT DISTINCT r.origin FROM Route r ORDER BY r.origin")
    List<String> findAllOrigins();
    
    @Query("SELECT DISTINCT r.destination FROM Route r ORDER BY r.destination")
    List<String> findAllDestinations();
    
    @Query("SELECT r FROM Route r WHERE r.distance BETWEEN :minDistance AND :maxDistance")
    List<Route> findByDistanceRange(@Param("minDistance") Double minDistance, 
                                   @Param("maxDistance") Double maxDistance);
    
    @Query("SELECT r FROM Route r WHERE r.duration BETWEEN :minDuration AND :maxDuration")
    List<Route> findByDurationRange(@Param("minDuration") Integer minDuration, 
                                   @Param("maxDuration") Integer maxDuration);
    
    // Paginated queries for large datasets
    Page<Route> findAllByStatus(Route.RouteStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Route r WHERE r.origin LIKE %:search% OR r.destination LIKE %:search% OR r.routeCode LIKE %:search%")
    Page<Route> searchAllRoutes(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT r FROM Route r WHERE r.status = :status AND (r.origin LIKE %:search% OR r.destination LIKE %:search% OR r.routeCode LIKE %:search%)")
    Page<Route> searchRoutesByStatus(@Param("status") Route.RouteStatus status, 
                                    @Param("search") String search, 
                                    Pageable pageable);
    
    // Advanced filtering
    @Query("SELECT r FROM Route r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:origin IS NULL OR r.origin = :origin) AND " +
           "(:destination IS NULL OR r.destination = :destination) AND " +
           "(:minDistance IS NULL OR r.distance >= :minDistance) AND " +
           "(:maxDistance IS NULL OR r.distance <= :maxDistance)")
    List<Route> findRoutesWithFilters(@Param("status") Route.RouteStatus status,
                                     @Param("origin") String origin,
                                     @Param("destination") String destination,
                                     @Param("minDistance") Double minDistance,
                                     @Param("maxDistance") Double maxDistance);
}