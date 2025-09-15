package com.busSystem.BookingSchedule.itSupport.repository;

import com.busSystem.BookingSchedule.itSupport.model.LoginHistory;
import com.busSystem.BookingSchedule.itSupport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    
    List<LoginHistory> findByUser(User user);
    
    List<LoginHistory> findByUserOrderByLoginTimeDesc(User user);
    
    List<LoginHistory> findByLoginTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<LoginHistory> findByStatus(String status);
}