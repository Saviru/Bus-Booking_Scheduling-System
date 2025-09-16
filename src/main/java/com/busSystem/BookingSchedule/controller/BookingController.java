package com.busSystem.BookingSchedule.controller;

import com.busSystem.BookingSchedule.model.Booking;
import com.busSystem.BookingSchedule.model.Passenger;
import com.busSystem.BookingSchedule.model.Schedule;
import com.busSystem.BookingSchedule.service.BookingService;
import com.busSystem.BookingSchedule.service.RouteService;
import com.busSystem.BookingSchedule.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private ScheduleService scheduleService;
    
    // List all bookings for logged-in passenger
    @GetMapping
    public String listBookings(@RequestParam(defaultValue = "all") String filter,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                              HttpSession session, Model model) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        List<Booking> bookings;
        
        // Apply filters
        switch (filter.toLowerCase()) {
            case "upcoming":
                bookings = bookingService.getUpcomingBookings(loggedInPassenger);
                break;
            case "past":
                bookings = bookingService.getPastBookings(loggedInPassenger);
                break;
            case "pending":
                bookings = bookingService.getBookingsByStatus(loggedInPassenger, Booking.BookingStatus.PENDING);
                break;
            case "confirmed":
                bookings = bookingService.getBookingsByStatus(loggedInPassenger, Booking.BookingStatus.CONFIRMED);
                break;
            case "cancelled":
                bookings = bookingService.getBookingsByStatus(loggedInPassenger, Booking.BookingStatus.CANCELLED);
                break;
            case "daterange":
                if (fromDate != null && toDate != null) {
                    bookings = bookingService.getBookingsByDateRange(loggedInPassenger, fromDate, toDate);
                } else {
                    bookings = bookingService.getBookingsByPassenger(loggedInPassenger);
                }
                break;
            default:
                bookings = bookingService.getBookingsByPassenger(loggedInPassenger);
        }
        
        model.addAttribute("bookings", bookings);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("passenger", loggedInPassenger);
        
        return "bookings/list";
    }
    
    // Show booking details
    @GetMapping("/{id}")
    public String viewBooking(@PathVariable Long id, HttpSession session, Model model) {
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        Optional<Booking> booking = bookingService.findByIdAndPassenger(id, loggedInPassenger);
        if (booking.isEmpty()) {
            return "redirect:/bookings?error=Booking not found";
        }
        
        model.addAttribute("booking", booking.get());
        return "bookings/details";
    }
    
    // Show new booking form
    @GetMapping("/new")
    public String newBookingForm(@RequestParam(required = false) Long routeId,
                                @RequestParam(required = false) Long scheduleId,
                                HttpSession session, Model model) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("routes", routeService.getAllActiveRoutes());
        
        if (routeId != null) {
            List<Schedule> schedules = scheduleService.getAvailableSchedulesByRouteId(routeId);
            model.addAttribute("schedules", schedules);
            model.addAttribute("selectedRouteId", routeId);
        }
        
        if (scheduleId != null) {
            Optional<Schedule> schedule = scheduleService.findById(scheduleId);
            if (schedule.isPresent()) {
                model.addAttribute("selectedSchedule", schedule.get());
                model.addAttribute("selectedScheduleId", scheduleId);
            }
        }
        
        model.addAttribute("booking", new Booking());
        model.addAttribute("minDate", LocalDate.now().plusDays(1));
        
        return "bookings/form";
    }
    
    // Process new booking
    @PostMapping("/create")
    public String createBooking(@RequestParam Long scheduleId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
                               @RequestParam Integer numberOfSeats,
                               @RequestParam(required = false) String seatPreference,
                               @RequestParam String paymentMethod,
                               @RequestParam(required = false) String specialRequests,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Schedule> schedule = scheduleService.findById(scheduleId);
            if (schedule.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Selected schedule not found");
                return "redirect:/bookings/new";
            }
            
            Booking booking = bookingService.createBooking(
                loggedInPassenger, schedule.get(), travelDate, numberOfSeats,
                seatPreference, paymentMethod, specialRequests
            );
            
            redirectAttributes.addFlashAttribute("success", 
                "Booking created successfully! Reference: " + booking.getBookingReference());
            return "redirect:/bookings/" + booking.getId();
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/new?scheduleId=" + scheduleId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create booking. Please try again.");
            return "redirect:/bookings/new";
        }
    }
    
    // Show edit booking form
    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable Long id, HttpSession session, Model model) {
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        Optional<Booking> booking = bookingService.findByIdAndPassenger(id, loggedInPassenger);
        if (booking.isEmpty()) {
            return "redirect:/bookings?error=Booking not found";
        }
        
        if (!booking.get().canBeModified()) {
            return "redirect:/bookings/" + id + "?error=Booking cannot be modified";
        }
        
        model.addAttribute("booking", booking.get());
        model.addAttribute("minDate", LocalDate.now().plusDays(1));
        
        return "bookings/edit";
    }
    
    // Process booking update
    @PostMapping("/{id}/update")
    public String updateBooking(@PathVariable Long id,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
                               @RequestParam(required = false) String seatPreference,
                               @RequestParam(required = false) String specialRequests,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        try {
            boolean updated = bookingService.updateBooking(id, loggedInPassenger, 
                                                         travelDate, seatPreference, specialRequests);
            if (updated) {
                redirectAttributes.addFlashAttribute("success", "Booking updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update booking");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/bookings/" + id;
    }
    
    // Confirm booking (payment)
    @PostMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable Long id,
                                @RequestParam String paymentReference,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        boolean confirmed = bookingService.confirmBooking(id, loggedInPassenger, paymentReference);
        if (confirmed) {
            redirectAttributes.addFlashAttribute("success", "Booking confirmed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to confirm booking");
        }
        
        return "redirect:/bookings/" + id;
    }
    
    // Cancel booking
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                               @RequestParam(required = false) String reason,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        try {
            boolean cancelled = bookingService.cancelBooking(id, loggedInPassenger, reason);
            if (cancelled) {
                redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to cancel booking");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/bookings/" + id;
    }
    
    // Delete booking
    @PostMapping("/{id}/delete")
    public String deleteBooking(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        try {
            boolean deleted = bookingService.deleteBooking(id, loggedInPassenger);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Booking deleted successfully!");
                return "redirect:/bookings";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete booking");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/bookings/" + id;
    }
    
    // AJAX endpoint to get schedules by route
    @GetMapping("/schedules/{routeId}")
    @ResponseBody
    public List<Schedule> getSchedulesByRoute(@PathVariable Long routeId) {
        return scheduleService.getAvailableSchedulesByRouteId(routeId);
    }
    
    // AJAX endpoint to check seat availability
    @GetMapping("/check-availability")
    @ResponseBody
    public boolean checkSeatAvailability(@RequestParam Long scheduleId,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
                                       @RequestParam Integer numberOfSeats) {
        
        Optional<Schedule> schedule = scheduleService.findById(scheduleId);
        if (schedule.isEmpty()) {
            return false;
        }
        
        return bookingService.isSeatsAvailable(schedule.get(), travelDate, numberOfSeats);
    }
    
    // Search bookings by reference
    @GetMapping("/search")
    public String searchBookings(@RequestParam String reference,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = getLoggedInPassenger(session);
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        Optional<Booking> booking = bookingService.findByBookingReference(reference);
        if (booking.isPresent() && booking.get().getPassenger().getId().equals(loggedInPassenger.getId())) {
            return "redirect:/bookings/" + booking.get().getId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Booking not found with reference: " + reference);
            return "redirect:/bookings";
        }
    }
    
    // Utility method to get logged-in passenger
    private Passenger getLoggedInPassenger(HttpSession session) {
        return (Passenger) session.getAttribute("loggedInPassenger");
    }
}