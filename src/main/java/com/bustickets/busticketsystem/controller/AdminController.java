package com.bustickets.busticketsystem.controller;

import com.bustickets.busticketsystem.model.BusRoute;
import com.bustickets.busticketsystem.model.BusSchedule;
import com.bustickets.busticketsystem.model.User;
import com.bustickets.busticketsystem.service.BusRouteService;
import com.bustickets.busticketsystem.service.BusScheduleService;
import com.bustickets.busticketsystem.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BusRouteService busRouteService;

    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("routes", busRouteService.getAllRoutes());
        model.addAttribute("schedules", busScheduleService.getAllSchedules());
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/dashboard";
    }

    @GetMapping("/routes")
    public String routesPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("routes", busRouteService.getAllRoutes());
        model.addAttribute("newRoute", new BusRoute());
        return "admin/routes";
    }

    @PostMapping("/routes/add")
    public String addRoute(@ModelAttribute BusRoute busRoute, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        busRouteService.saveRoute(busRoute);
        return "redirect:/admin/routes";
    }

    @GetMapping("/schedules")
    public String schedulesPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("routes", busRouteService.getAllRoutes());
        model.addAttribute("schedules", busScheduleService.getAllSchedules());
        return "admin/schedules";
    }

    @PostMapping("/schedules/add")
    public String addSchedule(@RequestParam Long routeId, @RequestParam String departureTime, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        BusRoute busRoute = busRouteService.getRouteById(routeId).orElse(null);
        if (busRoute != null) {
            LocalTime time = LocalTime.parse(departureTime);
            BusSchedule schedule = new BusSchedule();
            schedule.setBusRoute(busRoute);
            schedule.setDepartureTime(time);
            busScheduleService.saveSchedule(schedule);
        }
        return "redirect:/admin/schedules";
    }

    @GetMapping("/reservations")
    public String reservationsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/reservations";
    }
}