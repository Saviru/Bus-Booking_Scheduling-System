package com.busSystem.BookingSchedule.ticketingOfficer.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.ticketingOfficer.model.Fare;
import com.busSystem.BookingSchedule.ticketingOfficer.service.FareService;
import com.busSystem.BookingSchedule.passenger.model.Booking;
import com.busSystem.BookingSchedule.passenger.service.BookingService;
import com.busSystem.BookingSchedule.operationsManager.service.RouteService;
import com.busSystem.BookingSchedule.operationsManager.service.BusService;
import com.busSystem.BookingSchedule.operationsManager.service.ScheduleService;
import com.busSystem.BookingSchedule.operationsManager.model.Bus;
import com.busSystem.BookingSchedule.operationsManager.model.Route;
import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import com.busSystem.BookingSchedule.itSupport.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequestMapping("/ticketing")
public class TicketingController extends Controller {

    @Autowired
    private FareService fareService;
    
    @Autowired
    private BookingService bookingService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private BusService busService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String ticketingDashboard(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);

            // Get buses assigned to this ticketing officer
            java.util.List<Bus> assignedBuses = busService.getBusesByTicketingOfficerId(userId);
            model.addAttribute("assignedBuses", assignedBuses);

            // Get tickets for assigned buses
            java.util.List<Booking> myTickets = new java.util.ArrayList<>();
            for (Bus bus : assignedBuses) {
                myTickets.addAll(bookingService.getBookingsByBus(bus.getId()));
            }

