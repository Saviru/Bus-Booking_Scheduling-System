package com.busSystem.BookingSchedule.passenger.service;

import com.busSystem.BookingSchedule.passenger.model.Complaint;
import com.busSystem.BookingSchedule.passenger.repository.ComplaintRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ComplaintService extends Service<Complaint, Long> {

    @Autowired
    private ComplaintRepository complaintRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Complaint> getAll() {
        return complaintRepository.findAll();
    }

    @Override
    public Optional<Complaint> getById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public Complaint save(Complaint complaint) {
        complaint.setUpdatedDate(LocalDateTime.now());
        if (complaint.getId() == null) {
            complaint.setCreatedDate(LocalDateTime.now());
        }
        return complaintRepository.save(complaint);
    }

    @Override
    public void delete(Long id) {
        complaintRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return complaintRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Complaint> getComplaintsByPassenger(Long passengerId) {
        return complaintRepository.findByPassengerIdOrderByCreatedDateDesc(passengerId);
    }
    
    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status);
    }
    
    public List<Complaint> getComplaintsByCategory(String category) {
        return complaintRepository.findByCategory(category);
    }
}