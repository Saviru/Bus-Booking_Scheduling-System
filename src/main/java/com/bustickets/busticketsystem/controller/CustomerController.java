package com.bustickets.busticketsystem.controller;

import com.bustickets.busticketsystem.model.*;
import com.bustickets.busticketsystem.service.BusRouteService;
import com.bustickets.busticketsystem.service.BusScheduleService;
import com.bustickets.busticketsystem.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private BusRouteService busRouteService;

    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("routes", busRouteService.getAllRoutes());
        model.addAttribute("reservations", reservationService.getReservationsByUser(user));
        return "customer/dashboard";
    }

    @GetMapping("/book")
    public String bookTicket(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("routes", busRouteService.getAllRoutes());
        return "customer/book";
    }

    @GetMapping("/schedules")
    public String getSchedules(@RequestParam Long routeId, Model model) {
        BusRoute busRoute = busRouteService.getRouteById(routeId).orElse(null);
        if (busRoute != null) {
            model.addAttribute("schedules", busScheduleService.getSchedulesByRoute(busRoute));
            model.addAttribute("route", busRoute);
        }
        return "customer/schedules";
    }

    @PostMapping("/book")
    public String confirmBooking(
            @RequestParam Long scheduleId,
            @RequestParam String passengerName,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        BusSchedule schedule = busScheduleService.getScheduleById(scheduleId).orElse(null);
        if (schedule != null) {
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setSchedule(schedule);
            reservation.setPassengerName(passengerName);
            reservationService.saveReservation(reservation);
            return "redirect:/customer/dashboard";
        }
        
        return "redirect:/customer/book";
    }

    @GetMapping("/my-tickets")
    public String myTickets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("reservations", reservationService.getReservationsByUser(user));
        return "customer/my-tickets";
    }
}