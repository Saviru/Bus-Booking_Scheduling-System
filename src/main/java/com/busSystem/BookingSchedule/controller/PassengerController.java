package com.busSystem.BookingSchedule.controller;

import com.busSystem.BookingSchedule.model.Passenger;
import com.busSystem.BookingSchedule.service.PassengerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PassengerController {
    
    @Autowired
    private PassengerService passengerService;
    
    // Index page
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger != null) {
            model.addAttribute("loggedInPassenger", loggedInPassenger);
        }
        return "index";
    }
    
    // Login page
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedInPassenger") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }
    
    // Login processing
    @PostMapping("/login")
    public String login(@RequestParam String identifier,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        Optional<Passenger> passenger = passengerService.authenticate(identifier, password);
        
        if (passenger.isPresent() && passenger.get().getIsActive()) {
            session.setAttribute("loggedInPassenger", passenger.get());
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username/email or password");
            return "redirect:/login";
        }
    }
    
    // Register page
    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedInPassenger") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("passenger", new Passenger());
        return "register";
    }
    
    // Register processing
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Passenger passenger,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        if (passengerService.registerPassenger(passenger)) {
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Username or email already exists");
            return "register";
        }
    }
    
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        model.addAttribute("passenger", loggedInPassenger);
        return "dashboard";
    }
    
    // Profile view
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        // Get fresh data from database
        Optional<Passenger> freshPassenger = passengerService.findById(loggedInPassenger.getId());
        if (freshPassenger.isPresent()) {
            model.addAttribute("passenger", freshPassenger.get());
            session.setAttribute("loggedInPassenger", freshPassenger.get()); // Update session
        }
        return "profile";
    }
    
    // Edit profile page
    @GetMapping("/profile/edit")
    public String editProfilePage(HttpSession session, Model model) {
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        model.addAttribute("passenger", loggedInPassenger);
        return "edit-profile";
    }
    
    // Update profile
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute Passenger passenger,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            return "edit-profile";
        }
        
        if (passengerService.updateProfile(loggedInPassenger.getId(), passenger)) {
            // Update session with fresh data
            Optional<Passenger> updatedPassenger = passengerService.findById(loggedInPassenger.getId());
            if (updatedPassenger.isPresent()) {
                session.setAttribute("loggedInPassenger", updatedPassenger.get());
            }
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile");
        }
        
        return "redirect:/profile";
    }
    
    // Payment methods page
    @GetMapping("/profile/payment-methods")
    public String paymentMethodsPage(HttpSession session, Model model) {
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        model.addAttribute("passenger", loggedInPassenger);
        return "payment-methods";
    }
    
    // Update payment methods
    @PostMapping("/profile/payment-methods")
    public String updatePaymentMethods(@RequestParam String paymentMethods,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        if (passengerService.updatePaymentMethods(loggedInPassenger.getId(), paymentMethods)) {
            // Update session
            Optional<Passenger> updatedPassenger = passengerService.findById(loggedInPassenger.getId());
            if (updatedPassenger.isPresent()) {
                session.setAttribute("loggedInPassenger", updatedPassenger.get());
            }
            redirectAttributes.addFlashAttribute("success", "Payment methods updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update payment methods");
        }
        
        return "redirect:/profile/payment-methods";
    }
    
    // Change password page
    @GetMapping("/profile/change-password")
    public String changePasswordPage(HttpSession session) {
        if (session.getAttribute("loggedInPassenger") == null) {
            return "redirect:/login";
        }
        return "change-password";
    }
    
    // Change password
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/profile/change-password";
        }
        
        if (passengerService.changePassword(loggedInPassenger.getId(), oldPassword, newPassword)) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid old password");
        }
        
        return "redirect:/profile/change-password";
    }
    
    // Delete account
    @GetMapping("/profile/delete")
    public String deleteAccountPage(HttpSession session) {
        if (session.getAttribute("loggedInPassenger") == null) {
            return "redirect:/login";
        }
        return "delete-account";
    }
    
    @PostMapping("/profile/delete")
    public String deleteAccount(@RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Passenger loggedInPassenger = (Passenger) session.getAttribute("loggedInPassenger");
        if (loggedInPassenger == null) {
            return "redirect:/login";
        }
        
        // Verify password before deletion
        Optional<Passenger> verified = passengerService.authenticate(loggedInPassenger.getUsername(), password);
        if (verified.isPresent()) {
            if (passengerService.deactivatePassenger(loggedInPassenger.getId())) {
                session.invalidate();
                redirectAttributes.addFlashAttribute("success", "Account deactivated successfully!");
                return "redirect:/";
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Invalid password");
        return "redirect:/profile/delete";
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logged out successfully!");
        return "redirect:/";
    }
    
    // Check username availability (AJAX endpoint)
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return passengerService.isUsernameAvailable(username);
    }
    
    // Check email availability (AJAX endpoint)
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return passengerService.isEmailAvailable(email);
    }
}