package com.busSystem.BookingSchedule.driver.controller;

import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;
import com.busSystem.BookingSchedule.driver.service.VehicleConditionReportService;
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
import java.util.Map;

@Controller
@RequestMapping("/driver/vehicle-reports")
public class VehicleConditionReportController {

    private final VehicleConditionReportService reportService;
    
    @Autowired
    public VehicleConditionReportController(VehicleConditionReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping
    public String listReports(
            @RequestParam(required = false) String vehicleNumber,
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Boolean needsAttention,
            Model model) {
        
        List<VehicleConditionReport> reports;
        
        if (vehicleNumber != null && !vehicleNumber.isEmpty()) {
            reports = reportService.getReportsByVehicleNumber(vehicleNumber);
            model.addAttribute("filterType", "Vehicle Number");
            model.addAttribute("filterValue", vehicleNumber);
            
            // If filtering by vehicle, get the issues summary
            Map<String, Long> summary = reportService.getVehicleIssuesSummary(vehicleNumber);
            model.addAttribute("summary", summary);
        } else if (driverId != null && !driverId.isEmpty()) {
            reports = reportService.getReportsByDriverId(driverId);
            model.addAttribute("filterType", "Driver ID");
            model.addAttribute("filterValue", driverId);
        } else if (status != null && !status.isEmpty()) {
            reports = reportService.getReportsByStatus(status);
            model.addAttribute("filterType", "Status");
            model.addAttribute("filterValue", status);
        } else if (reportType != null && !reportType.isEmpty()) {
            reports = reportService.getReportsByType(reportType);
            model.addAttribute("filterType", "Report Type");
            model.addAttribute("filterValue", reportType);
        } else if (date != null) {
            reports = reportService.getReportsByDate(date);
            model.addAttribute("filterType", "Date");
            model.addAttribute("filterValue", date);
        } else if (Boolean.TRUE.equals(needsAttention)) {
            reports = reportService.getReportsNeedingAttention();
            model.addAttribute("filterType", "Condition");
            model.addAttribute("filterValue", "Needs Attention");
        } else {
            reports = reportService.getAllReports();
        }
        
        model.addAttribute("reports", reports);
        model.addAttribute("today", LocalDate.now());
        
        return "driver/vehicleCondition/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(
            @RequestParam(required = false) String vehicleNumber,
            Model model) {
        VehicleConditionReport report = new VehicleConditionReport();
        
        // Set default values
        report.setReportDate(LocalDateTime.now());
        
        // If vehicleNumber is provided, pre-populate it
        if (vehicleNumber != null && !vehicleNumber.isEmpty()) {
            report.setVehicleNumber(vehicleNumber);
        }
        
        model.addAttribute("report", report);
        return "driver/vehicleCondition/create";
    }
    
    @PostMapping("/create")
    public String createReport(@Valid @ModelAttribute("report") VehicleConditionReport report,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        if (result.hasErrors()) {
            // Log validation errors for debugging
            result.getAllErrors().forEach(error -> 
                System.out.println("Validation error: " + error.getDefaultMessage()));
            return "driver/vehicleCondition/create";
        }
        
        try {
            // Ensure report date is set if not provided
            if (report.getReportDate() == null) {
                report.setReportDate(LocalDateTime.now());
            }
            
            reportService.saveReport(report);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle condition report submitted successfully");
            return "redirect:/driver/vehicle-reports";
        } catch (Exception e) {
            // Log the exception for debugging
            System.out.println("Error saving report: " + e.getMessage());
            model.addAttribute("errorMessage", "Error saving report: " + e.getMessage());
            return "driver/vehicleCondition/create";
        }
    }
    
    @GetMapping("/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        VehicleConditionReport report = reportService.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID: " + id));
        model.addAttribute("report", report);
        return "driver/vehicleCondition/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        VehicleConditionReport report = reportService.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID: " + id));
        model.addAttribute("report", report);
        return "driver/vehicleCondition/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateReport(@PathVariable Long id,
                             @Valid @ModelAttribute("report") VehicleConditionReport report,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "driver/vehicleCondition/edit";
        }
        
        report.setId(id);
        reportService.saveReport(report);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle condition report updated successfully");
        return "redirect:/driver/vehicle-reports";
    }
    
    @GetMapping("/{id}/update-status")
    public String showUpdateStatusForm(@PathVariable Long id, Model model) {
        VehicleConditionReport report = reportService.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID: " + id));
        model.addAttribute("report", report);
        return "driver/vehicleCondition/update-status";
    }
    
    @PostMapping("/{id}/update-status")
    public String updateReportStatus(@PathVariable Long id,
                                   @RequestParam String status,
                                   @RequestParam String reviewedBy,
                                   @RequestParam String maintenanceNotes,
                                   @RequestParam String followUpAction,
                                   RedirectAttributes redirectAttributes) {
        
        reportService.updateReportStatus(id, status, reviewedBy, maintenanceNotes, followUpAction);
        redirectAttributes.addFlashAttribute("successMessage", "Report status updated successfully");
        return "redirect:/driver/vehicle-reports/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.deleteReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle condition report deleted successfully");
        return "redirect:/driver/vehicle-reports";
    }
    
    @GetMapping("/vehicle-history/{vehicleNumber}")
    public String viewVehicleHistory(@PathVariable String vehicleNumber, Model model) {
        List<VehicleConditionReport> reports = reportService.getReportsByVehicleNumber(vehicleNumber);
        Map<String, Long> summary = reportService.getVehicleIssuesSummary(vehicleNumber);
        
        model.addAttribute("reports", reports);
        model.addAttribute("vehicleNumber", vehicleNumber);
        model.addAttribute("summary", summary);
        
        return "driver/vehicleCondition/vehicle-history";
    }
    
    @GetMapping("/search")
    public String showSearchForm() {
        return "driver/vehicleCondition/search";
    }
    
    @GetMapping("/search-results")
    public String searchReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String component,
            @RequestParam(required = false) String condition,
            Model model) {
        
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            List<VehicleConditionReport> reports = reportService.getReportsByDateRange(startDateTime, endDateTime);
            
            // If component and condition are specified, filter the reports
            if (component != null && !component.isEmpty() && condition != null && !condition.isEmpty()) {
                reports = filterReportsByComponentCondition(reports, component, condition);
                model.addAttribute("component", component);
                model.addAttribute("condition", condition);
            }
            
            model.addAttribute("reports", reports);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        }
        
        return "driver/vehicleCondition/search-results";
    }
    
    // Helper method to filter reports by component condition
    private List<VehicleConditionReport> filterReportsByComponentCondition(
            List<VehicleConditionReport> reports, String component, String condition) {
        
        return reports.stream()
                .filter(report -> {
                    switch (component) {
                        case "brakes":
                            return condition.equals(report.getBrakesCondition());
                        case "lights":
                            return condition.equals(report.getLightsCondition());
                        case "tires":
                            return condition.equals(report.getTiresCondition());
                        case "exterior":
                            return condition.equals(report.getExteriorCondition());
                        case "interior":
                            return condition.equals(report.getInteriorCondition());
                        default:
                            return false;
                    }
                })
                .toList();
    }
}