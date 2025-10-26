package com.busSystem.BookingSchedule.customerService.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback_responses")
public class FeedbackResponse extends Model {

    @Column(name = "feedback_id")
    private Long feedbackId;
    
    @Column(name = "customer_service_id")
    private Long customerServiceId;
    
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;
    
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;
    
    @Column(name = "status")
    private String status;

    // Constructors
    public FeedbackResponse() {}
    
    public FeedbackResponse(Long feedbackId, Long customerServiceId, String responseMessage, 
                          String internalNotes, String status) {
        this.feedbackId = feedbackId;
        this.customerServiceId = customerServiceId;
        this.responseMessage = responseMessage;
        this.internalNotes = internalNotes;
        this.status = status;
    }

    // Getters and Setters
    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }
    
    public Long getCustomerServiceId() { return customerServiceId; }
    public void setCustomerServiceId(Long customerServiceId) { this.customerServiceId = customerServiceId; }
    
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}