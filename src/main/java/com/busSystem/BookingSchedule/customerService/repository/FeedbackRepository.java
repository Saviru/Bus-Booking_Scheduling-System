package com.busSystem.BookingSchedule.customerService.repository;

import com.busSystem.BookingSchedule.customerService.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByRating(Integer rating);
    
    List<Feedback> findByRatingGreaterThanEqual(Integer rating);
    
    List<Feedback> findByRatingLessThanEqual(Integer rating);
    
    List<Feedback> findByRouteId(String routeId);
    
    List<Feedback> findByCustomerNameContainingIgnoreCase(String customerName);
    
    List<Feedback> findByReviewed(Boolean reviewed);
    
    List<Feedback> findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Feedback> findByFeedbackType(String feedbackType);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.routeId = :routeId")
    Double getAverageRatingByRouteId(@Param("routeId") String routeId);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getOverallAverageRating();
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.rating = :rating")
    Long countByRating(@Param("rating") Integer rating);
    
    List<Feedback> findByTagsContaining(String tag);
}