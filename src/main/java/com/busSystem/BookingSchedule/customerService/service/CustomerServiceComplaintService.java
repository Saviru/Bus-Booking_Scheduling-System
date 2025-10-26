package com.busSystem.BookingSchedule.customerService.service;

import com.busSystem.BookingSchedule.customerService.model.ComplaintResponse;
import com.busSystem.BookingSchedule.customerService.repository.ComplaintResponseRepository;
import com.busSystem.BookingSchedule.passenger.model.Complaint;
import com.busSystem.BookingSchedule.passenger.service.ComplaintService;
import com.busSystem.BookingSchedule.passenger.service.BookingService;
import com.busSystem.BookingSchedule.passenger.model.Booking;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CustomerServiceComplaintService extends Service<Complaint, Long> {

    @Autowired
    private ComplaintResponseRepository complaintResponseRepository;
    
    @Autowired
    private ComplaintService complaintService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;

    // Implement abstract methods from base Service class
    @Override
    public List<Complaint> getAll() {
        return complaintService.getAll();
    }

    @Override
    public Optional<Complaint> getById(Long id) {
        return complaintService.getById(id);
    }

    @Override
    public Complaint save(Complaint complaint) {
        return complaintService.save(complaint);
    }

    @Override
    public void delete(Long id) {
        // First delete all associated responses
        List<ComplaintResponse> responses = getResponsesByComplaintId(id);
        complaintResponseRepository.deleteAll(responses);
        // Then delete the complaint
        complaintService.delete(id);
    }

    @Override
    public boolean exists(Long id) {
        return complaintService.exists(id);
    }

    // Specific business logic methods only
    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintService.getComplaintsByStatus(status);
    }
    
    public List<Complaint> getComplaintsByCategory(String category) {
        return complaintService.getComplaintsByCategory(category);
    }
    
    public Optional<Booking> getBookingByComplaint(Long complaintId) {
        Optional<Complaint> complaint = complaintService.getById(complaintId);
        if (complaint.isPresent() && complaint.get().getTicketId() != null) {
            return bookingService.getById(complaint.get().getTicketId());
        }
        return Optional.empty();
    }
    
    public Optional<User> getPassengerByComplaint(Long complaintId) {
        Optional<Complaint> complaint = complaintService.getById(complaintId);
        if (complaint.isPresent()) {
            return userService.getById(complaint.get().getPassengerId());
        }
        return Optional.empty();
    }
    
    public Complaint updateComplaintStatus(Long complaintId, String status, String resolution) {
        Optional<Complaint> complaint = complaintService.getById(complaintId);
        if (complaint.isPresent()) {
            complaint.get().setStatus(status);
            complaint.get().setResolution(resolution);
            complaint.get().setUpdatedDate(LocalDateTime.now());
            return complaintService.save(complaint.get());
        }
        return null;
    }
    
    public ComplaintResponse saveComplaintResponse(ComplaintResponse response) {
        response.setUpdatedDate(LocalDateTime.now());
        if (response.getId() == null) {
            response.setCreatedDate(LocalDateTime.now());
        }
        return complaintResponseRepository.save(response);
    }
    
    public List<ComplaintResponse> getResponsesByComplaintId(Long complaintId) {
        return complaintResponseRepository.findByComplaintId(complaintId);
    }

    public Optional<ComplaintResponse> getComplaintResponseById(Long responseId) {
        return complaintResponseRepository.findById(responseId);
    }

    public ComplaintResponse updateComplaintResponse(Long responseId, ComplaintResponse response) {
        response.setId(responseId);
        response.setUpdatedDate(LocalDateTime.now());
        return complaintResponseRepository.save(response);
    }

    public void deleteComplaintResponse(Long responseId) {
        complaintResponseRepository.deleteById(responseId);
    }
}