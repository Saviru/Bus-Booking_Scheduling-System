package com.busSystem.BookingSchedule.itSupport.repository;

import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import com.busSystem.BookingSchedule.itSupport.model.BackupHistory;
import com.busSystem.BookingSchedule.itSupport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    
    List<BackupHistory> findByBackupConfig(BackupConfig backupConfig);
    
    List<BackupHistory> findByStatus(String status);
    
    List<BackupHistory> findByUser(User user);
    
    List<BackupHistory> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<BackupHistory> findByInitiatedBy(String initiatedBy);
    
    List<BackupHistory> findByBackupConfigOrderByStartTimeDesc(BackupConfig backupConfig);
    
    List<BackupHistory> findTop10ByOrderByStartTimeDesc();
}