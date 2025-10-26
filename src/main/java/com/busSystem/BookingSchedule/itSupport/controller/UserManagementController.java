package com.busSystem.BookingSchedule.itSupport.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/itsupport/users")
public class UserManagementController extends Controller {

    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listUsers(HttpSession session, Model model, 
                           @RequestParam(required = false) String role,
                           @RequestParam(required = false) String status) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            if (role != null && !role.isEmpty()) {
                model.addAttribute("users", userService.getUsersByRole(role));
            } else if (status != null && !status.isEmpty()) {
                model.addAttribute("users", userService.getUsersByStatus(status));
            } else {
                model.addAttribute("users", userService.getAll());
            }
            return "itSupport/users/list";
        } catch (Exception e) {
            return handleException(e, model, "itSupport/users/list");
        }
    }
    
    @GetMapping("/view/{id}")
    public String viewUser(HttpSession session, @PathVariable Long id, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<User> user = userService.getById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "itSupport/users/view";
        }
        addErrorMessage(model, "User not found");
        return "redirect:/itsupport/users";
    }

    @GetMapping("/create")
    public String createUserForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        model.addAttribute("user", new User());
        return "itSupport/users/create";
    }
    
    @PostMapping("/create")
    public String createUser(HttpSession session, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            userService.save(user);
            return redirectWithSuccess("/itsupport/users", "User created successfully!", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/itsupport/users/create", "Failed to create user: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editUserForm(HttpSession session, @PathVariable Long id, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<User> user = userService.getById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "itSupport/users/edit";
        }
        addErrorMessage(model, "User not found");
        return "redirect:/itsupport/users";
    }
    
    @PostMapping("/edit/{id}")
    public String editUser(HttpSession session, @PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            user.setId(id);
            userService.save(user);
            return redirectWithSuccess("/itsupport/users", "User updated successfully!", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/itsupport/users/edit/" + id, "Failed to update user: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteUser(HttpSession session, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            userService.delete(id);
            return redirectWithSuccess("/itsupport/users", "User deleted successfully!", redirectAttributes);
        } catch (Exception e) {
            return redirectWithError("/itsupport/users", "Failed to delete user: " + e.getMessage(), redirectAttributes);
        }
    }
}