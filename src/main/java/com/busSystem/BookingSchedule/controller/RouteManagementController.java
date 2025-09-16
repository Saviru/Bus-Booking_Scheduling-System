package com.busSystem.BookingSchedule.controller;

import com.busSystem.BookingSchedule.model.OperationsManager;
import com.busSystem.BookingSchedule.model.Route;
import com.busSystem.BookingSchedule.service.RouteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operations/routes")
public class RouteManagementController {
    
    @Autowired
    private RouteService routeService;
    
    // Helper method to get logged in manager
    private OperationsManager getLoggedInManager(HttpSession session) {
        return (OperationsManager) session.getAttribute("loggedInOperationsManager");
    }
    
    // Route list page with filtering and search
    @GetMapping
    public String listRoutes(@RequestParam(required = false) String status,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String origin,
                            @RequestParam(required = false) String destination,
                            HttpSession session,
                            Model model) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        List<Route> routes;
        
        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            routes = routeService.searchAllRoutes(search.trim());
        } else if (status != null && !status.isEmpty()) {
            Route.RouteStatus routeStatus = Route.RouteStatus.valueOf(status);
            routes = routeService.getRoutesByStatus(routeStatus);
        } else {
            routes = routeService.getAllRoutes();
        }
        
        // Additional filtering by origin/destination if specified
        if (origin != null && !origin.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getOrigin().equalsIgnoreCase(origin))
                    .toList();
        }
        
        if (destination != null && !destination.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getDestination().equalsIgnoreCase(destination))
                    .toList();
        }
        
        model.addAttribute("routes", routes);
        model.addAttribute("statuses", Route.RouteStatus.values());
        model.addAttribute("origins", routeService.getAllOrigins());
        model.addAttribute("destinations", routeService.getAllDestinations());
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentOrigin", origin);
        model.addAttribute("currentDestination", destination);
        
        // Statistics
        model.addAttribute("totalRoutes", routeService.getTotalRoutes());
        model.addAttribute("activeRoutes", routeService.getActiveRouteCount());
        model.addAttribute("inactiveRoutes", routeService.getInactiveRouteCount());
        model.addAttribute("suspendedRoutes", routeService.getSuspendedRouteCount());
        
        return "operations/routes/list";
    }
    
    // Route details view
    @GetMapping("/{id}")
    public String viewRoute(@PathVariable Long id, HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        Optional<Route> route = routeService.findById(id);
        if (route.isEmpty()) {
            return "redirect:/operations/routes";
        }
        
        model.addAttribute("route", route.get());
        return "operations/routes/details";
    }
    
    // Create route page
    @GetMapping("/create")
    public String createRoutePage(HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        model.addAttribute("route", new Route());
        model.addAttribute("statuses", Route.RouteStatus.values());
        return "operations/routes/create";
    }
    
    // Process route creation
    @PostMapping("/create")
    public String createRoute(@Valid @ModelAttribute Route route,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("route", route);
            model.addAttribute("statuses", Route.RouteStatus.values());
            return "operations/routes/create";
        }
        
        // Check for conflicts
        if (routeService.hasConflicts(route)) {
            model.addAttribute("error", "A route with the same origin and destination already exists");
            model.addAttribute("route", route);
            model.addAttribute("statuses", Route.RouteStatus.values());
            return "operations/routes/create";
        }
        
        if (routeService.createRoute(route)) {
            redirectAttributes.addFlashAttribute("success", "Route created successfully!");
            return "redirect:/operations/routes";
        } else {
            model.addAttribute("error", "Failed to create route. Route code may already exist.");
            model.addAttribute("route", route);
            model.addAttribute("statuses", Route.RouteStatus.values());
            return "operations/routes/create";
        }
    }
    
    // Edit route page
    @GetMapping("/{id}/edit")
    public String editRoutePage(@PathVariable Long id, HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        Optional<Route> route = routeService.findById(id);
        if (route.isEmpty()) {
            return "redirect:/operations/routes";
        }
        
        model.addAttribute("route", route.get());
        model.addAttribute("statuses", Route.RouteStatus.values());
        return "operations/routes/edit";
    }
    
    // Process route update
    @PostMapping("/{id}/edit")
    public String editRoute(@PathVariable Long id,
                           @Valid @ModelAttribute Route route,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        route.setId(id);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("route", route);
            model.addAttribute("statuses", Route.RouteStatus.values());
            return "operations/routes/edit";
        }
        
        if (routeService.updateRoute(route)) {
            redirectAttributes.addFlashAttribute("success", "Route updated successfully!");
            return "redirect:/operations/routes/" + id;
        } else {
            model.addAttribute("error", "Failed to update route. Route code may already exist.");
            model.addAttribute("route", route);
            model.addAttribute("statuses", Route.RouteStatus.values());
            return "operations/routes/edit";
        }
    }
    
    // Change route status
    @PostMapping("/{id}/status")
    public String changeRouteStatus(@PathVariable Long id,
                                   @RequestParam String status,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        try {
            Route.RouteStatus newStatus = Route.RouteStatus.valueOf(status);
            if (routeService.updateRouteStatus(id, newStatus)) {
                redirectAttributes.addFlashAttribute("success", "Route status updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update route status");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid status");
        }
        
        return "redirect:/operations/routes/" + id;
    }
    
    // Bulk status update
    @PostMapping("/bulk-status")
    public String bulkStatusUpdate(@RequestParam List<Long> routeIds,
                                  @RequestParam String status,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        try {
            Route.RouteStatus newStatus = Route.RouteStatus.valueOf(status);
            if (routeService.bulkUpdateStatus(routeIds, newStatus)) {
                redirectAttributes.addFlashAttribute("success", 
                    "Updated status for " + routeIds.size() + " routes successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update route statuses");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid status");
        }
        
        return "redirect:/operations/routes";
    }
    
    // Soft delete (deactivate) route
    @PostMapping("/{id}/deactivate")
    public String deactivateRoute(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        if (routeService.deactivateRoute(id)) {
            redirectAttributes.addFlashAttribute("success", "Route deactivated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to deactivate route");
        }
        
        return "redirect:/operations/routes";
    }
    
    // Reactivate route
    @PostMapping("/{id}/reactivate")
    public String reactivateRoute(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        if (routeService.reactivateRoute(id)) {
            redirectAttributes.addFlashAttribute("success", "Route reactivated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to reactivate route");
        }
        
        return "redirect:/operations/routes/" + id;
    }
    
    // Hard delete route (admin only)
    @PostMapping("/{id}/delete")
    public String deleteRoute(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        // Only admin can hard delete
        if (loggedInManager.getRole() != OperationsManager.ManagerRole.ADMIN) {
            redirectAttributes.addFlashAttribute("error", "Insufficient permissions");
            return "redirect:/operations/routes/" + id;
        }
        
        if (routeService.deleteRoute(id)) {
            redirectAttributes.addFlashAttribute("success", "Route deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete route");
        }
        
        return "redirect:/operations/routes";
    }
    
    // AJAX endpoints for validation and data
    @GetMapping("/check-route-code")
    @ResponseBody
    public boolean checkRouteCode(@RequestParam String routeCode,
                                 @RequestParam(required = false) Long excludeId) {
        if (excludeId != null) {
            return routeService.isRouteCodeAvailable(routeCode, excludeId);
        }
        return routeService.isRouteCodeAvailable(routeCode);
    }
    
    @GetMapping("/check-conflicts")
    @ResponseBody
    public boolean checkConflicts(@RequestParam String origin,
                                 @RequestParam String destination,
                                 @RequestParam(required = false) Long excludeId) {
        Route tempRoute = new Route();
        tempRoute.setOrigin(origin);
        tempRoute.setDestination(destination);
        if (excludeId != null) {
            tempRoute.setId(excludeId);
        }
        return !routeService.hasConflicts(tempRoute);
    }
    
    // Export routes (placeholder for future implementation)
    @GetMapping("/export")
    public String exportRoutes(@RequestParam(required = false) String format,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        // Placeholder for export functionality
        redirectAttributes.addFlashAttribute("info", "Export functionality will be implemented soon");
        return "redirect:/operations/routes";
    }
}