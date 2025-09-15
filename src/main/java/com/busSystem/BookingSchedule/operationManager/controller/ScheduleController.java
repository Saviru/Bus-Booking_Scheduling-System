package com.busSystem.BookingSchedule.operationManager.controller;

import com.busSystem.BookingSchedule.operationManager.model.Route;
import com.busSystem.BookingSchedule.operationManager.model.Schedule;
import com.busSystem.BookingSchedule.operationManager.service.RouteService;
import com.busSystem.BookingSchedule.operationManager.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final RouteService routeService;
    
    @Autowired
    public ScheduleController(ScheduleService scheduleService, RouteService routeService) {
        this.scheduleService = scheduleService;
        this.routeService = routeService;
    }
    
    @GetMapping
    public String listSchedules(Model model,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               @RequestParam(required = false) Long routeId) {
        
        List<Schedule> schedules;
        
        if (status != null && !status.isEmpty()) {
            schedules = scheduleService.getSchedulesByStatus(status);
        } else if (date != null) {
            schedules = scheduleService.getSchedulesByDate(date);
        } else if (routeId != null) {
            schedules = scheduleService.getSchedulesByRouteId(routeId);
        } else {
            schedules = scheduleService.getAllSchedules();
        }
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("today", LocalDate.now());
        return "operationManager/schedule/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("routes", routeService.getAllRoutes());
        return "operationManager/schedule/create";
    }
    
    @PostMapping("/create")
    public String createSchedule(@Valid @ModelAttribute("schedule") Schedule schedule,
                                BindingResult result,
                                @RequestParam Long routeId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime departureTime,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime arrivalTime,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        if (scheduleService.isScheduleIdExists(schedule.getScheduleId())) {
            result.rejectValue("scheduleId", "error.schedule", "Schedule ID already exists");
        }
        
        // Validate times
        if (departureTime != null && arrivalTime != null && departureTime.isAfter(arrivalTime)) {
            result.rejectValue("arrivalTime", "error.schedule", "Arrival time must be after departure time");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            return "operationManager/schedule/create";
        }
        
        // Set the route
        routeService.getRouteById(routeId).ifPresent(schedule::setRoute);
        
        scheduleService.saveSchedule(schedule);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule created successfully");
        return "redirect:/schedules";
    }
    
    @GetMapping("/{id}")
    public String viewSchedule(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));
        model.addAttribute("schedule", schedule);
        return "operationManager/schedule/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));
        
        model.addAttribute("schedule", schedule);
        model.addAttribute("routes", routeService.getAllRoutes());
        return "operationManager/schedule/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateSchedule(@PathVariable Long id,
                               @Valid @ModelAttribute("schedule") Schedule schedule,
                               BindingResult result,
                               @RequestParam Long routeId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime departureTime,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime arrivalTime,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        
        Schedule existingSchedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));
        
        // Check if scheduleId is being changed and if it already exists
        if (!existingSchedule.getScheduleId().equals(schedule.getScheduleId()) && 
            scheduleService.isScheduleIdExists(schedule.getScheduleId())) {
            result.rejectValue("scheduleId", "error.schedule", "Schedule ID already exists");
        }
        
        // Validate times
        if (departureTime != null && arrivalTime != null && departureTime.isAfter(arrivalTime)) {
            result.rejectValue("arrivalTime", "error.schedule", "Arrival time must be after departure time");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            return "operationManager/schedule/edit";
        }
        
        // Set the route
        routeService.getRouteById(routeId).ifPresent(schedule::setRoute);
        
        schedule.setId(id);
        scheduleService.saveSchedule(schedule);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully");
        return "redirect:/schedules";
    }
    
    @GetMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        scheduleService.deleteSchedule(id);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully");
        return "redirect:/schedules";
    }
    
    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("routes", routeService.getAllRoutes());
        return "operationManager/schedule/report";
    }
    
    @GetMapping("/generate-report")
    public String generateReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               @RequestParam(required = false) Long routeId,
                               Model model) {
        
        List<Schedule> schedules = scheduleService.getSchedulesByDateRange(startDate, endDate);
        
        if (routeId != null) {
            // Filter by route if specified
            schedules = schedules.stream()
                .filter(s -> s.getRoute() != null && s.getRoute().getId().equals(routeId))
                .toList();
        }
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("routeId", routeId);
        
        // Get the route details if routeId is provided
        if (routeId != null) {
            routeService.getRouteById(routeId).ifPresent(route -> model.addAttribute("route", route));
        }
        
        return "operationManager/schedule/report-results";
    }
}