package com.busSystem.BookingSchedule.operationsManager.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import com.busSystem.BookingSchedule.operationsManager.service.RouteService;
import com.busSystem.BookingSchedule.operationsManager.service.ScheduleService;
import com.busSystem.BookingSchedule.operationsManager.service.BusService;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@org.springframework.stereotype.Controller
@RequestMapping("/operations/schedules")
public class ScheduleController extends Controller {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private UserService userService;

    @Autowired
    private BusService busService;

    @GetMapping
    public String listSchedules(@RequestParam(required = false) Long routeId,
                                @RequestParam(required = false) String status,
                                HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            if (routeId != null) {
                model.addAttribute("schedules", scheduleService.getSchedulesByRouteId(routeId));
            } else if (status != null && !status.isEmpty()) {
                model.addAttribute("schedules", scheduleService.getSchedulesByStatus(status));
            } else {
                model.addAttribute("schedules", scheduleService.getAll());
            }

            model.addAttribute("routes", routeService.getAll());
            model.addAttribute("selectedRouteId", routeId);
            model.addAttribute("selectedStatus", status);
            return "operations/schedules/list";
        } catch (Exception e) {
            return handleException(e, model, "operations/schedules/list");
        }
    }

    @GetMapping("/new")
    public String newScheduleForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        model.addAttribute("schedule", new Schedule());
        model.addAttribute("routes", routeService.getAll());
        model.addAttribute("buses", busService.getBusesByStatus("ACTIVE"));
        model.addAttribute("drivers", userService.getUsersByRole("DRIVER"));
        model.addAttribute("ticketingOfficers", userService.getUsersByRole("TICKETING_OFFICER"));
        return "operations/schedules/form";
    }

    @GetMapping("/{id}/edit")
    public String editScheduleForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        return scheduleService.getById(id)
            .map(schedule -> {
                model.addAttribute("schedule", schedule);
                model.addAttribute("routes", routeService.getAll());
                model.addAttribute("buses", busService.getAll());
                model.addAttribute("drivers", userService.getUsersByRole("DRIVER"));
                model.addAttribute("ticketingOfficers", userService.getUsersByRole("TICKETING_OFFICER"));
                return "operations/schedules/form";
            })
            .orElseGet(() -> redirectWithError("/operations/schedules", "Schedule not found", redirectAttributes));
    }

    @PostMapping
    public String saveSchedule(@ModelAttribute Schedule schedule,
                             @RequestParam Long routeId,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            routeService.getById(routeId).ifPresent(schedule::setRoute);
            scheduleService.save(schedule);
            return redirectWithSuccess("/operations/schedules", "Schedule saved successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/operations/schedules", "Error saving schedule: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        if (scheduleService.exists(id)) {
            try {
                scheduleService.delete(id);
                return redirectWithSuccess("/operations/schedules", "Schedule deleted successfully", redirectAttributes);
            } catch (Exception e) {
                return redirectWithError("/operations/schedules", "Error deleting schedule: " + e.getMessage(), redirectAttributes);
            }
        } else {
            return redirectWithError("/operations/schedules", "Schedule not found", redirectAttributes);
        }
    }
}