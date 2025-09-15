package com.busSystem.BookingSchedule.driver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_condition_reports")
public class VehicleConditionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle number is required")
    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @NotBlank(message = "Driver name is required")
    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "driver_id")
    private String driverId;

    @NotNull(message = "Report date is required")
    @Column(name = "report_date")
    private LocalDateTime reportDate;

    @NotBlank(message = "Report type is required")
    @Column(name = "report_type")
    private String reportType; // PRE_TRIP, POST_TRIP, MAINTENANCE_REQUEST, INCIDENT

    @Column(name = "odometer_reading")
    private Integer odometerReading;

    @NotNull(message = "Brakes condition is required")
    @Column(name = "brakes_condition")
    private String brakesCondition; // GOOD, FAIR, POOR, NEEDS_ATTENTION, UNKNOWN

    @NotNull(message = "Lights condition is required")
    @Column(name = "lights_condition")
    private String lightsCondition; // GOOD, FAIR, POOR, NEEDS_ATTENTION, UNKNOWN

    @NotNull(message = "Tires condition is required")
    @Column(name = "tires_condition")
    private String tiresCondition; // GOOD, FAIR, POOR, NEEDS_ATTENTION, UNKNOWN

    @Column(name = "exterior_condition")
    private String exteriorCondition; // GOOD, FAIR, POOR, NEEDS_ATTENTION, UNKNOWN

    @Column(name = "interior_condition")
    private String interiorCondition; // GOOD, FAIR, POOR, NEEDS_ATTENTION, UNKNOWN

    @Column(name = "fuel_level")
    private String fuelLevel; // FULL, 3/4, 1/2, 1/4, EMPTY

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @NotBlank(message = "Status is required")
    @Column(name = "status")
    private String status = "PENDING"; // PENDING, REVIEWED, IN_REPAIR, RESOLVED

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_date")
    private LocalDateTime reviewedDate;

    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

    @Column(name = "follow_up_action", columnDefinition = "TEXT")
    private String followUpAction;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (reportDate == null) {
            reportDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public void setOdometerReading(Integer odometerReading) {
        this.odometerReading = odometerReading;
    }

    public String getBrakesCondition() {
        return brakesCondition;
    }

    public void setBrakesCondition(String brakesCondition) {
        this.brakesCondition = brakesCondition;
    }

    public String getLightsCondition() {
        return lightsCondition;
    }

    public void setLightsCondition(String lightsCondition) {
        this.lightsCondition = lightsCondition;
    }

    public String getTiresCondition() {
        return tiresCondition;
    }

    public void setTiresCondition(String tiresCondition) {
        this.tiresCondition = tiresCondition;
    }

    public String getExteriorCondition() {
        return exteriorCondition;
    }

    public void setExteriorCondition(String exteriorCondition) {
        this.exteriorCondition = exteriorCondition;
    }

    public String getInteriorCondition() {
        return interiorCondition;
    }

    public void setInteriorCondition(String interiorCondition) {
        this.interiorCondition = interiorCondition;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(LocalDateTime reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public String getMaintenanceNotes() {
        return maintenanceNotes;
    }

    public void setMaintenanceNotes(String maintenanceNotes) {
        this.maintenanceNotes = maintenanceNotes;
    }

    public String getFollowUpAction() {
        return followUpAction;
    }

    public void setFollowUpAction(String followUpAction) {
        this.followUpAction = followUpAction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to check if any component needs attention
    @Transient
    public boolean hasIssues() {
        return "POOR".equals(brakesCondition) || 
               "NEEDS_ATTENTION".equals(brakesCondition) ||
               "POOR".equals(lightsCondition) || 
               "NEEDS_ATTENTION".equals(lightsCondition) ||
               "POOR".equals(tiresCondition) || 
               "NEEDS_ATTENTION".equals(tiresCondition) ||
               "POOR".equals(exteriorCondition) || 
               "NEEDS_ATTENTION".equals(exteriorCondition) ||
               "POOR".equals(interiorCondition) || 
               "NEEDS_ATTENTION".equals(interiorCondition);
    }
}