package com.busSystem.BookingSchedule.customerService.service;

import com.busSystem.BookingSchedule.customerService.model.Complaint;
import com.busSystem.BookingSchedule.customerService.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    
    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public Optional<Complaint> getComplaintByReferenceNumber(String referenceNumber) {
        return complaintRepository.findByReferenceNumber(referenceNumber);
    }

    @Override
    @Transactional
    public Complaint saveComplaint(Complaint complaint) {
        // If it's a new complaint, generate a reference number
        if (complaint.getId() == null && (complaint.getReferenceNumber() == null || complaint.getReferenceNumber().isEmpty())) {
            complaint.setReferenceNumber(generateReferenceNumber());
        }
        
        return complaintRepository.save(complaint);
    }

    @Override
    @Transactional
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status);
    }

    @Override
    public List<Complaint> getComplaintsByCategory(String category) {
        return complaintRepository.findByCategory(category);
    }

    @Override
    public List<Complaint> searchComplaintsByCustomerName(String customerName) {
        return complaintRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }

    @Override
    public List<Complaint> getComplaintsByPriority(String priority) {
        return complaintRepository.findByPriority(priority);
    }

    @Override
    public List<Complaint> getComplaintsByAssignedTo(String assignedTo) {
        return complaintRepository.findByAssignedTo(assignedTo);
    }

    @Override
    public List<Complaint> getComplaintsByRouteId(String routeId) {
        return complaintRepository.findByRouteId(routeId);
    }

    @Override
    public List<Complaint> getComplaintsByIncidentDateRange(LocalDateTime start, LocalDateTime end) {
        return complaintRepository.findByIncidentDateBetween(start, end);
    }

    @Override
    public List<Complaint> getComplaintsBySubmissionDateRange(LocalDateTime start, LocalDateTime end) {
        return complaintRepository.findBySubmissionDateBetween(start, end);
    }

    @Override
    @Transactional
    public void cleanupOldResolvedComplaints(int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        List<Complaint> oldComplaints = complaintRepository.findResolvedComplaintsOlderThan(cutoffDate);
        complaintRepository.deleteAll(oldComplaints);
    }

    @Override
    public String generateReferenceNumber() {
        // Format: CMP-YYYYMMDD-XXXX (where XXXX is a random alphanumeric string)
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CMP-" + datePart + "-" + randomPart;
    }

    @Override
    public boolean isReferenceNumberExists(String referenceNumber) {
        return complaintRepository.existsByReferenceNumber(referenceNumber);
    }

    @Override
    @Transactional
    public Complaint updateComplaintStatus(Long id, String status, String resolutionNotes, String assignedTo) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid complaint ID: " + id));
        
        complaint.setStatus(status);
        
        if (resolutionNotes != null && !resolutionNotes.isEmpty()) {
            complaint.setResolutionNotes(resolutionNotes);
        }
        
        if (assignedTo != null && !assignedTo.isEmpty()) {
            complaint.setAssignedTo(assignedTo);
        }
        
        // If status is set to RESOLVED, set the resolution date
        if ("RESOLVED".equals(status) && complaint.getResolutionDate() == null) {
            complaint.setResolutionDate(LocalDateTime.now());
        }
        
        return complaintRepository.save(complaint);
    }

    @Override
    public List<String> getAllCategories() {
        // In a real application, this might come from a database table
        // For now, we'll return a fixed list
        return Arrays.asList(
            "Delay",
            "Staff Behavior",
            "Vehicle Condition",
            "Safety Concern",
            "Lost Item",
            "Overcrowding",
            "Payment Issue",
            "Schedule Issue",
            "Other"
        );
    }
}