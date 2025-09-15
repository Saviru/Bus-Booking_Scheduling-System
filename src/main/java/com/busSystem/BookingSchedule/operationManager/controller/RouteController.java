package com.busSystem.BookingSchedule.operationManager.controller;

import com.busSystem.BookingSchedule.operationManager.model.Route;
import com.busSystem.BookingSchedule.operationManager.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;
    
    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }
    
    @GetMapping
    public String listRoutes(Model model, 
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String location) {
        List<Route> routes;
        
        if (status != null && !status.isEmpty()) {
            routes = routeService.getRoutesByStatus(status);
        } else if (location != null && !location.isEmpty()) {
            routes = routeService.searchRoutesByLocation(location);
        } else {
            routes = routeService.getAllRoutes();
        }
        
        model.addAttribute("routes", routes);
        return "operationManager/route/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("route", new Route());
        return "operationManager/route/create";
    }
    
    @PostMapping("/create")
    public String createRoute(@Valid @ModelAttribute("route") Route route,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        
        if (routeService.isRouteIdExists(route.getRouteId())) {
            result.rejectValue("routeId", "error.route", "Route ID already exists");
        }
        
        if (result.hasErrors()) {
            return "operationManager/route/create";
        }
        
        routeService.saveRoute(route);
        redirectAttributes.addFlashAttribute("successMessage", "Route created successfully");
        return "redirect:/routes";
    }
    
    @GetMapping("/{id}")
    public String viewRoute(@PathVariable Long id, Model model) {
        Route route = routeService.getRouteById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID: " + id));
        model.addAttribute("route", route);
        return "operationManager/route/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Route route = routeService.getRouteById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID: " + id));
        model.addAttribute("route", route);
        return "operationManager/route/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateRoute(@PathVariable Long id, 
                             @Valid @ModelAttribute("route") Route route,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        
        Route existingRoute = routeService.getRouteById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID: " + id));
        
        // Check if routeId is being changed and if it already exists
        if (!existingRoute.getRouteId().equals(route.getRouteId()) && 
            routeService.isRouteIdExists(route.getRouteId())) {
            result.rejectValue("routeId", "error.route", "Route ID already exists");
        }
        
        if (result.hasErrors()) {
            return "operationManager/route/edit";
        }
        
        route.setId(id);
        routeService.saveRoute(route);
        redirectAttributes.addFlashAttribute("successMessage", "Route updated successfully");
        return "redirect:/routes";
    }
    
    @GetMapping("/{id}/delete")
    public String deleteRoute(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        routeService.deleteRoute(id);
        redirectAttributes.addFlashAttribute("successMessage", "Route deleted successfully");
        return "redirect:/routes";
    }
}