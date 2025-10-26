package com.busSystem.BookingSchedule.passenger.repository;

import com.busSystem.BookingSchedule.passenger.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByPassengerId(Long passengerId);
    List<Feedback> findByStatus(String status);
    List<Feedback> findByPassengerIdAndStatus(Long passengerId, String status);
    List<Feedback> findByPassengerIdOrderByCreatedDateDesc(Long passengerId);
    List<Feedback> findByCategory(String category);
    List<Feedback> findByRating(Integer rating);
}