            model.addAttribute("userName", session.getAttribute("userName"));
            model.addAttribute("totalTickets", myTickets.size());
            model.addAttribute("totalFares", fareService.getAll().size());
            return "ticketing/dashboard";
        } catch (Exception e) {
            return handleException(e, model, "ticketing/dashboard");
        }
    }
    
    // Ticket/Booking Management (tickets and bookings are the same)
    @GetMapping("/tickets")
    public String listTickets(Model model, HttpSession session) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);

            // Get buses assigned to this ticketing officer
            java.util.List<Bus> assignedBuses = busService.getBusesByTicketingOfficerId(userId);

            // Get tickets for assigned buses only
            java.util.List<Booking> myTickets = new java.util.ArrayList<>();
            for (Bus bus : assignedBuses) {
                myTickets.addAll(bookingService.getBookingsByBus(bus.getId()));
            }

            model.addAttribute("tickets", myTickets);
            return "ticketing/tickets/list";
        } catch (Exception e) {
            return handleException(e, model, "ticketing/tickets/list");
        }
    }

    @GetMapping("/tickets/{id}/confirm")
    public String confirmTicket(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            var bookingOpt = bookingService.getById(id);
            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                booking.setStatus("CONFIRMED");
                bookingService.save(booking);
                return redirectWithSuccess("/ticketing/tickets", "Ticket confirmed successfully", redirectAttributes);
            }
            return redirectWithError("/ticketing/tickets", "Ticket not found", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/ticketing/tickets", "Error confirming ticket: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/tickets/{id}/cancel")
    public String cancelTicket(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            var bookingOpt = bookingService.getById(id);
            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                booking.setStatus("CANCELLED");
                bookingService.save(booking);
                return redirectWithSuccess("/ticketing/tickets", "Ticket cancelled successfully", redirectAttributes);
            }
            return redirectWithError("/ticketing/tickets", "Ticket not found", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/ticketing/tickets", "Error cancelling ticket: " + e.getMessage(), redirectAttributes);
        }
    }

    // Fare Management
    @GetMapping("/fares")
    public String listFares(Model model, HttpSession session) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            model.addAttribute("fares", fareService.getAll());
            return "ticketing/fares/list";
        } catch (Exception e) {
            return handleException(e, model, "ticketing/fares/list");
        }
    }
    
    @GetMapping("/fares/new")
    public String newFareForm(Model model, HttpSession session) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        model.addAttribute("fare", new Fare());
        model.addAttribute("routes", routeService.getAll());
        return "ticketing/fares/form";
    }

    @GetMapping("/fares/edit/{id}")
    public String editFareForm(@PathVariable Long id, Model model, HttpSession session) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            var fareOpt = fareService.getById(id);
            if (fareOpt.isPresent()) {
                model.addAttribute("fare", fareOpt.get());
                model.addAttribute("routes", routeService.getAll());
                return "ticketing/fares/form";
            }
            return "redirect:/ticketing/fares";
        } catch (Exception e) {
            return handleException(e, model, "ticketing/fares/form");
        }
    }
    
    @PostMapping("/fares")
    public String saveFare(@ModelAttribute Fare fare, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            fareService.save(fare);
            return redirectWithSuccess("/ticketing/fares", "Fare saved successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/ticketing/fares", "Error saving fare: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/fares/delete/{id}")
    public String deleteFare(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            fareService.delete(id);
            return redirectWithSuccess("/ticketing/fares", "Fare deleted successfully", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/ticketing/fares", "Error deleting fare: " + e.getMessage(), redirectAttributes);
        }
    }

    // New Ticket Creation for Passengers
    @GetMapping("/tickets/new")
    public String newTicketForm(Model model, HttpSession session) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);

            // Get buses assigned to this ticketing officer
            List<Bus> assignedBuses = busService.getBusesByTicketingOfficerId(userId);

            // Get all active routes
            List<Route> availableRoutes = routeService.getRoutesByStatus("ACTIVE");

            // Get all passengers for selection
            List<User> passengers = userService.getUsersByRole("PASSENGER");

            model.addAttribute("booking", new Booking()); // Using Booking model since tickets = bookings
            model.addAttribute("routes", availableRoutes);
            model.addAttribute("passengers", passengers);
            model.addAttribute("assignedBuses", assignedBuses);
            return "ticketing/tickets/form";
        } catch (Exception e) {
            return handleException(e, model, "ticketing/tickets/form");
        }
    }

    @PostMapping("/tickets")
    public String createTicket(@RequestParam Long passengerId, @RequestParam Long routeId,
                              @RequestParam Long scheduleId, @RequestParam String ticketType,
                              @RequestParam String travelDate, @RequestParam String seatNumber,
                              @RequestParam(required = false) String specialRequests,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            // Parse date
            LocalDateTime travelDateTime = LocalDateTime.parse(travelDate + "T00:00:00");

            // Check if seat is available
            if (!bookingService.isSeatAvailable(scheduleId, travelDateTime, seatNumber)) {
                return redirectWithError("/ticketing/tickets/new",
                    "Seat " + seatNumber + " is already booked. Please select another seat.", redirectAttributes);
            }

            // Get the schedule to find bus ID
            Optional<Schedule> scheduleOpt = scheduleService.getById(scheduleId);
            if (scheduleOpt.isEmpty()) {
                return redirectWithError("/ticketing/tickets/new", "Schedule not found", redirectAttributes);
            }
            Long busId = scheduleOpt.get().getBusId();

            // Verify that this ticketing officer manages this bus
            Long userId = getCurrentUserId(session);
            List<Bus> assignedBuses = busService.getBusesByTicketingOfficerId(userId);
            boolean canManageBus = assignedBuses.stream().anyMatch(bus -> bus.getId().equals(busId));

            if (!canManageBus) {
                return redirectWithError("/ticketing/tickets/new",
                    "You are not authorized to create tickets for this bus", redirectAttributes);
            }

            // Get fare for the route and ticket type
            List<Fare> fares = fareService.getActiveFaresByRouteAndType(routeId, ticketType);
            if (fares.isEmpty()) {
                return redirectWithError("/ticketing/tickets/new",
                    "No fare found for selected route and ticket type", redirectAttributes);
            }

            Fare fare = fares.get(0);
            Double fareAmount = fare.getBasePrice();
            Double discountApplied = fareAmount * (fare.getDiscountPercentage() / 100);
            Double finalAmount = fareAmount - discountApplied;

            // Create new ticket (using Booking model since tickets = bookings)
            Booking newTicket = new Booking(passengerId, routeId, scheduleId, busId, ticketType,
                    fareAmount, discountApplied, finalAmount, travelDateTime, seatNumber, "CONFIRMED");

            if (specialRequests != null && !specialRequests.trim().isEmpty()) {
                newTicket.setSpecialRequests(specialRequests);
            }

            bookingService.save(newTicket);
            return redirectWithSuccess("/ticketing/tickets",
                "Ticket created successfully for seat " + seatNumber, redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/ticketing/tickets/new",
                "Failed to create ticket: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/tickets/{id}/edit")
    public String editTicketForm(@PathVariable Long id, HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            Optional<Booking> ticketOpt = bookingService.getById(id);
            if (ticketOpt.isEmpty()) {
                return "redirect:/ticketing/tickets";
            }

            Booking ticket = ticketOpt.get();

            // Verify that this ticketing officer can manage this ticket
            Long userId = getCurrentUserId(session);
            List<Bus> assignedBuses = busService.getBusesByTicketingOfficerId(userId);
            boolean canManageBus = assignedBuses.stream().anyMatch(bus -> bus.getId().equals(ticket.getBusId()));

            if (!canManageBus) {
                return "redirect:/ticketing/tickets";
            }

            // Get necessary data for the form
            List<Route> availableRoutes = routeService.getRoutesByStatus("ACTIVE");
            List<Schedule> schedules = scheduleService.getSchedulesByRouteId(ticket.getRouteId());
            List<User> passengers = userService.getUsersByRole("PASSENGER");

            model.addAttribute("booking", ticket); // Using 'booking' for consistency with form
            model.addAttribute("routes", availableRoutes);
            model.addAttribute("schedules", schedules);
            model.addAttribute("passengers", passengers);
            model.addAttribute("assignedBuses", assignedBuses);

            return "ticketing/tickets/edit";
        } catch (Exception e) {
            return handleException(e, model, "ticketing/tickets/edit");
        }
    }

    @PostMapping("/tickets/{id}")
    public String updateTicket(@PathVariable Long id, @RequestParam Long passengerId,
                              @RequestParam String travelDate, @RequestParam String seatNumber,
                              @RequestParam String status, @RequestParam(required = false) String specialRequests,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "TICKETING_OFFICER");
        if (authCheck != null) return authCheck;

        try {
            Optional<Booking> ticketOpt = bookingService.getById(id);
            if (ticketOpt.isEmpty()) {
                return redirectWithError("/ticketing/tickets", "Ticket not found", redirectAttributes);
            }

            Booking ticket = ticketOpt.get();

            // Verify authorization
            Long userId = getCurrentUserId(session);
            List<Bus> assignedBuses = busService.getBusesByTicketingOfficerId(userId);
            boolean canManageBus = assignedBuses.stream().anyMatch(bus -> bus.getId().equals(ticket.getBusId()));

            if (!canManageBus) {
                return redirectWithError("/ticketing/tickets",
                    "You are not authorized to edit this ticket", redirectAttributes);
            }

            LocalDateTime newTravelDate = LocalDateTime.parse(travelDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // Check if seat is being changed and if it's available
            if (!ticket.getSeatNumber().equals(seatNumber)) {
                if (!bookingService.isSeatAvailable(ticket.getScheduleId(), newTravelDate, seatNumber)) {
                    return redirectWithError("/ticketing/tickets/" + id + "/edit",
                        "Seat " + seatNumber + " is already booked. Please select another seat.", redirectAttributes);
                }
            }

            ticket.setPassengerId(passengerId);
            ticket.setTravelDate(newTravelDate);
            ticket.setSeatNumber(seatNumber);
            ticket.setStatus(status);
            ticket.setSpecialRequests(specialRequests);

            bookingService.save(ticket);
            return redirectWithSuccess("/ticketing/tickets",
                "Ticket updated successfully for seat " + seatNumber, redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/ticketing/tickets/" + id + "/edit",
                "Error updating ticket: " + e.getMessage(), redirectAttributes);
        }
    }

    // API endpoints for dynamic form updates
    @GetMapping("/routes/{routeId}/schedules")
    @ResponseBody
    public List<Schedule> getSchedulesByRoute(@PathVariable Long routeId) {
        return scheduleService.getSchedulesByRouteId(routeId);
    }

    @GetMapping("/routes/{routeId}/fares")
    @ResponseBody
    public List<Fare> getFaresByRoute(@PathVariable Long routeId) {
        return fareService.getFaresByRoute(routeId);
    }

    @GetMapping("/schedules/{scheduleId}/booked-seats")
    @ResponseBody
    public List<String> getBookedSeats(@PathVariable Long scheduleId, @RequestParam String travelDate) {
        LocalDateTime travelDateTime = LocalDateTime.parse(travelDate + "T00:00:00");
        return bookingService.getBookedSeats(scheduleId, travelDateTime);
    }

    @GetMapping("/api/available-seats")
    @ResponseBody
    public Map<String, Object> getAvailableSeatsForEdit(@RequestParam Long scheduleId,
                                                       @RequestParam String travelDate,
                                                       @RequestParam(required = false) Long bookingId) {
        try {
            LocalDateTime travelDateTime = LocalDateTime.parse(travelDate.replace("T", "T") + ":00");
            List<String> bookedSeats = bookingService.getBookedSeats(scheduleId, travelDateTime);

            // If editing an existing booking, remove its current seat from booked seats
            String currentSeat = null;
            if (bookingId != null) {
                Optional<Booking> booking = bookingService.getById(bookingId);
                if (booking.isPresent()) {
                    currentSeat = booking.get().getSeatNumber();
                    bookedSeats.remove(currentSeat);
                }
            }

            // Generate available seats (1-40)
            List<String> availableSeats = new java.util.ArrayList<>();
            for (int i = 1; i <= 40; i++) {
                String seatNumber = String.format("%02d", i);
                if (!bookedSeats.contains(seatNumber)) {
                    availableSeats.add(seatNumber);
                }
            }

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("availableSeats", availableSeats);
            response.put("currentSeat", currentSeat);
            response.put("totalAvailable", availableSeats.size());

            return response;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Failed to load available seats: " + e.getMessage());
            errorResponse.put("availableSeats", new java.util.ArrayList<>());
            return errorResponse;
        }
    }
}
