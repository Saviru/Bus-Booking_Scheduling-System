package com.busSystem.BookingSchedule.ticketingOfficer.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fares")
public class Fare extends Model {

    @Column(name = "route_id")
    private Long routeId;
    
    @Column(name = "ticket_type")
    private String ticketType;
    
    @Column(name = "base_price")
    private Double basePrice;
    
    @Column(name = "discount_percentage")
    private Double discountPercentage;
    
    @Column(name = "promotion_name")
    private String promotionName;
    
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    
    @Column(name = "valid_to")
    private LocalDateTime validTo;
    
    @Column(name = "status")
    private String status;

    // Constructors
    public Fare() {}
    
    public Fare(Long routeId, String ticketType, Double basePrice, Double discountPercentage,
                String promotionName, LocalDateTime validFrom, LocalDateTime validTo, String status) {
        this.routeId = routeId;
        this.ticketType = ticketType;
        this.basePrice = basePrice;
        this.discountPercentage = discountPercentage;
        this.promotionName = promotionName;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
    }

    // Getters and Setters
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    
    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    
    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
    
    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }
    
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    
    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}