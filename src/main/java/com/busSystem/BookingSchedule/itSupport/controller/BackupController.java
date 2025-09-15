package com.busSystem.BookingSchedule.itSupport.controller;

import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import com.busSystem.BookingSchedule.itSupport.model.BackupHistory;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.BackupService;
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
import java.util.List;

@Controller
@RequestMapping("/it-support/backup")
public class BackupController {

    private final BackupService backupService;
    private final UserService userService;
    
    @Autowired
    public BackupController(BackupService backupService, UserService userService) {
        this.backupService = backupService;
        this.userService = userService;
    }
    
    @GetMapping("/configs")
    public String listBackupConfigs(Model model) {
        List<BackupConfig> configs = backupService.getAllBackupConfigs();
        model.addAttribute("configs", configs);
        return "itSupport/backup/config-list";
    }
    
    @GetMapping("/configs/create")
    public String showCreateConfigForm(Model model) {
        model.addAttribute("config", new BackupConfig());
        return "itSupport/backup/config-create";
    }
    
    @PostMapping("/configs/create")
    public String createBackupConfig(@Valid @ModelAttribute("config") BackupConfig config,
                                   BindingResult result,
                                   @RequestParam(name = "userId", required = false) Long userId,
                                   RedirectAttributes redirectAttributes) {
        
        if (backupService.isBackupConfigNameExists(config.getName())) {
            result.rejectValue("name", "error.config", "Configuration name already exists");
        }
        
        if (result.hasErrors()) {
            return "itSupport/backup/config-create";
        }
        
        // Set the modified by user if provided
        if (userId != null) {
            userService.getUserById(userId).ifPresent(config::setModifiedBy);
        }
        
        backupService.saveBackupConfig(config);
        redirectAttributes.addFlashAttribute("successMessage", "Backup configuration created successfully");
        return "redirect:/it-support/backup/configs";
    }
    
    @GetMapping("/configs/{id}")
    public String viewBackupConfig(@PathVariable Long id, Model model) {
        BackupConfig config = backupService.getBackupConfigById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid backup configuration ID: " + id));
        
        // Get history for this config
        List<BackupHistory> history = backupService.getBackupHistoryByConfig(id);
        
        model.addAttribute("config", config);
        model.addAttribute("history", history);
        return "itSupport/backup/config-view";
    }
    
    @GetMapping("/configs/{id}/edit")
    public String showEditConfigForm(@PathVariable Long id, Model model) {
        BackupConfig config = backupService.getBackupConfigById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid backup configuration ID: " + id));
        model.addAttribute("config", config);
        return "itSupport/backup/config-edit";
    }
    
    @PostMapping("/configs/{id}/edit")
    public String updateBackupConfig(@PathVariable Long id,
                                   @Valid @ModelAttribute("config") BackupConfig config,
                                   BindingResult result,
                                   @RequestParam(name = "userId", required = false) Long userId,
                                   RedirectAttributes redirectAttributes) {
        
        BackupConfig existingConfig = backupService.getBackupConfigById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid backup configuration ID: " + id));
        
        if (!existingConfig.getName().equals(config.getName()) && 
            backupService.isBackupConfigNameExists(config.getName())) {
            result.rejectValue("name", "error.config", "Configuration name already exists");
        }
        
        if (result.hasErrors()) {
            return "itSupport/backup/config-edit";
        }
        
        // Set the ID and modified by user
        config.setId(id);
        
        if (userId != null) {
            userService.getUserById(userId).ifPresent(config::setModifiedBy);
        }
        
        backupService.saveBackupConfig(config);
        redirectAttributes.addFlashAttribute("successMessage", "Backup configuration updated successfully");
        return "redirect:/it-support/backup/configs";
    }
    
    @GetMapping("/configs/{id}/delete")
    public String deleteBackupConfig(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        backupService.deleteBackupConfig(id);
        redirectAttributes.addFlashAttribute("successMessage", "Backup configuration deleted successfully");
        return "redirect:/it-support/backup/configs";
    }
    
    @GetMapping("/history")
    public String listBackupHistory(Model model) {
        List<BackupHistory> history = backupService.getRecentBackupHistory();
        model.addAttribute("history", history);
        return "itSupport/backup/history-list";
    }
    
    @GetMapping("/history/search")
    public String searchBackupHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        List<BackupHistory> history;
        
        if (status != null && !status.isEmpty()) {
            history = backupService.getBackupHistoryByStatus(status);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            history = backupService.getBackupHistoryByDateRange(startDateTime, endDateTime);
        } else {
            history = backupService.getRecentBackupHistory();
        }
        
        model.addAttribute("history", history);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedStatus", status);
        
        return "itSupport/backup/history-list";
    }
    
    @GetMapping("/history/{id}")
    public String viewBackupHistory(@PathVariable Long id, Model model) {
        BackupHistory history = backupService.getBackupHistoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid backup history ID: " + id));
        model.addAttribute("backup", history);
        return "itSupport/backup/history-view";
    }
    
    @GetMapping("/history/{id}/delete")
    public String deleteBackupHistory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // In a real application, this would also delete the actual backup file
        backupService.deleteBackupHistory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Backup history record deleted successfully");
        return "redirect:/it-support/backup/history";
    }
    
    @GetMapping("/manual")
    public String showManualBackupForm(Model model) {
        List<BackupConfig> configs = backupService.getActiveBackupConfigs();
        model.addAttribute("configs", configs);
        return "itSupport/backup/manual-backup";
    }
    
    @PostMapping("/manual")
    public String initiateManualBackup(@RequestParam Long configId,
                                     @RequestParam(required = false) Long userId,
                                     RedirectAttributes redirectAttributes) {
        
        // Get the user who initiated the backup
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId)
                    .orElse(null);
        }
        
        // Initiate the backup
        BackupHistory backupHistory = backupService.initiateManualBackup(configId, user);
        
        // For demo purposes, simulate the backup completion
        // In a real application, this would be handled by a background job
        boolean success = Math.random() > 0.2; // 80% success rate for simulation
        backupService.simulateBackupCompletion(backupHistory.getId(), success);
        
        redirectAttributes.addFlashAttribute("successMessage", "Manual backup initiated successfully");
        return "redirect:/it-support/backup/history";
    }
}