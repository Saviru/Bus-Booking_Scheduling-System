package com.busSystem.BookingSchedule.customerService.service;

import com.busSystem.BookingSchedule.customerService.model.Complaint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ComplaintService {

    List<Complaint> getAllComplaints();
    
    Optional<Complaint> getComplaintById(Long id);
    
    Optional<Complaint> getComplaintByReferenceNumber(String referenceNumber);
    
    Complaint saveComplaint(Complaint complaint);
    
    void deleteComplaint(Long id);
    
    List<Complaint> getComplaintsByStatus(String status);
    
    List<Complaint> getComplaintsByCategory(String category);
    
    List<Complaint> searchComplaintsByCustomerName(String customerName);
    
    List<Complaint> getComplaintsByPriority(String priority);
    
    List<Complaint> getComplaintsByAssignedTo(String assignedTo);
    
    List<Complaint> getComplaintsByRouteId(String routeId);
    
    List<Complaint> getComplaintsByIncidentDateRange(LocalDateTime start, LocalDateTime end);
    
    List<Complaint> getComplaintsBySubmissionDateRange(LocalDateTime start, LocalDateTime end);
    
    void cleanupOldResolvedComplaints(int retentionDays);
    
    String generateReferenceNumber();
    
    boolean isReferenceNumberExists(String referenceNumber);
    
    Complaint updateComplaintStatus(Long id, String status, String resolutionNotes, String assignedTo);
    
    List<String> getAllCategories();
}