package com.busSystem.BookingSchedule.customerService.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.customerService.service.CustomerServiceComplaintService;
import com.busSystem.BookingSchedule.customerService.service.CustomerServiceFeedbackService;
import com.busSystem.BookingSchedule.customerService.model.ComplaintResponse;
import com.busSystem.BookingSchedule.customerService.model.FeedbackResponse;
import com.busSystem.BookingSchedule.passenger.model.Complaint;
import com.busSystem.BookingSchedule.passenger.model.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/customersupport")
public class CustomerServiceController extends Controller {

    @Autowired
    private CustomerServiceComplaintService complaintService;
    
    @Autowired
    private CustomerServiceFeedbackService feedbackService;
    
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            return "customersupport/dashboard";
        } catch (Exception e) {
            return handleException(e, model, "customersupport/dashboard");
        }
    }
    
    // Complaint Management
    @GetMapping("/complaints")
    public String listComplaints(@RequestParam(required = false) String status,
                                @RequestParam(required = false) String category,
                                HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            List<Complaint> complaints;
            if (status != null && !status.isEmpty()) {
                complaints = complaintService.getComplaintsByStatus(status);
            } else if (category != null && !category.isEmpty()) {
                complaints = complaintService.getComplaintsByCategory(category);
            } else {
                complaints = complaintService.getAll();
            }
            model.addAttribute("complaints", complaints);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("selectedCategory", category);
            return "customersupport/complaints";
        } catch (Exception e) {
            return handleException(e, model, "customersupport/complaints");
        }
    }
    
    @GetMapping("/complaints/{id}")
    public String viewComplaint(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<Complaint> complaint = complaintService.getById(id);
        if (complaint.isPresent()) {
            model.addAttribute("complaint", complaint.get());
            model.addAttribute("response", new ComplaintResponse());
            model.addAttribute("responses", complaintService.getResponsesByComplaintId(id));

            // Add passenger information
            complaintService.getPassengerByComplaint(id).ifPresent(passenger ->
                model.addAttribute("passenger", passenger));

            // Add booking information if available
            complaintService.getBookingByComplaint(id).ifPresent(booking ->
                model.addAttribute("booking", booking));

            return "customersupport/complaint-detail";
        }
        addErrorMessage(model, "Complaint not found");
        return "redirect:/customersupport/complaints";
    }
    
    @GetMapping("/complaints/{id}/edit")
    public String editComplaintForm(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<Complaint> complaint = complaintService.getById(id);
        if (complaint.isPresent()) {
            model.addAttribute("complaint", complaint.get());
            return "customersupport/complaint-edit";
        }
        addErrorMessage(model, "Complaint not found");
        return "redirect:/customersupport/complaints";
    }

    @PostMapping("/complaints/{id}")
    public String updateComplaint(@PathVariable Long id, @RequestParam String status,
                                 @RequestParam(required = false) String resolution,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            complaintService.updateComplaintStatus(id, status, resolution);
            return redirectWithSuccess("/customersupport/complaints", "Complaint updated successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/complaints", "Error updating complaint: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/complaints/{id}/edit")
    public String editComplaint(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam String category,
                               @RequestParam String priority,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Optional<Complaint> complaintOpt = complaintService.getById(id);
            if (complaintOpt.isPresent()) {
                Complaint complaint = complaintOpt.get();
                complaint.setSubject(title);
                complaint.setDescription(description);
                complaint.setCategory(category);
                complaint.setPriority(priority);
                complaintService.save(complaint);
                return redirectWithSuccess("/customersupport/complaints/" + id, "Complaint updated successfully", redirectAttributes);
            }
            return redirectWithError("/customersupport/complaints", "Complaint not found", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/complaints/" + id, "Error updating complaint: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/complaints/{id}/response")
    public String addComplaintResponse(@PathVariable Long id,
                                      @RequestParam String responseMessage,
                                      @RequestParam(required = false) String internalNotes,
                                      @RequestParam String status,
                                      @RequestParam(required = false) String resolution,
                                      HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);

            // Create and save the response
            ComplaintResponse response = new ComplaintResponse();
            response.setComplaintId(id);
            response.setCustomerServiceId(userId);
            response.setResponseMessage(responseMessage);
            response.setInternalNotes(internalNotes);
            response.setStatus(status);
            complaintService.saveComplaintResponse(response);

            // Update the complaint status and resolution
            complaintService.updateComplaintStatus(id, status, resolution);

            return redirectWithSuccess("/customersupport/complaints/" + id, "Response added successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/complaints/" + id, "Error adding response: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/complaints/responses/{responseId}/edit")
    public String editComplaintResponseForm(@PathVariable Long responseId, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<ComplaintResponse> response = complaintService.getComplaintResponseById(responseId);
        if (response.isPresent()) {
            model.addAttribute("response", response.get());
            return "customersupport/complaint-response-edit";
        }
        addErrorMessage(model, "Response not found");
        return "redirect:/customersupport/complaints";
    }

    @PostMapping("/complaints/responses/{responseId}/edit")
    public String updateComplaintResponse(@PathVariable Long responseId,
                                         @RequestParam String responseMessage,
                                         @RequestParam(required = false) String internalNotes,
                                         HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Optional<ComplaintResponse> responseOpt = complaintService.getComplaintResponseById(responseId);
            if (responseOpt.isPresent()) {
                ComplaintResponse response = responseOpt.get();
                response.setResponseMessage(responseMessage);
                response.setInternalNotes(internalNotes);
                complaintService.updateComplaintResponse(responseId, response);
                return redirectWithSuccess("/customersupport/complaints/" + response.getComplaintId(),
                    "Response updated successfully", redirectAttributes);
            }
            return redirectWithError("/customersupport/complaints", "Response not found", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/complaints", "Error updating response: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/complaints/responses/{responseId}/delete")
    public String deleteComplaintResponse(@PathVariable Long responseId, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Optional<ComplaintResponse> response = complaintService.getComplaintResponseById(responseId);
            Long complaintId = response.map(ComplaintResponse::getComplaintId).orElse(null);
            complaintService.deleteComplaintResponse(responseId);

            if (complaintId != null) {
                return redirectWithSuccess("/customersupport/complaints/" + complaintId,
                    "Response deleted successfully", redirectAttributes);
            }
            return redirectWithSuccess("/customersupport/complaints", "Response deleted successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/complaints", "Error deleting response: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/complaints/{id}/delete")
    public String deleteComplaint(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            complaintService.delete(id);
            return redirectWithSuccess("/customersupport/complaints", "Complaint deleted successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/complaints", "Error deleting complaint: " + e.getMessage(), redirectAttributes);
        }
    }
    
    // Feedback Management
    @GetMapping("/feedbacks")
    public String listFeedbacks(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String category,
                               HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            List<Feedback> feedbacks;
            if (status != null && !status.isEmpty()) {
                feedbacks = feedbackService.getFeedbacksByStatus(status);
            } else if (category != null && !category.isEmpty()) {
                feedbacks = feedbackService.getFeedbacksByCategory(category);
            } else {
                feedbacks = feedbackService.getAll();
            }
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("selectedCategory", category);
            return "customersupport/feedbacks";
        } catch (Exception e) {
            return handleException(e, model, "customersupport/feedbacks");
        }
    }
    
    @GetMapping("/feedbacks/{id}")
    public String viewFeedback(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<Feedback> feedback = feedbackService.getById(id);
        if (feedback.isPresent()) {
            model.addAttribute("feedback", feedback.get());
            model.addAttribute("response", new FeedbackResponse());
            model.addAttribute("responses", feedbackService.getResponsesByFeedbackId(id));

            // Add passenger information
            feedbackService.getPassengerByFeedback(id).ifPresent(passenger ->
                model.addAttribute("passenger", passenger));

            return "customersupport/feedback-detail";
        }
        addErrorMessage(model, "Feedback not found");
        return "redirect:/customersupport/feedbacks";
    }

    @GetMapping("/feedbacks/{id}/edit")
    public String editFeedbackForm(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<Feedback> feedback = feedbackService.getById(id);
        if (feedback.isPresent()) {
            model.addAttribute("feedback", feedback.get());
            return "customersupport/feedback-edit";
        }
        addErrorMessage(model, "Feedback not found");
        return "redirect:/customersupport/feedbacks";
    }

    @PostMapping("/feedbacks/{id}")
    public String updateFeedback(@PathVariable Long id, @RequestParam String status,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            feedbackService.updateFeedbackStatus(id, status);
            return redirectWithSuccess("/customersupport/feedbacks", "Feedback updated successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/feedbacks", "Error updating feedback: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/feedbacks/{id}/edit")
    public String editFeedback(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String message,
                              @RequestParam String category,
                              @RequestParam Integer rating,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Optional<Feedback> feedbackOpt = feedbackService.getById(id);
            if (feedbackOpt.isPresent()) {
                Feedback feedback = feedbackOpt.get();
                feedback.setSubject(title);
                feedback.setMessage(message);
                feedback.setCategory(category);
                feedback.setRating(rating);
                feedbackService.save(feedback);
                return redirectWithSuccess("/customersupport/feedbacks/" + id, "Feedback updated successfully", redirectAttributes);
            }
            return redirectWithError("/customersupport/feedbacks", "Feedback not found", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/feedbacks/" + id, "Error updating feedback: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/feedbacks/{id}/response")
    public String addFeedbackResponse(@PathVariable Long id,
                                     @RequestParam String responseMessage,
                                     @RequestParam(required = false) String internalNotes,
                                     @RequestParam String status,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);

            // Create and save the response
            FeedbackResponse response = new FeedbackResponse();
            response.setFeedbackId(id);
            response.setCustomerServiceId(userId);
            response.setResponseMessage(responseMessage);
            response.setInternalNotes(internalNotes);
            response.setStatus(status);
            feedbackService.saveFeedbackResponse(response);

            // Update the feedback status
            feedbackService.updateFeedbackStatus(id, status);

            return redirectWithSuccess("/customersupport/feedbacks/" + id, "Response added successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/feedbacks/" + id, "Error adding response: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/feedbacks/responses/{responseId}/edit")
    public String editFeedbackResponseForm(@PathVariable Long responseId, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<FeedbackResponse> response = feedbackService.getFeedbackResponseById(responseId);
        if (response.isPresent()) {
            model.addAttribute("response", response.get());
            return "customersupport/feedback-response-edit";
        }
        addErrorMessage(model, "Response not found");
        return "redirect:/customersupport/feedbacks";
    }

    @PostMapping("/feedbacks/responses/{responseId}/edit")
    public String updateFeedbackResponse(@PathVariable Long responseId,
                                        @RequestParam String responseMessage,
                                        @RequestParam(required = false) String internalNotes,
                                        HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Optional<FeedbackResponse> responseOpt = feedbackService.getFeedbackResponseById(responseId);
            if (responseOpt.isPresent()) {
                FeedbackResponse response = responseOpt.get();
                response.setResponseMessage(responseMessage);
                response.setInternalNotes(internalNotes);
                feedbackService.updateFeedbackResponse(responseId, response);
                return redirectWithSuccess("/customersupport/feedbacks/" + response.getFeedbackId(),
                    "Response updated successfully", redirectAttributes);
            }
            return redirectWithError("/customersupport/feedbacks", "Response not found", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/feedbacks", "Error updating response: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/feedbacks/responses/{responseId}/delete")
    public String deleteFeedbackResponse(@PathVariable Long responseId, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            Optional<FeedbackResponse> response = feedbackService.getFeedbackResponseById(responseId);
            Long feedbackId = response.map(FeedbackResponse::getFeedbackId).orElse(null);
            feedbackService.deleteFeedbackResponse(responseId);

            if (feedbackId != null) {
                return redirectWithSuccess("/customersupport/feedbacks/" + feedbackId,
                    "Response deleted successfully", redirectAttributes);
            }
            return redirectWithSuccess("/customersupport/feedbacks", "Response deleted successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/feedbacks", "Error deleting response: " + e.getMessage(), redirectAttributes);
        }
    }

    @PostMapping("/feedbacks/{id}/delete")
    public String deleteFeedback(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "CUSTOMER_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            feedbackService.delete(id);
            return redirectWithSuccess("/customersupport/feedbacks", "Feedback deleted successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/customersupport/feedbacks", "Error deleting feedback: " + e.getMessage(), redirectAttributes);
        }
    }
}
