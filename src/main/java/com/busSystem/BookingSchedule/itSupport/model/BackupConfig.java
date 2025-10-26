package com.busSystem.BookingSchedule.itSupport.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_configs")
public class BackupConfig extends Model {

    @Column(name = "backup_name", nullable = false)
    private String backupName;
    
    @Column(name = "backup_type", nullable = false)
    private String backupType;
    
    @Column(nullable = false)
    private String schedule;
    
    @Column(name = "retention_policy", nullable = false)
    private String retentionPolicy;
    
    @Column(nullable = false)
    private String status;
    
    @Column(name = "last_backup_date")
    private LocalDateTime lastBackupDate;
    
    // Constructors
    public BackupConfig() {}
    
    public BackupConfig(String backupName, String backupType, String schedule, String retentionPolicy, String status) {
        this.backupName = backupName;
        this.backupType = backupType;
        this.schedule = schedule;
        this.retentionPolicy = retentionPolicy;
        this.status = status;
    }
    
    // Getters and Setters
    public String getBackupName() { return backupName; }
    public void setBackupName(String backupName) { this.backupName = backupName; }
    
    public String getBackupType() { return backupType; }
    public void setBackupType(String backupType) { this.backupType = backupType; }
    
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public String getRetentionPolicy() { return retentionPolicy; }
    public void setRetentionPolicy(String retentionPolicy) { this.retentionPolicy = retentionPolicy; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastBackupDate() { return lastBackupDate; }
    public void setLastBackupDate(LocalDateTime lastBackupDate) { this.lastBackupDate = lastBackupDate; }
}