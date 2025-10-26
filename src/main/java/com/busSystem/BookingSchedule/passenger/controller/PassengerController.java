package com.busSystem.BookingSchedule.passenger.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.passenger.model.Booking;
import com.busSystem.BookingSchedule.passenger.model.PassengerProfile;
import com.busSystem.BookingSchedule.passenger.model.Complaint;
import com.busSystem.BookingSchedule.passenger.model.Feedback;
import com.busSystem.BookingSchedule.passenger.service.BookingService;
import com.busSystem.BookingSchedule.passenger.service.PassengerProfileService;
import com.busSystem.BookingSchedule.passenger.service.ComplaintService;
import com.busSystem.BookingSchedule.passenger.service.FeedbackService;
import com.busSystem.BookingSchedule.customerService.service.CustomerServiceComplaintService;
import com.busSystem.BookingSchedule.customerService.service.CustomerServiceFeedbackService;
import com.busSystem.BookingSchedule.operationsManager.model.Route;
import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import com.busSystem.BookingSchedule.operationsManager.service.RouteService;
import com.busSystem.BookingSchedule.operationsManager.service.ScheduleService;
import com.busSystem.BookingSchedule.ticketingOfficer.model.Fare;
import com.busSystem.BookingSchedule.ticketingOfficer.service.FareService;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/passenger")
public class PassengerController extends Controller {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PassengerProfileService passengerProfileService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private CustomerServiceComplaintService customerServiceComplaintService;

    @Autowired
    private CustomerServiceFeedbackService customerServiceFeedbackService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FareService fareService;

    @Autowired
    private UserService userService;

