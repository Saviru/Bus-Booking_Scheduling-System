package com.busSystem.BookingSchedule.customerService.repository;

import com.busSystem.BookingSchedule.customerService.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    Optional<Complaint> findByReferenceNumber(String referenceNumber);
    
    List<Complaint> findByStatus(String status);
    
    List<Complaint> findByCategory(String category);
    
    List<Complaint> findByCustomerNameContainingIgnoreCase(String customerName);
    
    List<Complaint> findByPriority(String priority);
    
    List<Complaint> findByAssignedTo(String assignedTo);
    
    List<Complaint> findByRouteId(String routeId);
    
    List<Complaint> findByIncidentDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Complaint> findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c FROM Complaint c WHERE c.status = 'RESOLVED' AND c.resolutionDate < :cutoffDate")
    List<Complaint> findResolvedComplaintsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    boolean existsByReferenceNumber(String referenceNumber);
}