package com.busSystem.BookingSchedule.passenger.service;

import com.busSystem.BookingSchedule.passenger.model.Feedback;
import com.busSystem.BookingSchedule.passenger.repository.FeedbackRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class FeedbackService extends Service<Feedback, Long> {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getById(Long id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public Feedback save(Feedback feedback) {
        feedback.setUpdatedDate(LocalDateTime.now());
        if (feedback.getId() == null) {
            feedback.setCreatedDate(LocalDateTime.now());
        }
        return feedbackRepository.save(feedback);
    }

    @Override
    public void delete(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return feedbackRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Feedback> getFeedbacksByPassenger(Long passengerId) {
        return feedbackRepository.findByPassengerIdOrderByCreatedDateDesc(passengerId);
    }
    
    public List<Feedback> getFeedbacksByStatus(String status) {
        return feedbackRepository.findByStatus(status);
    }

    public List<Feedback> getFeedbacksByCategory(String category) {
        return feedbackRepository.findByCategory(category);
    }
}