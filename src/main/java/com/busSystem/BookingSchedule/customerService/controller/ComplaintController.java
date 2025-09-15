package com.busSystem.BookingSchedule.customerService.controller;

import com.busSystem.BookingSchedule.customerService.model.Complaint;
import com.busSystem.BookingSchedule.customerService.service.ComplaintService;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@RequestMapping("/customer-service/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    
    @Autowired
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }
    
    @GetMapping
    public String listComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String priority,
            Model model) {
        
        List<Complaint> complaints;
        
        if (status != null && !status.isEmpty()) {
            complaints = complaintService.getComplaintsByStatus(status);
            model.addAttribute("filterType", "Status");
            model.addAttribute("filterValue", status);
        } else if (category != null && !category.isEmpty()) {
            complaints = complaintService.getComplaintsByCategory(category);
            model.addAttribute("filterType", "Category");
            model.addAttribute("filterValue", category);
        } else if (customerName != null && !customerName.isEmpty()) {
            complaints = complaintService.searchComplaintsByCustomerName(customerName);
            model.addAttribute("filterType", "Customer Name");
            model.addAttribute("filterValue", customerName);
        } else if (priority != null && !priority.isEmpty()) {
            complaints = complaintService.getComplaintsByPriority(priority);
            model.addAttribute("filterType", "Priority");
            model.addAttribute("filterValue", priority);
        } else {
            complaints = complaintService.getAllComplaints();
        }
        
        model.addAttribute("complaints", complaints);
        model.addAttribute("categories", complaintService.getAllCategories());
        
        return "customerService/complaint/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("complaint", new Complaint());
        model.addAttribute("categories", complaintService.getAllCategories());
        return "customerService/complaint/create";
    }
    
    @PostMapping("/create")
    public String createComplaint(@Valid @ModelAttribute("complaint") Complaint complaint,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", complaintService.getAllCategories());
            return "customerService/complaint/create";
        }
        
        // Set any default values or generate reference number in the service
        Complaint savedComplaint = complaintService.saveComplaint(complaint);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Complaint registered successfully with reference number: " + savedComplaint.getReferenceNumber());
        return "redirect:/customer-service/complaints";
    }
    
    @GetMapping("/{id}")
    public String viewComplaint(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid complaint ID: " + id));
        model.addAttribute("complaint", complaint);
        return "customerService/complaint/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid complaint ID: " + id));
        model.addAttribute("complaint", complaint);
        model.addAttribute("categories", complaintService.getAllCategories());
        return "customerService/complaint/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateComplaint(@PathVariable Long id,
                                @Valid @ModelAttribute("complaint") Complaint complaint,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", complaintService.getAllCategories());
            return "customerService/complaint/edit";
        }
        
        complaint.setId(id);
        complaintService.saveComplaint(complaint);
        redirectAttributes.addFlashAttribute("successMessage", "Complaint updated successfully");
        return "redirect:/customer-service/complaints";
    }
    
    @GetMapping("/{id}/update-status")
    public String showUpdateStatusForm(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid complaint ID: " + id));
        model.addAttribute("complaint", complaint);
        return "customerService/complaint/update-status";
    }
    
    @PostMapping("/{id}/update-status")
    public String updateComplaintStatus(@PathVariable Long id,
                                      @RequestParam String status,
                                      @RequestParam String resolutionNotes,
                                      @RequestParam String assignedTo,
                                      RedirectAttributes redirectAttributes) {
        
        Complaint updatedComplaint = complaintService.updateComplaintStatus(id, status, resolutionNotes, assignedTo);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Complaint status updated to: " + updatedComplaint.getStatus());
        return "redirect:/customer-service/complaints/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deleteComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        complaintService.deleteComplaint(id);
        redirectAttributes.addFlashAttribute("successMessage", "Complaint deleted successfully");
        return "redirect:/customer-service/complaints";
    }
    
    @GetMapping("/search")
    public String showSearchForm() {
        return "customerService/complaint/search";
    }
    
    @GetMapping("/search-results")
    public String searchComplaints(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String referenceNumber,
            Model model) {
        
        AtomicReference<List<Complaint>> complaints = new AtomicReference<>();
        
        if (referenceNumber != null && !referenceNumber.isEmpty()) {
            // Search by reference number
            complaintService.getComplaintByReferenceNumber(referenceNumber)
                    .ifPresent(complaint -> {
                        complaints.set(List.of(complaint));
                        model.addAttribute("searchType", "Reference Number");
                        model.addAttribute("searchValue", referenceNumber);
                    });
        } else if (startDate != null && endDate != null && searchType != null) {
            // Search by date range
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            if ("incident".equals(searchType)) {
                complaints.set(complaintService.getComplaintsByIncidentDateRange(startDateTime, endDateTime));
                model.addAttribute("searchType", "Incident Date");
            } else if ("submission".equals(searchType)) {
                complaints.set(complaintService.getComplaintsBySubmissionDateRange(startDateTime, endDateTime));
                model.addAttribute("searchType", "Submission Date");
            }
            
            model.addAttribute("searchValue", startDate + " to " + endDate);
        }
        
        if (complaints.get() != null) {
            model.addAttribute("complaints", complaints);
        }
        
        return "customerService/complaint/search-results";
    }
    
    @GetMapping("/cleanup")
    public String cleanupOldComplaints(@RequestParam int retentionDays, RedirectAttributes redirectAttributes) {
        complaintService.cleanupOldResolvedComplaints(retentionDays);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Cleanup completed. Removed resolved complaints older than " + retentionDays + " days.");
        return "redirect:/customer-service/complaints";
    }
}