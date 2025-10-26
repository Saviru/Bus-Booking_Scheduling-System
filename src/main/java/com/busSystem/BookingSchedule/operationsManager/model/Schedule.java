package com.busSystem.BookingSchedule.operationsManager.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "schedules")
public class Schedule extends Model {

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    @JsonBackReference
    private Route route;
    
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;
    
    @Column(name = "driver_id")
    private Long driverId;
    
    @Column(name = "ticketing_officer_id")
    private Long ticketingOfficerId;
    
    @Column(name = "bus_id")
    private Long busId;

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, CANCELLED
    
    // Constructors
    public Schedule() {}
    
    public Schedule(Route route, LocalTime departureTime, LocalTime arrivalTime, String status) {
        this.route = route;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }
    
    // Getters and Setters
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    
    public Long getTicketingOfficerId() { return ticketingOfficerId; }
    public void setTicketingOfficerId(Long ticketingOfficerId) { this.ticketingOfficerId = ticketingOfficerId; }
    
    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}