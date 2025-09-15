package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import com.busSystem.BookingSchedule.itSupport.model.BackupHistory;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.repository.BackupConfigRepository;
import com.busSystem.BookingSchedule.itSupport.repository.BackupHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BackupServiceImpl implements BackupService {

    private final BackupConfigRepository backupConfigRepository;
    private final BackupHistoryRepository backupHistoryRepository;
    
    @Autowired
    public BackupServiceImpl(BackupConfigRepository backupConfigRepository,
                           BackupHistoryRepository backupHistoryRepository) {
        this.backupConfigRepository = backupConfigRepository;
        this.backupHistoryRepository = backupHistoryRepository;
    }

    @Override
    public List<BackupConfig> getAllBackupConfigs() {
        return backupConfigRepository.findAll();
    }

    @Override
    public Optional<BackupConfig> getBackupConfigById(Long id) {
        return backupConfigRepository.findById(id);
    }

    @Override
    @Transactional
    public BackupConfig saveBackupConfig(BackupConfig backupConfig) {
        return backupConfigRepository.save(backupConfig);
    }

    @Override
    @Transactional
    public void deleteBackupConfig(Long id) {
        backupConfigRepository.deleteById(id);
    }

    @Override
    public List<BackupConfig> getActiveBackupConfigs() {
        return backupConfigRepository.findByActive(true);
    }

    @Override
    public boolean isBackupConfigNameExists(String name) {
        return backupConfigRepository.existsByName(name);
    }

    @Override
    public List<BackupHistory> getAllBackupHistory() {
        return backupHistoryRepository.findAll();
    }

    @Override
    public Optional<BackupHistory> getBackupHistoryById(Long id) {
        return backupHistoryRepository.findById(id);
    }

    @Override
    @Transactional
    public BackupHistory saveBackupHistory(BackupHistory backupHistory) {
        return backupHistoryRepository.save(backupHistory);
    }

    @Override
    @Transactional
    public void deleteBackupHistory(Long id) {
        backupHistoryRepository.deleteById(id);
    }

    @Override
    public List<BackupHistory> getBackupHistoryByConfig(Long configId) {
        Optional<BackupConfig> config = backupConfigRepository.findById(configId);
        return config.map(backupHistoryRepository::findByBackupConfigOrderByStartTimeDesc)
                .orElse(List.of());
    }

    @Override
    public List<BackupHistory> getBackupHistoryByStatus(String status) {
        return backupHistoryRepository.findByStatus(status);
    }

    @Override
    public List<BackupHistory> getBackupHistoryByDateRange(LocalDateTime start, LocalDateTime end) {
        return backupHistoryRepository.findByStartTimeBetween(start, end);
    }

    @Override
    public List<BackupHistory> getRecentBackupHistory() {
        return backupHistoryRepository.findTop10ByOrderByStartTimeDesc();
    }

    @Override
    @Transactional
    public BackupHistory initiateManualBackup(Long configId, User user) {
        // Get the backup configuration
        BackupConfig config = backupConfigRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid backup configuration ID: " + configId));
        
        // Create a new backup history record
        BackupHistory backupHistory = new BackupHistory();
        backupHistory.setBackupConfig(config);
        backupHistory.setStartTime(LocalDateTime.now());
        backupHistory.setStatus("IN_PROGRESS");
        backupHistory.setInitiatedBy("MANUAL");
        backupHistory.setUser(user);
        
        // Generate a file path based on backup type and timestamp
        String timestamp = backupHistory.getStartTime().toString().replace(":", "-").replace(".", "-");
        String filePath = config.getStorageLocation() + "/" + config.getBackupType() + "_" + timestamp + ".backup";
        backupHistory.setFilePath(filePath);
        
        // In a real application, this would trigger the actual backup process
        // For now, we just save the record
        return backupHistoryRepository.save(backupHistory);
    }

    @Override
    @Transactional
    public boolean deleteBackupFile(Long historyId) {
        // In a real application, this would delete the actual backup file
        // For now, we just mark it as deleted in the database
        Optional<BackupHistory> historyOptional = backupHistoryRepository.findById(historyId);
        
        if (historyOptional.isPresent()) {
            BackupHistory history = historyOptional.get();
            backupHistoryRepository.delete(history);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional
    public void cleanupOldBackups() {
        // This would be implemented to remove backups older than retention period
        // In a real application, this would scan and remove old backup files
        // and their corresponding database records
    }

    @Override
    @Transactional
    public BackupHistory simulateBackupCompletion(Long historyId, boolean success) {
        // This is only for simulation/demo purposes
        Optional<BackupHistory> historyOptional = backupHistoryRepository.findById(historyId);
        
        if (historyOptional.isPresent()) {
            BackupHistory history = historyOptional.get();
            
            // Set end time and status
            history.setEndTime(LocalDateTime.now());
            history.setStatus(success ? "SUCCESS" : "FAILED");
            
            // If successful, set a random file size, otherwise set error message
            if (success) {
                Random random = new Random();
                history.setFileSizeMb(100.0 + random.nextDouble() * 900.0); // Random size between 100 and 1000 MB
            } else {
                history.setErrorMessage("Simulated backup failure");
            }
            
            return backupHistoryRepository.save(history);
        }
        
        throw new IllegalArgumentException("Invalid backup history ID: " + historyId);
    }
}