package com.busSystem.BookingSchedule.customerService.repository;

import com.busSystem.BookingSchedule.customerService.model.FeedbackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, Long> {
    List<FeedbackResponse> findByFeedbackId(Long feedbackId);
    List<FeedbackResponse> findByCustomerServiceId(Long customerServiceId);
    List<FeedbackResponse> findByStatus(String status);
}