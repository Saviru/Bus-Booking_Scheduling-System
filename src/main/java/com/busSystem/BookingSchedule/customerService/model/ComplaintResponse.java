package com.busSystem.BookingSchedule.customerService.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "complaint_responses")
public class ComplaintResponse extends Model {

    @Column(name = "complaint_id")
    private Long complaintId;
    
    @Column(name = "customer_service_id")
    private Long customerServiceId;
    
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;
    
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;
    
    @Column(name = "status")
    private String status;

    // Constructors
    public ComplaintResponse() {}
    
    public ComplaintResponse(Long complaintId, Long customerServiceId, String responseMessage, 
                           String internalNotes, String status) {
        this.complaintId = complaintId;
        this.customerServiceId = customerServiceId;
        this.responseMessage = responseMessage;
        this.internalNotes = internalNotes;
        this.status = status;
    }

    // Getters and Setters
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    
    public Long getCustomerServiceId() { return customerServiceId; }
    public void setCustomerServiceId(Long customerServiceId) { this.customerServiceId = customerServiceId; }
    
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}