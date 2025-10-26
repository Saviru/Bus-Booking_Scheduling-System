package com.busSystem.BookingSchedule.driver.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.driver.model.TripLog;
import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;
import com.busSystem.BookingSchedule.driver.service.TripLogService;
import com.busSystem.BookingSchedule.driver.service.VehicleConditionReportService;
import com.busSystem.BookingSchedule.operationsManager.service.ScheduleService;
import com.busSystem.BookingSchedule.operationsManager.service.RouteService;
import com.busSystem.BookingSchedule.operationsManager.service.BusService;
import com.busSystem.BookingSchedule.operationsManager.model.Bus;
import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import com.busSystem.BookingSchedule.passenger.service.BookingService;
import com.busSystem.BookingSchedule.passenger.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/driver")
public class DriverController extends Controller {

    @Autowired
    private TripLogService tripLogService;
    
    @Autowired
    private VehicleConditionReportService vehicleConditionReportService;
    
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private BusService busService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("")
    public String driverDashboard(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);

            // Get assigned bus
            Optional<Bus> assignedBus = busService.getBusByDriverId(userId);
            if (assignedBus.isPresent()) {
                model.addAttribute("assignedBus", assignedBus.get());

                // Get schedules for this driver
                List<Schedule> mySchedules = scheduleService.getSchedulesByDriverId(userId);
                model.addAttribute("schedules", mySchedules);

                // Get upcoming schedules (active ones)
                List<Schedule> upcomingSchedules = mySchedules.stream()
                    .filter(s -> "ACTIVE".equals(s.getStatus()))
                    .toList();
                model.addAttribute("upcomingSchedules", upcomingSchedules);

                // Calculate total passengers for today (you can customize the logic)
                if (!mySchedules.isEmpty()) {
                    int totalPassengers = 0;
                    for (Schedule schedule : mySchedules) {
                        List<Booking> bookings = bookingService.getBookingsByScheduleAndDate(
                            schedule.getId(),
                            java.time.LocalDateTime.now().toLocalDate().atStartOfDay()
                        );
                        totalPassengers += bookings.size();
                    }
                    model.addAttribute("totalPassengers", totalPassengers);
                }
            }