    // Dashboard
    @GetMapping("")
    public String dashboard(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "PASSENGER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            List<Booking> recentBookings = bookingService.getBookingsByPassenger(userId);
            model.addAttribute("bookings", recentBookings.size() > 5 ? recentBookings.subList(0, 5) : recentBookings);
            model.addAttribute("userName", session.getAttribute("userName"));
            return "passenger/dashboard";
        } catch (Exception e) {
            return handleException(e, model, "passenger/dashboard");
        }
    }

    // Registration
    @GetMapping("/register")
    public String registerForm() {
        return "passenger/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password,
                           @RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam String phoneNumber, Model model) {
        try {
            Optional<User> existingUser = userService.getUserByEmail(email);
            if (existingUser.isPresent()) {
                addErrorMessage(model, "Email already exists");
                return "passenger/register";
            }

            User user = new User(email, password, firstName, lastName, "PASSENGER", "ACTIVE", phoneNumber);
            userService.save(user);
            addSuccessMessage(model, "Registration successful. Please login.");
            return "redirect:/?action=login";
        } catch (Exception e) {
            return handleException(e, model, "passenger/register");
        }
    }

    // Booking Management
    @GetMapping("/bookings")
    public String viewBookings(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "PASSENGER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            List<Booking> bookings = bookingService.getBookingsByPassenger(userId);
            model.addAttribute("bookings", bookings);
            return "passenger/bookings";
        } catch (Exception e) {
            return handleException(e, model, "passenger/bookings");
        }
    }

    @GetMapping("/bookings/new")
    public String newBookingForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "PASSENGER");
        if (authCheck != null) return authCheck;

        try {
            List<Route> routes = routeService.getRoutesByStatus("ACTIVE");
            model.addAttribute("routes", routes);
            return "passenger/booking-form";
        } catch (Exception e) {
            return handleException(e, model, "passenger/booking-form");
        }
    }

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

    @PostMapping("/bookings")
    public String createBooking(@RequestParam Long routeId, @RequestParam Long scheduleId,
                                @RequestParam String ticketType, @RequestParam String travelDate,
                                @RequestParam String seatNumber, @RequestParam(required = false) String specialRequests,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "PASSENGER");
        if (authCheck != null) return authCheck;

        try {
            Long userId = getCurrentUserId(session);
            // Parse date (now just yyyy-MM-dd format)
            LocalDateTime travelDateTime = LocalDateTime.parse(travelDate + "T00:00:00");

            // Check if seat is available
            if (!bookingService.isSeatAvailable(scheduleId, travelDateTime, seatNumber)) {
                addErrorMessage(redirectAttributes, "Seat " + seatNumber + " is already booked. Please select another seat.");
                return "redirect:/passenger/bookings/new";
            }

            // Get the schedule to find bus ID
            Optional<Schedule> scheduleOpt = scheduleService.getById(scheduleId);
            if (scheduleOpt.isEmpty()) {
                return redirectWithError("/passenger/bookings/new", "Schedule not found", redirectAttributes);
            }
            Long busId = scheduleOpt.get().getBusId();

            // Get fare for the route and ticket type
            List<Fare> fares = fareService.getActiveFaresByRouteAndType(routeId, ticketType);
            if (fares.isEmpty()) {
                return redirectWithError("/passenger/bookings/new", "No fare found for selected route and ticket type", redirectAttributes);
            }

            Fare fare = fares.getFirst();
            Double fareAmount = fare.getBasePrice();
            Double discountApplied = fareAmount * (fare.getDiscountPercentage() / 100);
            Double finalAmount = fareAmount - discountApplied;

            Booking booking = new Booking(userId, routeId, scheduleId, busId, ticketType,
                    fareAmount, discountApplied, finalAmount, travelDateTime, seatNumber, "BOOKED");

            if (specialRequests != null && !specialRequests.trim().isEmpty()) {
                booking.setSpecialRequests(specialRequests);
            }

            bookingService.save(booking);
            return redirectWithSuccess("/passenger/bookings", "Booking created successfully! Seat " + seatNumber + " confirmed.", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/passenger/bookings/new", "Failed to create booking: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/bookings/{id}/edit")
    public String editBookingForm(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Booking> bookingOpt = bookingService.getById(id);
        if (bookingOpt.isEmpty() || !bookingOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/bookings";
        }

        Booking booking = bookingOpt.get();
        List<Route> routes = routeService.getRoutesByStatus("ACTIVE");
        List<Schedule> schedules = scheduleService.getSchedulesByRouteId(booking.getRouteId());

        model.addAttribute("booking", booking);
        model.addAttribute("routes", routes);
        model.addAttribute("schedules", schedules);

        // Get schedule details for seat selection
        Optional<Schedule> scheduleOpt = scheduleService.getById(booking.getScheduleId());
        if (scheduleOpt.isPresent()) {
            model.addAttribute("currentSchedule", scheduleOpt.get());
        }

        return "passenger/booking-edit";
    }

    @PostMapping("/bookings/{id}")
    public String updateBooking(@PathVariable Long id, @RequestParam String travelDate,
                                @RequestParam String seatNumber, @RequestParam(required = false) String specialRequests,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Booking> bookingOpt = bookingService.getById(id);
        if (bookingOpt.isEmpty() || !bookingOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/bookings";
        }

        Booking booking = bookingOpt.get();
        LocalDateTime newTravelDate = LocalDateTime.parse(travelDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        // Check if seat is being changed
        if (!booking.getSeatNumber().equals(seatNumber)) {
            // Check if new seat is available
            if (!bookingService.isSeatAvailable(booking.getScheduleId(), newTravelDate, seatNumber)) {
                return redirectWithError("/passenger/bookings/" + id + "/edit",
                    "Seat " + seatNumber + " is already booked. Please select another seat.", redirectAttributes);
            }
        }

        booking.setTravelDate(newTravelDate);
        booking.setSeatNumber(seatNumber);
        booking.setSpecialRequests(specialRequests);

        bookingService.save(booking);
        return redirectWithSuccess("/passenger/bookings",
            "Booking updated successfully! Seat " + seatNumber + " confirmed.", redirectAttributes);
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Booking> bookingOpt = bookingService.getById(id);
        if (bookingOpt.isEmpty() || !bookingOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/bookings";
        }

        Booking booking = bookingOpt.get();
        booking.setStatus("CANCELLED");
        bookingService.save(booking);
        return "redirect:/passenger/bookings";
    }

    // Profile Management
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.getById(userId);
        Optional<PassengerProfile> profile = passengerProfileService.getProfileByUserId(userId);

        model.addAttribute("user", user.orElse(null));
        model.addAttribute("profile", profile.orElse(null));
        return "passenger/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.getById(userId);
        Optional<PassengerProfile> profile = passengerProfileService.getProfileByUserId(userId);

        model.addAttribute("user", user.orElse(null));
        model.addAttribute("profile", profile.orElse(new PassengerProfile()));
        return "passenger/profile-edit";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String firstName, @RequestParam String lastName,
                                @RequestParam String phoneNumber, @RequestParam(required = false) String preferredSeatType,
                                @RequestParam(required = false) String frequentDestinations, @RequestParam(required = false) String paymentMethods,
                                @RequestParam(required = false) String accessibilityNeeds, @RequestParam(required = false) String travelPreferences,
                                @RequestParam(required = false) String emergencyContactName, @RequestParam(required = false) String emergencyContactPhone,
                                HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // Update user basic info
        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            userService.save(user);
            session.setAttribute("userName", firstName + " " + lastName);
        }

        // Update or create profile
        Optional<PassengerProfile> profileOpt = passengerProfileService.getProfileByUserId(userId);
        PassengerProfile profile;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        } else {
            profile = new PassengerProfile();
            profile.setUserId(userId);
            profile.setStatus("ACTIVE");
        }

        profile.setPreferredSeatType(preferredSeatType);
        profile.setFrequentDestinations(frequentDestinations);
        profile.setPaymentMethods(paymentMethods);
        profile.setAccessibilityNeeds(accessibilityNeeds);
        profile.setTravelPreferences(travelPreferences);
        profile.setEmergencyContactName(emergencyContactName);
        profile.setEmergencyContactPhone(emergencyContactPhone);

        passengerProfileService.save(profile);
        return "redirect:/passenger/profile";
    }

    // Search Routes
    @GetMapping("/routes")
    public String searchRoutes(@RequestParam(required = false) String search, Model model) {
        List<Route> routes;
        if (search != null && !search.trim().isEmpty()) {
            routes = routeService.searchRoutes(search);
        } else {
            routes = routeService.getRoutesByStatus("ACTIVE");
        }
        model.addAttribute("routes", routes);
        model.addAttribute("search", search);
        return "passenger/routes";
    }

    @GetMapping("/routes/{id}/details")
    public String routeDetails(@PathVariable Long id, Model model) {
        Optional<Route> route = routeService.getById(id);
        if (route.isEmpty()) {
            return "redirect:/passenger/routes";
        }

        List<Schedule> schedules = scheduleService.getSchedulesByRouteId(id);
        List<Fare> fares = fareService.getFaresByRoute(id);

        model.addAttribute("route", route.get());
        model.addAttribute("schedules", schedules);
        model.addAttribute("fares", fares);
        return "passenger/route-details";
    }

    // Complaint Management
    @GetMapping("/complaints")
    public String viewComplaints(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Complaint> complaints = complaintService.getComplaintsByPassenger(userId);
        model.addAttribute("complaints", complaints);
        return "passenger/complaints";
    }

    @GetMapping("/complaints/new")
    public String newComplaintForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingService.getBookingsByPassenger(userId);
        model.addAttribute("tickets", bookings);
        return "passenger/complaint-form";
    }

    @PostMapping("/complaints")
    public String createComplaint(@RequestParam(required = false) Long ticketId, @RequestParam String subject,
                                  @RequestParam String description, @RequestParam String category,
                                  @RequestParam String priority, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Complaint complaint = new Complaint(userId, ticketId, subject, description, category, priority, "PENDING");
        complaintService.save(complaint);
        return "redirect:/passenger/complaints";
    }

    @GetMapping("/complaints/{id}/edit")
    public String editComplaintForm(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Complaint> complaintOpt = complaintService.getById(id);
        if (complaintOpt.isEmpty() || !complaintOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/complaints";
        }

        List<Booking> bookings = bookingService.getBookingsByPassenger(userId);
        model.addAttribute("complaint", complaintOpt.get());
        model.addAttribute("tickets", bookings);
        return "passenger/complaint-edit";
    }

    @PostMapping("/complaints/{id}")
    public String updateComplaint(@PathVariable Long id, @RequestParam(required = false) Long ticketId,
                                  @RequestParam String subject, @RequestParam String description,
                                  @RequestParam String category, @RequestParam String priority,
                                  HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Complaint> complaintOpt = complaintService.getById(id);
        if (complaintOpt.isEmpty() || !complaintOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/complaints";
        }

        Complaint complaint = complaintOpt.get();
        complaint.setTicketId(ticketId);
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setCategory(category);
        complaint.setPriority(priority);

        complaintService.save(complaint);
        return "redirect:/passenger/complaints";
    }

    @PostMapping("/complaints/{id}/delete")
    public String deleteComplaint(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Complaint> complaintOpt = complaintService.getById(id);
        if (complaintOpt.isEmpty() || !complaintOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/complaints";
        }

        complaintService.delete(id);
        return "redirect:/passenger/complaints";
    }

    @GetMapping("/complaints/{id}")
    public String viewComplaintDetail(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Complaint> complaintOpt = complaintService.getById(id);
        if (complaintOpt.isEmpty() || !complaintOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/complaints";
        }

        model.addAttribute("complaint", complaintOpt.get());
        model.addAttribute("responses", customerServiceComplaintService.getResponsesByComplaintId(id));
        return "passenger/complaint-detail";
    }

    // Feedback Management
    @GetMapping("/feedbacks")
    public String viewFeedbacks(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Feedback> feedbacks = feedbackService.getFeedbacksByPassenger(userId);
        model.addAttribute("feedbacks", feedbacks);
        return "passenger/feedbacks";
    }

    @GetMapping("/feedbacks/{id}")
    public String viewFeedbackDetail(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Feedback> feedbackOpt = feedbackService.getById(id);
        if (feedbackOpt.isEmpty() || !feedbackOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/feedbacks";
        }

        model.addAttribute("feedback", feedbackOpt.get());
        model.addAttribute("responses", customerServiceFeedbackService.getResponsesByFeedbackId(id));
        return "passenger/feedback-detail";
    }

    @GetMapping("/feedbacks/new")
    public String newFeedbackForm() {
        return "passenger/feedback-form";
    }

    @PostMapping("/feedbacks")
    public String createFeedback(@RequestParam Integer rating, @RequestParam String subject,
                                 @RequestParam String message, @RequestParam String category,
                                 @RequestParam String serviceType, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Feedback feedback = new Feedback(userId, rating, subject, message, category, serviceType, "PENDING");
        feedbackService.save(feedback);
        return "redirect:/passenger/feedbacks";
    }

    @GetMapping("/feedbacks/{id}/edit")
    public String editFeedbackForm(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Feedback> feedbackOpt = feedbackService.getById(id);
        if (feedbackOpt.isEmpty() || !feedbackOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/feedbacks";
        }

        model.addAttribute("feedback", feedbackOpt.get());
        return "passenger/feedback-edit";
    }

    @PostMapping("/feedbacks/{id}")
    public String updateFeedback(@PathVariable Long id, @RequestParam Integer rating,
                                 @RequestParam String subject, @RequestParam String message,
                                 @RequestParam String category, @RequestParam String serviceType,
                                 HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Feedback> feedbackOpt = feedbackService.getById(id);
        if (feedbackOpt.isEmpty() || !feedbackOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/feedbacks";
        }

        Feedback feedback = feedbackOpt.get();
        feedback.setRating(rating);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        feedback.setCategory(category);
        feedback.setServiceType(serviceType);

        feedbackService.save(feedback);
        return "redirect:/passenger/feedbacks";
    }

    @PostMapping("/feedbacks/{id}/delete")
    public String deleteFeedback(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Feedback> feedbackOpt = feedbackService.getById(id);
        if (feedbackOpt.isEmpty() || !feedbackOpt.get().getPassengerId().equals(userId)) {
            return "redirect:/passenger/feedbacks";
        }

        feedbackService.delete(id);
        return "redirect:/passenger/feedbacks";
    }
}
