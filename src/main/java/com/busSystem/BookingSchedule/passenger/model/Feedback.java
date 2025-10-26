package com.busSystem.BookingSchedule.passenger.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "feedbacks")
public class Feedback extends Model {

    @Column(name = "passenger_id")
    private Long passengerId;
    
    @Column(name = "rating")
    private Integer rating;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "service_type")
    private String serviceType;
    
    @Column(name = "status")
    private String status;

    // Constructors
    public Feedback() {}
    
    public Feedback(Long passengerId, Integer rating, String subject, String message, 
                   String category, String serviceType, String status) {
        this.passengerId = passengerId;
        this.rating = rating;
        this.subject = subject;
        this.message = message;
        this.category = category;
        this.serviceType = serviceType;
        this.status = status;
    }

    // Getters and Setters
    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}