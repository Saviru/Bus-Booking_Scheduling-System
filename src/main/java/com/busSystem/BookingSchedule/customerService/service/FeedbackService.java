package com.busSystem.BookingSchedule.customerService.service;

import com.busSystem.BookingSchedule.customerService.model.Feedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FeedbackService {

    List<Feedback> getAllFeedback();
    
    Optional<Feedback> getFeedbackById(Long id);
    
    Feedback saveFeedback(Feedback feedback);
    
    void deleteFeedback(Long id);
    
    List<Feedback> getFeedbackByRating(Integer rating);
    
    List<Feedback> getFeedbackByRatingGreaterThanEqual(Integer rating);
    
    List<Feedback> getFeedbackByRatingLessThanEqual(Integer rating);
    
    List<Feedback> getFeedbackByRouteId(String routeId);
    
    List<Feedback> searchFeedbackByCustomerName(String customerName);
    
    List<Feedback> getFeedbackByReviewStatus(Boolean reviewed);
    
    List<Feedback> getFeedbackBySubmissionDateRange(LocalDateTime start, LocalDateTime end);
    
    List<Feedback> getFeedbackByType(String feedbackType);
    
    Double getAverageRatingByRouteId(String routeId);
    
    Double getOverallAverageRating();
    
    Map<Integer, Long> getRatingDistribution();
    
    List<Feedback> getFeedbackByTag(String tag);
    
    Feedback markAsReviewed(Long id, String reviewedBy, String internalNotes);
    
    void addTags(Long id, List<String> tags);
}