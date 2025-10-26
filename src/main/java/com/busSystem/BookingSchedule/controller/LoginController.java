package com.busSystem.BookingSchedule.controller;

import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginForm() {
        return "redirect:/?action=login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, 
                       HttpSession session, Model model) {
        Optional<User> user = userService.authenticateUser(email, password);
        
        if (user.isPresent()) {
            session.setAttribute("userId", user.get().getId());
            session.setAttribute("userEmail", user.get().getEmail());
            session.setAttribute("userRole", user.get().getRole());
            session.setAttribute("userName", user.get().getFirstName() + " " + user.get().getLastName());
            
            switch (user.get().getRole()) {
                case "IT_SUPPORT":
                    return "redirect:/itsupport";
                case "OPERATION_MANAGER":
                    return "redirect:/operations";
                case "CUSTOMER_SUPPORT":
                    return "redirect:/customersupport";
                case "TICKETING_OFFICER":
                    return "redirect:/ticketing";
                case "DRIVER":
                    return "redirect:/driver";
                case "PASSENGER":
                    return "redirect:/passenger";
                default:
                    return "redirect:/";
            }
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "redirect:/?action=login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?action=login";
    }
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
}