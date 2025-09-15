package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import com.busSystem.BookingSchedule.itSupport.model.BackupHistory;
import com.busSystem.BookingSchedule.itSupport.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BackupService {

    // Backup Configuration
    List<BackupConfig> getAllBackupConfigs();
    
    Optional<BackupConfig> getBackupConfigById(Long id);
    
    BackupConfig saveBackupConfig(BackupConfig backupConfig);
    
    void deleteBackupConfig(Long id);
    
    List<BackupConfig> getActiveBackupConfigs();
    
    boolean isBackupConfigNameExists(String name);
    
    // Backup History
    List<BackupHistory> getAllBackupHistory();
    
    Optional<BackupHistory> getBackupHistoryById(Long id);
    
    BackupHistory saveBackupHistory(BackupHistory backupHistory);
    
    void deleteBackupHistory(Long id);
    
    List<BackupHistory> getBackupHistoryByConfig(Long configId);
    
    List<BackupHistory> getBackupHistoryByStatus(String status);
    
    List<BackupHistory> getBackupHistoryByDateRange(LocalDateTime start, LocalDateTime end);
    
    List<BackupHistory> getRecentBackupHistory();
    
    // Backup Operations
    BackupHistory initiateManualBackup(Long configId, User user);
    
    boolean deleteBackupFile(Long historyId);
    
    void cleanupOldBackups();
    
    // For testing/simulation purposes
    BackupHistory simulateBackupCompletion(Long historyId, boolean success);
}