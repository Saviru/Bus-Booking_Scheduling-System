package com.busSystem.BookingSchedule.customerService.controller;

import com.busSystem.BookingSchedule.customerService.model.Feedback;
import com.busSystem.BookingSchedule.customerService.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer-service/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @GetMapping
    public String listFeedback(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String minRating,
            @RequestParam(required = false) String maxRating,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Boolean reviewed,
            @RequestParam(required = false) String feedbackType,
            Model model) {
        
        List<Feedback> feedback;
        
        if (rating != null) {
            feedback = feedbackService.getFeedbackByRating(rating);
            model.addAttribute("filterType", "Rating");
            model.addAttribute("filterValue", rating);
        } else if (minRating != null && !minRating.isEmpty()) {
            feedback = feedbackService.getFeedbackByRatingGreaterThanEqual(Integer.parseInt(minRating));
            model.addAttribute("filterType", "Minimum Rating");
            model.addAttribute("filterValue", minRating);
        } else if (maxRating != null && !maxRating.isEmpty()) {
            feedback = feedbackService.getFeedbackByRatingLessThanEqual(Integer.parseInt(maxRating));
            model.addAttribute("filterType", "Maximum Rating");
            model.addAttribute("filterValue", maxRating);
        } else if (routeId != null && !routeId.isEmpty()) {
            feedback = feedbackService.getFeedbackByRouteId(routeId);
            model.addAttribute("filterType", "Route ID");
            model.addAttribute("filterValue", routeId);
        } else if (customerName != null && !customerName.isEmpty()) {
            feedback = feedbackService.searchFeedbackByCustomerName(customerName);
            model.addAttribute("filterType", "Customer Name");
            model.addAttribute("filterValue", customerName);
        } else if (reviewed != null) {
            feedback = feedbackService.getFeedbackByReviewStatus(reviewed);
            model.addAttribute("filterType", "Reviewed Status");
            model.addAttribute("filterValue", reviewed ? "Reviewed" : "Not Reviewed");
        } else if (feedbackType != null && !feedbackType.isEmpty()) {
            feedback = feedbackService.getFeedbackByType(feedbackType);
            model.addAttribute("filterType", "Feedback Type");
            model.addAttribute("filterValue", feedbackType);
        } else {
            feedback = feedbackService.getAllFeedback();
        }
        
        model.addAttribute("feedback", feedback);
        model.addAttribute("averageRating", feedbackService.getOverallAverageRating());
        
        return "customerService/feedback/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "customerService/feedback/create";
    }
    
    @PostMapping("/create")
    public String createFeedback(@Valid @ModelAttribute("feedback") Feedback feedback,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "customerService/feedback/create";
        }
        
        feedbackService.saveFeedback(feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback recorded successfully");
        return "redirect:/customer-service/feedback";
    }
    
    @GetMapping("/{id}")
    public String viewFeedback(@PathVariable Long id, Model model) {
        Feedback feedback = feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID: " + id));
        model.addAttribute("feedback", feedback);
        return "customerService/feedback/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Feedback feedback = feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID: " + id));
        model.addAttribute("feedback", feedback);
        return "customerService/feedback/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateFeedback(@PathVariable Long id,
                               @Valid @ModelAttribute("feedback") Feedback feedback,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "customerService/feedback/edit";
        }
        
        feedback.setId(id);
        feedbackService.saveFeedback(feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback updated successfully");
        return "redirect:/customer-service/feedback";
    }
    
    @GetMapping("/{id}/mark-reviewed")
    public String showMarkReviewedForm(@PathVariable Long id, Model model) {
        Feedback feedback = feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID: " + id));
        model.addAttribute("feedback", feedback);
        return "customerService/feedback/mark-reviewed";
    }
    
    @PostMapping("/{id}/mark-reviewed")
    public String markAsReviewed(@PathVariable Long id,
                               @RequestParam String reviewedBy,
                               @RequestParam String internalNotes,
                               RedirectAttributes redirectAttributes) {
        
        feedbackService.markAsReviewed(id, reviewedBy, internalNotes);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback marked as reviewed");
        return "redirect:/customer-service/feedback/" + id;
    }
    
    @GetMapping("/{id}/add-tags")
    public String showAddTagsForm(@PathVariable Long id, Model model) {
        Feedback feedback = feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID: " + id));
        model.addAttribute("feedback", feedback);
        return "customerService/feedback/add-tags";
    }
    
    @PostMapping("/{id}/add-tags")
    public String addTags(@PathVariable Long id,
                        @RequestParam String tags,
                        RedirectAttributes redirectAttributes) {
        
        if (tags != null && !tags.trim().isEmpty()) {
            List<String> tagList = Arrays.asList(tags.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
            
            feedbackService.addTags(id, tagList);
            redirectAttributes.addFlashAttribute("successMessage", "Tags added successfully");
        }
        
        return "redirect:/customer-service/feedback/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully");
        return "redirect:/customer-service/feedback";
    }
    
    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        Double overallAverage = feedbackService.getOverallAverageRating();
        Map<Integer, Long> distribution = feedbackService.getRatingDistribution();
        
        model.addAttribute("overallAverage", overallAverage);
        model.addAttribute("distribution", distribution);
        
        return "customerService/feedback/analytics";
    }
    
    @GetMapping("/search")
    public String showSearchForm() {
        return "customerService/feedback/search";
    }
    
    @GetMapping("/search-results")
    public String searchFeedback(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String tag,
            Model model) {
        
        List<Feedback> results = null;
        
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            results = feedbackService.getFeedbackBySubmissionDateRange(startDateTime, endDateTime);
            model.addAttribute("searchType", "Date Range");
            model.addAttribute("searchValue", startDate + " to " + endDate);
        } else if (tag != null && !tag.isEmpty()) {
            results = feedbackService.getFeedbackByTag(tag);
            model.addAttribute("searchType", "Tag");
            model.addAttribute("searchValue", tag);
        }
        
        model.addAttribute("feedback", results);
        return "customerService/feedback/search-results";
    }
}