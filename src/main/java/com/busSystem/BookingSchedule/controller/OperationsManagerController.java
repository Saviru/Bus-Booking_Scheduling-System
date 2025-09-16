package com.busSystem.BookingSchedule.controller;

import com.busSystem.BookingSchedule.model.OperationsManager;
import com.busSystem.BookingSchedule.service.OperationsManagerService;
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
@RequestMapping("/operations")
public class OperationsManagerController {
    
    @Autowired
    private OperationsManagerService operationsManagerService;
    
    // Helper method to get logged in manager
    private OperationsManager getLoggedInManager(HttpSession session) {
        return (OperationsManager) session.getAttribute("loggedInOperationsManager");
    }
    
    // Login page
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedInOperationsManager") != null) {
            return "redirect:/operations/dashboard";
        }
        return "operations/login";
    }
    
    // Login processing
    @PostMapping("/login")
    public String login(@RequestParam String identifier,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        Optional<OperationsManager> manager = operationsManagerService.authenticate(identifier, password);
        
        if (manager.isPresent() && manager.get().isActive()) {
            session.setAttribute("loggedInOperationsManager", manager.get());
            return "redirect:/operations/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username/email or password");
            return "redirect:/operations/login";
        }
    }
    
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        model.addAttribute("manager", loggedInManager);
        model.addAttribute("activeManagerCount", operationsManagerService.getActiveManagerCount());
        return "operations/dashboard";
    }
    
    // Profile page
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        model.addAttribute("manager", loggedInManager);
        return "operations/profile";
    }
    
    // Edit profile page
    @GetMapping("/profile/edit")
    public String editProfilePage(HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        model.addAttribute("manager", loggedInManager);
        return "operations/edit-profile";
    }
    
    // Process profile update
    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute OperationsManager manager,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("manager", manager);
            return "operations/edit-profile";
        }
        
        if (operationsManagerService.updateProfile(loggedInManager.getId(), manager)) {
            // Update session with latest data
            Optional<OperationsManager> updatedManager = operationsManagerService.findById(loggedInManager.getId());
            if (updatedManager.isPresent()) {
                session.setAttribute("loggedInOperationsManager", updatedManager.get());
            }
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile");
        }
        
        return "redirect:/operations/profile";
    }
    
    // Change password page
    @GetMapping("/profile/change-password")
    public String changePasswordPage(HttpSession session) {
        if (getLoggedInManager(session) == null) {
            return "redirect:/operations/login";
        }
        return "operations/change-password";
    }
    
    // Change password processing
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/operations/profile/change-password";
        }
        
        if (operationsManagerService.changePassword(loggedInManager.getId(), oldPassword, newPassword)) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid old password");
        }
        
        return "redirect:/operations/profile/change-password";
    }
    
    // Register page (for admin use)
    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        // Only admin can register new managers
        if (loggedInManager.getRole() != OperationsManager.ManagerRole.ADMIN) {
            return "redirect:/operations/dashboard";
        }
        
        model.addAttribute("manager", new OperationsManager());
        return "operations/register";
    }
    
    // Register processing
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute OperationsManager manager,
                          BindingResult bindingResult,
                          HttpSession session,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        // Only admin can register new managers
        if (loggedInManager.getRole() != OperationsManager.ManagerRole.ADMIN) {
            return "redirect:/operations/dashboard";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("manager", manager);
            return "operations/register";
        }
        
        if (operationsManagerService.registerManager(manager)) {
            redirectAttributes.addFlashAttribute("success", "Manager registered successfully!");
            return "redirect:/operations/managers";
        } else {
            model.addAttribute("error", "Username, email, or employee ID already exists");
            model.addAttribute("manager", manager);
            return "operations/register";
        }
    }
    
    // List all managers (admin only)
    @GetMapping("/managers")
    public String listManagers(HttpSession session, Model model) {
        OperationsManager loggedInManager = getLoggedInManager(session);
        if (loggedInManager == null) {
            return "redirect:/operations/login";
        }
        
        // Only admin can view all managers
        if (loggedInManager.getRole() != OperationsManager.ManagerRole.ADMIN) {
            return "redirect:/operations/dashboard";
        }
        
        model.addAttribute("managers", operationsManagerService.getAllActiveManagers());
        return "operations/managers";
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logged out successfully!");
        return "redirect:/operations/login";
    }
    
    // AJAX endpoints for validation
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return operationsManagerService.isUsernameAvailable(username);
    }
    
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return operationsManagerService.isEmailAvailable(email);
    }
    
    @GetMapping("/check-employee-id")
    @ResponseBody
    public boolean checkEmployeeId(@RequestParam String employeeId) {
        return operationsManagerService.isEmployeeIdAvailable(employeeId);
    }
}