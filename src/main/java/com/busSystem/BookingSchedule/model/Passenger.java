package com.busSystem.BookingSchedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "passengers")
public class Passenger {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String username;
    
    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "emergency_phone")
    private String emergencyPhone;
    
    // Preferences
    @Column(name = "seat_preference")
    private String seatPreference; // Window, Aisle, Any
    
    @Column(name = "frequent_destinations")
    private String frequentDestinations; // Comma separated
    
    @Column(name = "accessibility_needs")
    private String accessibilityNeeds;
    
    @Column(name = "meal_preference")
    private String mealPreference;
    
    // Payment Methods (simplified - in real app would be separate table)
    @Column(name = "payment_methods")
    private String paymentMethods; // JSON string or comma separated
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    // Constructors
    public Passenger() {}
    
    public Passenger(String username, String email, String password, 
                    String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    
    public String getSeatPreference() { return seatPreference; }
    public void setSeatPreference(String seatPreference) { this.seatPreference = seatPreference; }
    
    public String getFrequentDestinations() { return frequentDestinations; }
    public void setFrequentDestinations(String frequentDestinations) { this.frequentDestinations = frequentDestinations; }
    
    public String getAccessibilityNeeds() { return accessibilityNeeds; }
    public void setAccessibilityNeeds(String accessibilityNeeds) { this.accessibilityNeeds = accessibilityNeeds; }
    
    public String getMealPreference() { return mealPreference; }
    public void setMealPreference(String mealPreference) { this.mealPreference = mealPreference; }
    
    public String getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(String paymentMethods) { this.paymentMethods = paymentMethods; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getFullName() {
        return firstName + " " + lastName;
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
        Passenger passenger = (Passenger) o;
        return Objects.equals(id, passenger.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}