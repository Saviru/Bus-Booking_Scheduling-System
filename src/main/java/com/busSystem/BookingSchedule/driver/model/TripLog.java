package com.busSystem.BookingSchedule.driver.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_logs")
public class TripLog extends Model {

    @Column(name = "driver_id", nullable = false)
    private Long driverId;
    
    @Column(name = "route_id", nullable = false)
    private Long routeId;
    
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;
    
    @Column(name = "issues", columnDefinition = "TEXT")
    private String issues;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";
    
    // Constructors
    public TripLog() {}
    
    public TripLog(Long driverId, Long routeId, Long scheduleId, LocalDateTime startTime, 
                   Integer passengerCount, String issues) {
        this.driverId = driverId;
        this.routeId = routeId;
        this.scheduleId = scheduleId;
        this.startTime = startTime;
        this.passengerCount = passengerCount;
        this.issues = issues;
    }
    
    // Getters and Setters
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
    
    public String getIssues() { return issues; }
    public void setIssues(String issues) { this.issues = issues; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}