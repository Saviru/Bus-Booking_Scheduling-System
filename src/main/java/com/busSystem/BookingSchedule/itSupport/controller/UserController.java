package com.busSystem.BookingSchedule.itSupport.controller;

import com.busSystem.BookingSchedule.itSupport.model.LoginHistory;
import com.busSystem.BookingSchedule.itSupport.model.Role;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/it-support/users")
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "itSupport/user/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", userService.getAllRoles());
        return "itSupport/user/create";
    }
    
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           @RequestParam(required = false) List<Long> roles,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (userService.isUsernameExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }
        
        if (userService.isEmailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.getAllRoles());
            return "itSupport/user/create";
        }
        
        // Save the user first
        User savedUser = userService.saveUser(user);
        
        // Assign roles if selected
        if (roles != null && !roles.isEmpty()) {
            userService.assignRolesToUser(savedUser.getId(), new HashSet<>(roles));
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
        return "redirect:/it-support/users";
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        return "itSupport/user/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.getAllRoles());
        
        // Get IDs of user's roles for pre-selecting in the form
        Set<Long> userRoleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        model.addAttribute("userRoleIds", userRoleIds);
        
        return "itSupport/user/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           @RequestParam(required = false) List<Long> roles,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        User existingUser = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        
        // Check if username is changed and already exists
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userService.isUsernameExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }
        
        // Check if email is changed and already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userService.isEmailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.getAllRoles());
            
            // Get IDs of user's roles for pre-selecting in the form
            Set<Long> userRoleIds = existingUser.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet());
            model.addAttribute("userRoleIds", userRoleIds);
            
            return "itSupport/user/edit";
        }
        
        // Preserve existing values that shouldn't be changed through the form
        user.setId(id);
        user.setPassword(existingUser.getPassword()); // Don't change password through edit form
        user.setLastLogin(existingUser.getLastLogin());
        user.setCreatedAt(existingUser.getCreatedAt());
        
        // Save the user
        userService.saveUser(user);
        
        // Assign roles if selected
        if (roles != null) {
            userService.assignRolesToUser(id, new HashSet<>(roles));
        } else {
            // If no roles selected, assign empty set
            userService.assignRolesToUser(id, new HashSet<>());
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        return "redirect:/it-support/users";
    }
    
    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/it-support/users";
    }
    
    @GetMapping("/{id}/toggle-active")
    public String toggleUserActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.toggleUserActive(id);
        String status = user.isActive() ? "activated" : "deactivated";
        redirectAttributes.addFlashAttribute("successMessage", "User " + status + " successfully");
        return "redirect:/it-support/users";
    }
    
    @GetMapping("/{id}/toggle-lock")
    public String toggleUserLock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.toggleUserLock(id);
        String status = user.isLocked() ? "locked" : "unlocked";
        redirectAttributes.addFlashAttribute("successMessage", "User account " + status + " successfully");
        return "redirect:/it-support/users";
    }
    
    @GetMapping("/{id}/reset-password")
    public String showResetPasswordForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        return "itSupport/user/reset-password";
    }
    
    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                              @RequestParam String newPassword,
                              @RequestParam String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match");
            return "redirect:/it-support/users/" + id + "/reset-password";
        }
        
        userService.resetPassword(id, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully");
        return "redirect:/it-support/users";
    }
    
    @GetMapping("/{id}/login-history")
    public String viewLoginHistory(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        
        List<LoginHistory> loginHistory = userService.getUserLoginHistory(id);
        
        model.addAttribute("user", user);
        model.addAttribute("loginHistory", loginHistory);
        
        return "itSupport/user/login-history";
    }
    
    @GetMapping("/roles")
    public String listRoles(Model model) {
        List<Role> roles = userService.getAllRoles();
        model.addAttribute("roles", roles);
        return "itSupport/user/roles";
    }
    
    @GetMapping("/roles/create")
    public String showCreateRoleForm(Model model) {
        model.addAttribute("role", new Role());
        return "itSupport/user/role-create";
    }
    
    @PostMapping("/roles/create")
    public String createRole(@Valid @ModelAttribute("role") Role role,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        
        if (userService.isRoleNameExists(role.getName())) {
            result.rejectValue("name", "error.role", "Role name already exists");
        }
        
        if (result.hasErrors()) {
            return "itSupport/user/role-create";
        }
        
        userService.saveRole(role);
        redirectAttributes.addFlashAttribute("successMessage", "Role created successfully");
        return "redirect:/it-support/users/roles";
    }
    
    @GetMapping("/roles/{id}/edit")
    public String showEditRoleForm(@PathVariable Long id, Model model) {
        Role role = userService.getRoleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
        model.addAttribute("role", role);
        return "itSupport/user/role-edit";
    }
    
    @PostMapping("/roles/{id}/edit")
    public String updateRole(@PathVariable Long id,
                           @Valid @ModelAttribute("role") Role role,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        
        Role existingRole = userService.getRoleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
        
        if (!existingRole.getName().equals(role.getName()) && 
            userService.isRoleNameExists(role.getName())) {
            result.rejectValue("name", "error.role", "Role name already exists");
        }
        
        if (result.hasErrors()) {
            return "itSupport/user/role-edit";
        }
        
        role.setId(id);
        userService.saveRole(role);
        redirectAttributes.addFlashAttribute("successMessage", "Role updated successfully");
        return "redirect:/it-support/users/roles";
    }
    
    @GetMapping("/roles/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteRole(id);
        redirectAttributes.addFlashAttribute("successMessage", "Role deleted successfully");
        return "redirect:/it-support/users/roles";
    }
    
    @GetMapping("/login-report")
    public String showLoginReportForm() {
        return "itSupport/user/login-report";
    }
    
    @GetMapping("/generate-login-report")
    public String generateLoginReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<LoginHistory> loginHistory = userService.getLoginHistoryBetween(startDateTime, endDateTime);
        
        model.addAttribute("loginHistory", loginHistory);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "itSupport/user/login-report-results";
    }
}