            model.addAttribute("userName", session.getAttribute("userName"));
            return "driver/driver-dashboard";
        } catch (Exception e) {
            return handleException(e, model, "driver/driver-dashboard");
        }
    }
    
    // Trip Log CRUD Operations
    @GetMapping("/triplogs")
    public String getAllTripLogs(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            List<TripLog> tripLogs = tripLogService.getAllTripLogsByDriverId(userId);
            model.addAttribute("tripLogs", tripLogs);
            model.addAttribute("userName", session.getAttribute("userName"));
            return "driver/triplog-list";
        } catch (Exception e) {
            return handleException(e, model, "driver/triplog-list");
        }
    }
    
    @GetMapping("/triplogs/new")
    public String createTripLogForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        Long userId = getCurrentUserId(session);

        // Get assigned bus
        Optional<Bus> assignedBus = busService.getBusByDriverId(userId);
        if (assignedBus.isPresent()) {
            model.addAttribute("assignedBus", assignedBus.get());
        }

        model.addAttribute("tripLog", new TripLog());
        model.addAttribute("userName", session.getAttribute("userName"));
        // Only get schedules assigned to this driver with ACTIVE status
        model.addAttribute("schedules", scheduleService.getSchedulesByDriverId(userId));
        model.addAttribute("currentDateTime", java.time.LocalDateTime.now().toString());
        return "driver/triplog-form";
    }
    
    @PostMapping("/triplogs")
    public String saveTripLog(@ModelAttribute TripLog tripLog, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            tripLog.setDriverId(userId);
            tripLogService.save(tripLog);
            return redirectWithSuccess("/driver/triplogs", "Trip log saved successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/driver/triplogs", "Error saving trip log: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/triplogs/edit/{id}")
    public String editTripLogForm(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        Optional<TripLog> tripLog = tripLogService.getById(id);
        if (tripLog.isPresent() && tripLog.get().getDriverId().equals(getCurrentUserId(session))) {
            model.addAttribute("tripLog", tripLog.get());
            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("schedules", scheduleService.getSchedulesByStatus("ACTIVE"));
            model.addAttribute("routes", routeService.getAll());
            return "driver/triplog-edit";
        }
        addErrorMessage(model, "Trip log not found or access denied");
        return "redirect:/driver/triplogs";
    }
    
    @PostMapping("/triplogs/{id}")
    public String updateTripLog(@PathVariable Long id, @ModelAttribute TripLog tripLog, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            tripLog.setId(id);
            tripLog.setDriverId(getCurrentUserId(session));
            tripLogService.updateTripLog(id, tripLog);
            return redirectWithSuccess("/driver/triplogs", "Trip log updated successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/driver/triplogs", "Error updating trip log: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/triplogs/delete/{id}")
    public String deleteTripLog(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Optional<TripLog> tripLog = tripLogService.getById(id);
            if (tripLog.isPresent() && tripLog.get().getDriverId().equals(getCurrentUserId(session))) {
                tripLogService.delete(id);
                return redirectWithSuccess("/driver/triplogs", "Trip log deleted successfully", redirectAttributes);
            }
            return redirectWithError("/driver/triplogs", "Trip log not found or access denied", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/driver/triplogs", "Error deleting trip log: " + e.getMessage(), redirectAttributes);
        }
    }

    // Vehicle Condition Report CRUD Operations
    @GetMapping("/reports")
    public String getAllReports(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            List<VehicleConditionReport> reports = vehicleConditionReportService.getAllReportsByDriverId(userId);
            model.addAttribute("reports", reports);
            return "driver/report-list";
        } catch (Exception e) {
            return handleException(e, model, "driver/report-list");
        }
    }
    
    @GetMapping("/reports/new")
    public String createReportForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        Long userId = getCurrentUserId(session);

        // Get assigned bus
        Optional<Bus> assignedBus = busService.getBusByDriverId(userId);
        if (assignedBus.isPresent()) {
            model.addAttribute("assignedBus", assignedBus.get());
        }

        model.addAttribute("report", new VehicleConditionReport());
        return "driver/report-form";
    }
    
    @PostMapping("/reports")
    public String saveReport(@ModelAttribute VehicleConditionReport report, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            report.setDriverId(userId);
            vehicleConditionReportService.save(report);
            return redirectWithSuccess("/driver/reports", "Report saved successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/driver/reports", "Error saving report: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/reports/edit/{id}")
    public String editReportForm(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        Optional<VehicleConditionReport> report = vehicleConditionReportService.getById(id);
        if (report.isPresent() && report.get().getDriverId().equals(getCurrentUserId(session))) {
            model.addAttribute("report", report.get());
            return "driver/report-edit";
        }
        addErrorMessage(model, "Report not found or access denied");
        return "redirect:/driver/reports";
    }
    
    @PostMapping("/reports/{id}")
    public String updateReport(@PathVariable Long id, @ModelAttribute VehicleConditionReport report, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            report.setId(id);
            report.setDriverId(getCurrentUserId(session));
            vehicleConditionReportService.updateReport(id, report);
            return redirectWithSuccess("/driver/reports", "Report updated successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/driver/reports", "Error updating report: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/reports/delete/{id}")
    public String deleteReport(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "DRIVER");
        if (authCheck != null) return authCheck;

        try {
            Optional<VehicleConditionReport> report = vehicleConditionReportService.getById(id);
            if (report.isPresent() && report.get().getDriverId().equals(getCurrentUserId(session))) {
                vehicleConditionReportService.delete(id);
                return redirectWithSuccess("/driver/reports", "Report deleted successfully", redirectAttributes);
            }
            return redirectWithError("/driver/reports", "Report not found or access denied", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/driver/reports", "Error deleting report: " + e.getMessage(), redirectAttributes);
        }
    }
}
