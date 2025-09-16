package com.busSystem.BookingSchedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Route code is required")
    @Column(name = "route_code", unique = true, nullable = false)
    private String routeCode;
    
    @NotBlank(message = "Origin is required")
    @Column(nullable = false)
    private String origin;
    
    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;
    
    @Column(columnDefinition = "TEXT")
    private String stops; // JSON or comma-separated
    
    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distance; // in kilometers
    
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration; // in minutes
    
    @NotNull(message = "Base fare is required")
    @Positive(message = "Base fare must be positive")
    @Column(name = "base_fare", precision = 10, scale = 2)
    private BigDecimal baseFare;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status = RouteStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;
    
    public enum RouteStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
    
    // Constructors
    public Route() {}
    
    public Route(String routeCode, String origin, String destination, 
                Double distance, Integer duration, BigDecimal baseFare) {
        this.routeCode = routeCode;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
        this.baseFare = baseFare;
        this.status = RouteStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public String getStops() { return stops; }
    public void setStops(String stops) { this.stops = stops; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    
    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
    
    public String getRouteDescription() {
        return origin + " → " + destination;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", routeCode='" + routeCode + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", status=" + status +
                '}';
    }
}