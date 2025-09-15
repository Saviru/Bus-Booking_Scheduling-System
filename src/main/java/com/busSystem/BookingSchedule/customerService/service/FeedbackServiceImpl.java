package com.busSystem.BookingSchedule.customerService.service;

import com.busSystem.BookingSchedule.customerService.model.Feedback;
import com.busSystem.BookingSchedule.customerService.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    
    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    @Override
    @Transactional
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<Feedback> getFeedbackByRating(Integer rating) {
        return feedbackRepository.findByRating(rating);
    }

    @Override
    public List<Feedback> getFeedbackByRatingGreaterThanEqual(Integer rating) {
        return feedbackRepository.findByRatingGreaterThanEqual(rating);
    }

    @Override
    public List<Feedback> getFeedbackByRatingLessThanEqual(Integer rating) {
        return feedbackRepository.findByRatingLessThanEqual(rating);
    }

    @Override
    public List<Feedback> getFeedbackByRouteId(String routeId) {
        return feedbackRepository.findByRouteId(routeId);
    }

    @Override
    public List<Feedback> searchFeedbackByCustomerName(String customerName) {
        return feedbackRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }

    @Override
    public List<Feedback> getFeedbackByReviewStatus(Boolean reviewed) {
        return feedbackRepository.findByReviewed(reviewed);
    }

    @Override
    public List<Feedback> getFeedbackBySubmissionDateRange(LocalDateTime start, LocalDateTime end) {
        return feedbackRepository.findBySubmissionDateBetween(start, end);
    }

    @Override
    public List<Feedback> getFeedbackByType(String feedbackType) {
        return feedbackRepository.findByFeedbackType(feedbackType);
    }

    @Override
    public Double getAverageRatingByRouteId(String routeId) {
        return feedbackRepository.getAverageRatingByRouteId(routeId);
    }

    @Override
    public Double getOverallAverageRating() {
        return feedbackRepository.getOverallAverageRating();
    }

    @Override
    public Map<Integer, Long> getRatingDistribution() {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, feedbackRepository.countByRating(i));
        }
        return distribution;
    }

    @Override
    public List<Feedback> getFeedbackByTag(String tag) {
        return feedbackRepository.findByTagsContaining(tag);
    }

    @Override
    @Transactional
    public Feedback markAsReviewed(Long id, String reviewedBy, String internalNotes) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID: " + id));
        
        feedback.setReviewed(true);
        feedback.setReviewDate(LocalDateTime.now());
        feedback.setReviewedBy(reviewedBy);
        
        if (internalNotes != null && !internalNotes.isEmpty()) {
            feedback.setInternalNotes(internalNotes);
        }
        
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void addTags(Long id, List<String> newTags) {
        if (newTags == null || newTags.isEmpty()) {
            return;
        }
        
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID: " + id));
        
        // Get existing tags
        Set<String> existingTags = new HashSet<>();
        if (feedback.getTags() != null && !feedback.getTags().isEmpty()) {
            existingTags.addAll(Arrays.asList(feedback.getTags().split(",")));
        }
        
        // Add new tags
        existingTags.addAll(newTags);
        
        // Convert back to comma-separated string
        String updatedTags = existingTags.stream()
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.joining(","));
        
        feedback.setTags(updatedTags);
        feedbackRepository.save(feedback);
    }
}