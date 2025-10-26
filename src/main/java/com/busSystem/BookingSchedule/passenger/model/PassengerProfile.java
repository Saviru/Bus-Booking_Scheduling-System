package com.busSystem.BookingSchedule.passenger.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "passenger_profiles")
public class PassengerProfile extends Model {

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "preferred_seat_type")
    private String preferredSeatType;
    
    @Column(name = "frequent_destinations")
    private String frequentDestinations;
    
    @Column(name = "payment_methods")
    private String paymentMethods;
    
    @Column(name = "accessibility_needs")
    private String accessibilityNeeds;
    
    @Column(name = "travel_preferences")
    private String travelPreferences;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "status")
    private String status;

    // Constructors
    public PassengerProfile() {}
    
    public PassengerProfile(Long userId, String preferredSeatType, String frequentDestinations,
                          String paymentMethods, String accessibilityNeeds, String travelPreferences,
                          String emergencyContactName, String emergencyContactPhone, String status) {
        this.userId = userId;
        this.preferredSeatType = preferredSeatType;
        this.frequentDestinations = frequentDestinations;
        this.paymentMethods = paymentMethods;
        this.accessibilityNeeds = accessibilityNeeds;
        this.travelPreferences = travelPreferences;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.status = status;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getPreferredSeatType() { return preferredSeatType; }
    public void setPreferredSeatType(String preferredSeatType) { this.preferredSeatType = preferredSeatType; }
    
    public String getFrequentDestinations() { return frequentDestinations; }
    public void setFrequentDestinations(String frequentDestinations) { this.frequentDestinations = frequentDestinations; }
    
    public String getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(String paymentMethods) { this.paymentMethods = paymentMethods; }
    
    public String getAccessibilityNeeds() { return accessibilityNeeds; }
    public void setAccessibilityNeeds(String accessibilityNeeds) { this.accessibilityNeeds = accessibilityNeeds; }
    
    public String getTravelPreferences() { return travelPreferences; }
    public void setTravelPreferences(String travelPreferences) { this.travelPreferences = travelPreferences; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}