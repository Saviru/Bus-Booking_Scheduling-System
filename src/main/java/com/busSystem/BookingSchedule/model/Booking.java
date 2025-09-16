package com.busSystem.BookingSchedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_reference", unique = true, nullable = false)
    private String bookingReference;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
    
    @NotNull(message = "Travel date is required")
    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;
    
    @NotNull(message = "Number of seats is required")
    @Positive(message = "Number of seats must be positive")
    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;
    
    @Column(name = "seat_numbers")
    private String seatNumbers; // Comma-separated seat numbers
    
    @Column(name = "seat_preference")
    private String seatPreference; // Window, Aisle, Any
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_reference")
    private String paymentReference;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "refund_processed_at")
    private LocalDateTime refundProcessedAt;
    
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED, REFUNDED
    }
    
    // Constructors
    public Booking() {}
    
    public Booking(Passenger passenger, Schedule schedule, LocalDate travelDate,
                  Integer numberOfSeats, BigDecimal totalAmount, String paymentMethod) {
        this.passenger = passenger;
        this.schedule = schedule;
        this.travelDate = travelDate;
        this.numberOfSeats = numberOfSeats;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = BookingStatus.PENDING;
        this.bookingDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.bookingReference = generateBookingReference();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    
    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    
    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    
    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }
    
    public String getSeatPreference() { return seatPreference; }
    public void setSeatPreference(String seatPreference) { this.seatPreference = seatPreference; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    
    public LocalDateTime getRefundProcessedAt() { return refundProcessedAt; }
    public void setRefundProcessedAt(LocalDateTime refundProcessedAt) { this.refundProcessedAt = refundProcessedAt; }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    // Business methods
    public boolean canBeModified() {
        return (status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED) 
               && travelDate.isAfter(LocalDate.now().plusDays(1));
    }
    
    public boolean canBeCancelled() {
        return (status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED)
               && travelDate.isAfter(LocalDate.now());
    }
    
    public BigDecimal calculateRefundAmount() {
        if (!canBeCancelled()) return BigDecimal.ZERO;
        
        long daysUntilTravel = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), travelDate);
        
        if (daysUntilTravel >= 7) {
            return totalAmount.multiply(BigDecimal.valueOf(0.9)); // 10% cancellation fee
        } else if (daysUntilTravel >= 3) {
            return totalAmount.multiply(BigDecimal.valueOf(0.75)); // 25% cancellation fee
        } else if (daysUntilTravel >= 1) {
            return totalAmount.multiply(BigDecimal.valueOf(0.5)); // 50% cancellation fee
        } else {
            return BigDecimal.ZERO; // No refund for same-day cancellation
        }
    }
    
    private String generateBookingReference() {
        return "BK" + System.currentTimeMillis() + 
               (int)(Math.random() * 1000);
    }
    
    public String getStatusDisplayName() {
        return switch (status) {
            case PENDING -> "Pending Payment";
            case CONFIRMED -> "Confirmed";
            case CANCELLED -> "Cancelled";
            case COMPLETED -> "Completed";
            case REFUNDED -> "Refunded";
        };
    }
    
    @PrePersist
    protected void onCreate() {
        if (bookingDate == null) bookingDate = LocalDateTime.now();
        lastModified = LocalDateTime.now();
        if (bookingReference == null) bookingReference = generateBookingReference();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastModified = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookingReference='" + bookingReference + '\'' +
                ", travelDate=" + travelDate +
                ", numberOfSeats=" + numberOfSeats +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}