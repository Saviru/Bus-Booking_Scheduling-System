package com.busSystem.BookingSchedule.customerService.service;

import com.busSystem.BookingSchedule.customerService.model.FeedbackResponse;
import com.busSystem.BookingSchedule.customerService.repository.FeedbackResponseRepository;
import com.busSystem.BookingSchedule.passenger.model.Feedback;
import com.busSystem.BookingSchedule.passenger.service.FeedbackService;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CustomerServiceFeedbackService extends Service<Feedback, Long> {

    @Autowired
    private FeedbackResponseRepository feedbackResponseRepository;
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private UserService userService;

    // Implement abstract methods from base Service class
    @Override
    public List<Feedback> getAll() {
        return feedbackService.getAll();
    }

    @Override
    public Optional<Feedback> getById(Long id) {
        return feedbackService.getById(id);
    }

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackService.save(feedback);
    }

    @Override
    public void delete(Long id) {
        // First delete all associated responses
        List<FeedbackResponse> responses = getResponsesByFeedbackId(id);
        feedbackResponseRepository.deleteAll(responses);
        // Then delete the feedback
        feedbackService.delete(id);
    }

    @Override
    public boolean exists(Long id) {
        return feedbackService.exists(id);
    }

    // Specific business logic methods only
    public List<Feedback> getFeedbacksByStatus(String status) {
        return feedbackService.getFeedbacksByStatus(status);
    }
    
    public List<Feedback> getFeedbacksByCategory(String category) {
        return feedbackService.getFeedbacksByCategory(category);
    }
    
    public Optional<User> getPassengerByFeedback(Long feedbackId) {
        Optional<Feedback> feedback = feedbackService.getById(feedbackId);
        if (feedback.isPresent()) {
            return userService.getById(feedback.get().getPassengerId());
        }
        return Optional.empty();
    }
    
    public Feedback updateFeedbackStatus(Long feedbackId, String status) {
        Optional<Feedback> feedback = feedbackService.getById(feedbackId);
        if (feedback.isPresent()) {
            feedback.get().setStatus(status);
            feedback.get().setUpdatedDate(LocalDateTime.now());
            return feedbackService.save(feedback.get());
        }
        return null;
    }
    
    public FeedbackResponse saveFeedbackResponse(FeedbackResponse response) {
        response.setUpdatedDate(LocalDateTime.now());
        if (response.getId() == null) {
            response.setCreatedDate(LocalDateTime.now());
        }
        return feedbackResponseRepository.save(response);
    }
    
    public List<FeedbackResponse> getResponsesByFeedbackId(Long feedbackId) {
        return feedbackResponseRepository.findByFeedbackId(feedbackId);
    }

    public Optional<FeedbackResponse> getFeedbackResponseById(Long responseId) {
        return feedbackResponseRepository.findById(responseId);
    }

    public FeedbackResponse updateFeedbackResponse(Long responseId, FeedbackResponse response) {
        response.setId(responseId);
        response.setUpdatedDate(LocalDateTime.now());
        return feedbackResponseRepository.save(response);
    }

    public void deleteFeedbackResponse(Long responseId) {
        feedbackResponseRepository.deleteById(responseId);
    }
}