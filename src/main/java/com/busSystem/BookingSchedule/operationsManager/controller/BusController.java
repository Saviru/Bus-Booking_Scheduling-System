package com.busSystem.BookingSchedule.operationsManager.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.operationsManager.model.Bus;
import com.busSystem.BookingSchedule.operationsManager.service.BusService;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/operations/buses")
public class BusController extends Controller {

    @Autowired
    private BusService busService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllBuses(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            List<Bus> buses = busService.getAll();
            model.addAttribute("buses", buses);
            model.addAttribute("userName", session.getAttribute("userName"));
            return "operations/buses/bus-list";
        } catch (Exception e) {
            return handleException(e, model, "operations/buses/bus-list");
        }
    }

    @GetMapping("/new")
    public String createBusForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            model.addAttribute("bus", new Bus());
            model.addAttribute("drivers", userService.getUsersByRole("DRIVER"));
            model.addAttribute("ticketingOfficers", userService.getUsersByRole("TICKETING_OFFICER"));
            model.addAttribute("userName", session.getAttribute("userName"));
            return "operations/buses/bus-form";
        } catch (Exception e) {
            return handleException(e, model, "operations/buses/bus-form");
        }
    }

    @PostMapping
    public String saveBus(@ModelAttribute Bus bus, HttpSession session,
                         RedirectAttributes redirectAttributes, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            // Check if bus number already exists
            if (bus.getId() == null) {
                Optional<Bus> existingBus = busService.getBusByBusNumber(bus.getBusNumber());
                if (existingBus.isPresent()) {
                    addErrorMessage(model, "Bus number already exists");
                    model.addAttribute("bus", bus);
                    model.addAttribute("drivers", userService.getUsersByRole("DRIVER"));
                    model.addAttribute("ticketingOfficers", userService.getUsersByRole("TICKETING_OFFICER"));
                    return "operations/buses/bus-form";
                }
            }

            busService.save(bus);
            addSuccessMessage(redirectAttributes, "Bus saved successfully");
            return "redirect:/operations/buses";
        } catch (Exception e) {
            return handleException(e, model, "operations/buses/bus-form");
        }
    }

    @GetMapping("/{id}/edit")
    public String editBusForm(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            Optional<Bus> bus = busService.getById(id);
            if (bus.isEmpty()) {
                addErrorMessage(model, "Bus not found");
                return "redirect:/operations/buses";
            }
            model.addAttribute("bus", bus.get());
            model.addAttribute("drivers", userService.getUsersByRole("DRIVER"));
            model.addAttribute("ticketingOfficers", userService.getUsersByRole("TICKETING_OFFICER"));
            model.addAttribute("userName", session.getAttribute("userName"));
            return "operations/buses/bus-edit";
        } catch (Exception e) {
            return handleException(e, model, "operations/buses/bus-edit");
        }
    }

    @PostMapping("/{id}")
    public String updateBus(@PathVariable Long id, @ModelAttribute Bus bus,
                           HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            bus.setId(id);
            busService.save(bus);
            addSuccessMessage(redirectAttributes, "Bus updated successfully");
            return "redirect:/operations/buses";
        } catch (Exception e) {
            return handleException(e, model, "operations/buses/bus-edit");
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteBus(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            busService.delete(id);
            addSuccessMessage(redirectAttributes, "Bus deleted successfully");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Error deleting bus: " + e.getMessage());
        }
        return "redirect:/operations/buses";
    }
}

