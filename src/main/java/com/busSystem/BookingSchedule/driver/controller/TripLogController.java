package com.busSystem.BookingSchedule.driver.controller;

import com.busSystem.BookingSchedule.driver.model.TripLog;
import com.busSystem.BookingSchedule.driver.service.TripLogService;
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
@RequestMapping("/driver/trip-logs")
public class TripLogController {

    private final TripLogService tripLogService;
    
    @Autowired
    public TripLogController(TripLogService tripLogService) {
        this.tripLogService = tripLogService;
    }
    
    @GetMapping
    public String listTripLogs(
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) String vehicleNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        
        List<TripLog> tripLogs;
        
        if (driverId != null && !driverId.isEmpty()) {
            tripLogs = tripLogService.getTripLogsByDriverId(driverId);
            model.addAttribute("filterType", "Driver ID");
            model.addAttribute("filterValue", driverId);
        } else if (routeId != null && !routeId.isEmpty()) {
            tripLogs = tripLogService.getTripLogsByRouteId(routeId);
            model.addAttribute("filterType", "Route ID");
            model.addAttribute("filterValue", routeId);
        } else if (vehicleNumber != null && !vehicleNumber.isEmpty()) {
            tripLogs = tripLogService.getTripLogsByVehicleNumber(vehicleNumber);
            model.addAttribute("filterType", "Vehicle Number");
            model.addAttribute("filterValue", vehicleNumber);
        } else if (status != null && !status.isEmpty()) {
            tripLogs = tripLogService.getTripLogsByStatus(status);
            model.addAttribute("filterType", "Status");
            model.addAttribute("filterValue", status);
        } else if (date != null) {
            tripLogs = tripLogService.getTripLogsByDate(date);
            model.addAttribute("filterType", "Date");
            model.addAttribute("filterValue", date);
        } else {
            tripLogs = tripLogService.getAllTripLogs();
        }
        
        model.addAttribute("tripLogs", tripLogs);
        model.addAttribute("today", LocalDate.now());
        
        return "driver/tripLog/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("tripLog", new TripLog());
        return "driver/tripLog/create";
    }
    
    @PostMapping("/create")
    public String createTripLog(@Valid @ModelAttribute("tripLog") TripLog tripLog,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "driver/tripLog/create";
        }
        
        // Validate start and end times
        if (tripLog.getStartTime() != null && tripLog.getEndTime() != null &&
            tripLog.getStartTime().isAfter(tripLog.getEndTime())) {
            result.rejectValue("endTime", "error.tripLog", "End time must be after start time");
            return "driver/tripLog/create";
        }
        
        tripLogService.saveTripLog(tripLog);
        redirectAttributes.addFlashAttribute("successMessage", "Trip log created successfully");
        return "redirect:/driver/trip-logs";
    }
    
    @GetMapping("/{id}")
    public String viewTripLog(@PathVariable Long id, Model model) {
        TripLog tripLog = tripLogService.getTripLogById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trip log ID: " + id));
        model.addAttribute("tripLog", tripLog);
        return "driver/tripLog/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        TripLog tripLog = tripLogService.getTripLogById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trip log ID: " + id));
        model.addAttribute("tripLog", tripLog);
        return "driver/tripLog/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateTripLog(@PathVariable Long id,
                              @Valid @ModelAttribute("tripLog") TripLog tripLog,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "driver/tripLog/edit";
        }
        
        // Validate start and end times
        if (tripLog.getStartTime() != null && tripLog.getEndTime() != null &&
            tripLog.getStartTime().isAfter(tripLog.getEndTime())) {
            result.rejectValue("endTime", "error.tripLog", "End time must be after start time");
            return "driver/tripLog/edit";
        }
        
        tripLog.setId(id);
        tripLogService.saveTripLog(tripLog);
        redirectAttributes.addFlashAttribute("successMessage", "Trip log updated successfully");
        return "redirect:/driver/trip-logs";
    }
    
    @GetMapping("/{id}/delete")
    public String deleteTripLog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tripLogService.deleteTripLog(id);
        redirectAttributes.addFlashAttribute("successMessage", "Trip log deleted successfully");
        return "redirect:/driver/trip-logs";
    }
    
    @GetMapping("/performance")
    public String showPerformanceMetrics(@RequestParam String driverId, Model model) {
        // Get performance metrics for the driver
        Map<String, Object> metrics = tripLogService.getDriverPerformanceMetrics(driverId);
        
        // Get trip logs for the driver
        List<TripLog> driverTrips = tripLogService.getTripLogsByDriverId(driverId);
        
        model.addAttribute("metrics", metrics);
        model.addAttribute("driverId", driverId);
        model.addAttribute("tripLogs", driverTrips);
        
        // Get driver name from the first trip if available
        if (!driverTrips.isEmpty()) {
            model.addAttribute("driverName", driverTrips.get(0).getDriverName());
        }
        
        return "driver/tripLog/performance";
    }
    
    @GetMapping("/search")
    public String showSearchForm() {
        return "driver/tripLog/search";
    }
    
    @GetMapping("/search-results")
    public String searchTripLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            List<TripLog> tripLogs = tripLogService.getTripLogsByDateRange(startDateTime, endDateTime);
            
            model.addAttribute("tripLogs", tripLogs);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        }
        
        return "driver/tripLog/search-results";
    }
}