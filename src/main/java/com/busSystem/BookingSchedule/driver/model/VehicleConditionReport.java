package com.busSystem.BookingSchedule.driver.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_condition_reports")
public class VehicleConditionReport extends Model {

    @Column(name = "driver_id", nullable = false)
    private Long driverId;
    
    @Column(name = "vehicle_number", nullable = false)
    private String vehicleNumber;
    
    @Column(name = "brakes_condition", nullable = false)
    private String brakesCondition;
    
    @Column(name = "lights_condition", nullable = false)
    private String lightsCondition;
    
    @Column(name = "tires_condition", nullable = false)
    private String tiresCondition;
    
    @Column(name = "engine_condition", nullable = false)
    private String engineCondition;
    
    @Column(name = "other_issues", columnDefinition = "TEXT")
    private String otherIssues;
    
    @Column(name = "overall_status", nullable = false)
    private String overallStatus;
    
    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";
    
    // Constructors
    public VehicleConditionReport() {}
    
    public VehicleConditionReport(Long driverId, String vehicleNumber, String brakesCondition,
                                String lightsCondition, String tiresCondition, String engineCondition,
                                String otherIssues, String overallStatus) {
        this.driverId = driverId;
        this.vehicleNumber = vehicleNumber;
        this.brakesCondition = brakesCondition;
        this.lightsCondition = lightsCondition;
        this.tiresCondition = tiresCondition;
        this.engineCondition = engineCondition;
        this.otherIssues = otherIssues;
        this.overallStatus = overallStatus;
    }
    
    // Getters and Setters
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public String getBrakesCondition() { return brakesCondition; }
    public void setBrakesCondition(String brakesCondition) { this.brakesCondition = brakesCondition; }
    
    public String getLightsCondition() { return lightsCondition; }
    public void setLightsCondition(String lightsCondition) { this.lightsCondition = lightsCondition; }
    
    public String getTiresCondition() { return tiresCondition; }
    public void setTiresCondition(String tiresCondition) { this.tiresCondition = tiresCondition; }
    
    public String getEngineCondition() { return engineCondition; }
    public void setEngineCondition(String engineCondition) { this.engineCondition = engineCondition; }
    
    public String getOtherIssues() { return otherIssues; }
    public void setOtherIssues(String otherIssues) { this.otherIssues = otherIssues; }
    
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    
    public String getFollowUpNotes() { return followUpNotes; }
    public void setFollowUpNotes(String followUpNotes) { this.followUpNotes = followUpNotes; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}