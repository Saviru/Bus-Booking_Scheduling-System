package com.busSystem.BookingSchedule.itSupport.repository;

import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BackupConfigRepository extends JpaRepository<BackupConfig, Long> {
    List<BackupConfig> findByStatus(String status);
    List<BackupConfig> findByBackupType(String backupType);
}