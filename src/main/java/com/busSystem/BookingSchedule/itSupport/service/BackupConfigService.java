package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import com.busSystem.BookingSchedule.itSupport.repository.BackupConfigRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class BackupConfigService extends Service<BackupConfig, Long> {

    @Autowired
    private BackupConfigRepository backupConfigRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<BackupConfig> getAll() {
        return backupConfigRepository.findAll();
    }

    @Override
    public Optional<BackupConfig> getById(Long id) {
        return backupConfigRepository.findById(id);
    }

    @Override
    public BackupConfig save(BackupConfig backupConfig) {
        if (backupConfig.getId() == null) {
            backupConfig.setCreatedDate(LocalDateTime.now());
        }
        return backupConfigRepository.save(backupConfig);
    }

    @Override
    public void delete(Long id) {
        backupConfigRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return backupConfigRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<BackupConfig> getBackupConfigsByStatus(String status) {
        return backupConfigRepository.findByStatus(status);
    }
    
    public List<BackupConfig> getBackupConfigsByType(String backupType) {
        return backupConfigRepository.findByBackupType(backupType);
    }
}