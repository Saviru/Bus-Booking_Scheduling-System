package com.busSystem.BookingSchedule.operationsManager.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "buses")
public class Bus extends Model {

    @Column(nullable = false, unique = true)
    private String busNumber;

    @Column(nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private Integer totalSeats = 60;

    @Column(nullable = false)
    private String busType; // STANDARD, LUXURY, SLEEPER

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, MAINTENANCE

    @Column(name = "assigned_driver_id")
    private Long assignedDriverId;

    @Column(name = "assigned_ticketing_officer_id")
    private Long assignedTicketingOfficerId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public Bus() {}

    public Bus(String busNumber, String registrationNumber, String busType, String status) {
        this.busNumber = busNumber;
        this.registrationNumber = registrationNumber;
        this.busType = busType;
        this.status = status;
        this.totalSeats = 60;
    }

    // Getters and Setters
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAssignedDriverId() { return assignedDriverId; }
    public void setAssignedDriverId(Long assignedDriverId) { this.assignedDriverId = assignedDriverId; }

    public Long getAssignedTicketingOfficerId() { return assignedTicketingOfficerId; }
    public void setAssignedTicketingOfficerId(Long assignedTicketingOfficerId) {
        this.assignedTicketingOfficerId = assignedTicketingOfficerId;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

