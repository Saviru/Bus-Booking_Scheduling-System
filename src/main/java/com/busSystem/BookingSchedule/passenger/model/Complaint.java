package com.busSystem.BookingSchedule.passenger.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "complaints")
public class Complaint extends Model {

    @Column(name = "passenger_id")
    private Long passengerId;
    
    @Column(name = "ticket_id")
    private Long ticketId;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "priority")
    private String priority;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    // Constructors
    public Complaint() {}
    
    public Complaint(Long passengerId, Long ticketId, String subject, String description, 
                    String category, String priority, String status) {
        this.passengerId = passengerId;
        this.ticketId = ticketId;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
    }

    // Getters and Setters
    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}