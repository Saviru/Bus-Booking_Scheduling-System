package com.busSystem.BookingSchedule.operationsManager.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.operationsManager.model.Route;
import com.busSystem.BookingSchedule.operationsManager.service.RouteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/operations/routes")
public class RouteController extends Controller {

    @Autowired
    private RouteService routeService;
    
    @GetMapping
    public String listRoutes(@RequestParam(required = false) String status,
                             @RequestParam(required = false) String search,
                             HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            if (status != null && !status.isEmpty()) {
                model.addAttribute("routes", routeService.getRoutesByStatus(status));
            } else if (search != null && !search.isEmpty()) {
                model.addAttribute("routes", routeService.searchRoutes(search));
            } else {
                model.addAttribute("routes", routeService.getAll());
            }

            model.addAttribute("selectedStatus", status);
            model.addAttribute("searchTerm", search);
            return "operations/routes/list";
        } catch (Exception e) {
            return handleException(e, model, "operations/routes/list");
        }
    }
    
    @GetMapping("/new")
    public String newRouteForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        model.addAttribute("route", new Route());
        return "operations/routes/form";
    }
    
    @GetMapping("/{id}/edit")
    public String editRouteForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        Optional<Route> routeOpt = routeService.getById(id);
        if (routeOpt.isPresent()) {
            model.addAttribute("route", routeOpt.get());
            return "operations/routes/form";
        } else {
            return redirectWithError("/operations/routes", "Route not found", redirectAttributes);
        }
    }
    
    @PostMapping
    public String saveRoute(@ModelAttribute Route route, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            routeService.save(route);
            return redirectWithSuccess("/operations/routes", "Route saved successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/operations/routes", "Error saving route: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/{id}/delete")
    public String deleteRoute(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        if (routeService.exists(id)) {
            try {
                routeService.delete(id);
                return redirectWithSuccess("/operations/routes", "Route deleted successfully", redirectAttributes);
            } catch (Exception e) {
                return redirectWithError("/operations/routes", "Error deleting route: " + e.getMessage(), redirectAttributes);
            }
        } else {
            return redirectWithError("/operations/routes", "Route not found", redirectAttributes);
        }
    }
}