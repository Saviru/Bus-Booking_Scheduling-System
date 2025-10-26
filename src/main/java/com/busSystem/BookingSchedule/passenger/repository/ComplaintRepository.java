package com.busSystem.BookingSchedule.passenger.repository;

import com.busSystem.BookingSchedule.passenger.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPassengerId(Long passengerId);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByPassengerIdAndStatus(Long passengerId, String status);
    List<Complaint> findByPassengerIdOrderByCreatedDateDesc(Long passengerId);
    List<Complaint> findByCategory(String category);
    List<Complaint> findByPriority(String priority);
}