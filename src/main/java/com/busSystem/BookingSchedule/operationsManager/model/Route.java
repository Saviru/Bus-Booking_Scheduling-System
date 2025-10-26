package com.busSystem.BookingSchedule.operationsManager.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route extends Model {

    @Column(nullable = false)
    private String origin;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(columnDefinition = "TEXT")
    private String stops;
    
    @Column(nullable = false)
    private Double distance;
    
    @Column(nullable = false)
    private Integer duration; // in minutes
    
    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Schedule> schedules;
    
    // Constructors
    public Route() {}
    
    public Route(String origin, String destination, String stops, Double distance, Integer duration, String status) {
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
        this.distance = distance;
        this.duration = duration;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
}