package com.busSystem.BookingSchedule.itSupport.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.itSupport.model.BackupConfig;
import com.busSystem.BookingSchedule.itSupport.service.BackupConfigService;
import com.busSystem.BookingSchedule.itSupport.service.DatabaseBackupService;
import com.busSystem.BookingSchedule.itSupport.model.BackupFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.util.Optional;

@org.springframework.stereotype.Controller
@RequestMapping("/itsupport/backups")
public class BackupManagementController extends Controller {

    @Autowired
    private BackupConfigService backupConfigService;
    
    @Autowired
    private DatabaseBackupService databaseBackupService;

    @GetMapping
    public String listBackups(HttpSession session, Model model,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String type) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            if (status != null && !status.isEmpty()) {
                model.addAttribute("backups", backupConfigService.getBackupConfigsByStatus(status));
            } else if (type != null && !type.isEmpty()) {
                model.addAttribute("backups", backupConfigService.getBackupConfigsByType(type));
            } else {
                model.addAttribute("backups", backupConfigService.getAll());
            }

            // Add list of backup files
            model.addAttribute("backupFiles", databaseBackupService.getAllBackupFiles());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("selectedType", type);
            return "itSupport/backups/list";
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage(model, "Error loading backup data: " + e.getMessage());
            model.addAttribute("backups", backupConfigService.getAll());
            model.addAttribute("backupFiles", databaseBackupService.getAllBackupFiles());
            return "itSupport/backups/list";
        }
    }
    
    @GetMapping("/generate")
    public String generateBackup(HttpSession session, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            String backupFilePath = databaseBackupService.generateBackup();
            return redirectWithSuccess("/itsupport/backups",
                "Database backup generated successfully: " + new File(backupFilePath).getName(),
                redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return redirectWithError("/itsupport/backups",
                "Failed to generate backup: " + e.getMessage(),
                redirectAttributes);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadBackup(HttpSession session, @PathVariable String fileName) {
        // Check authorization using base class method
        if (!isUserLoggedIn(session) || !hasRole(session, "IT_SUPPORT")) {
            return ResponseEntity.status(403).build();
        }

        try {
            BackupFile backupFile = databaseBackupService.getBackupFile(fileName);

            if (backupFile == null) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(backupFile.getPath());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                           "attachment; filename=\"" + backupFile.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/delete-file/{fileName}")
    public String deleteBackupFile(HttpSession session, @PathVariable String fileName,
                                   RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            boolean deleted = databaseBackupService.deleteBackupFile(fileName);
            if (deleted) {
                return redirectWithSuccess("/itsupport/backups",
                    "Backup file deleted successfully!", redirectAttributes);
            } else {
                return redirectWithError("/itsupport/backups",
                    "Backup file not found or could not be deleted", redirectAttributes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return redirectWithError("/itsupport/backups",
                "Failed to delete backup file: " + e.getMessage(), redirectAttributes);
        }
    }

    @GetMapping("/view/{id}")
    public String viewBackup(HttpSession session, @PathVariable Long id, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<BackupConfig> backup = backupConfigService.getById(id);
        if (backup.isPresent()) {
            model.addAttribute("backup", backup.get());
            return "itSupport/backups/view";
        }
        addErrorMessage(model, "Backup configuration not found");
        return "redirect:/itsupport/backups";
    }

    @GetMapping("/create")
    public String createBackupForm(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        model.addAttribute("backup", new BackupConfig());
        return "itSupport/backups/create";
    }
    
    @PostMapping("/create")
    public String createBackup(HttpSession session, @ModelAttribute BackupConfig backup, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            backupConfigService.save(backup);
            return redirectWithSuccess("/itsupport/backups", "Backup configuration created successfully!", redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return redirectWithError("/itsupport/backups/create", "Failed to create backup configuration: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editBackupForm(HttpSession session, @PathVariable Long id, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        Optional<BackupConfig> backup = backupConfigService.getById(id);
        if (backup.isPresent()) {
            model.addAttribute("backup", backup.get());
            return "itSupport/backups/edit";
        }
        addErrorMessage(model, "Backup configuration not found");
        return "redirect:/itsupport/backups";
    }
    
    @PostMapping("/edit/{id}")
    public String editBackup(HttpSession session, @PathVariable Long id, @ModelAttribute BackupConfig backup, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            backup.setId(id);
            backupConfigService.save(backup);
            return redirectWithSuccess("/itsupport/backups", "Backup configuration updated successfully!", redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return redirectWithError("/itsupport/backups/edit/" + id, "Failed to update backup configuration: " + e.getMessage(), redirectAttributes);
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteBackup(HttpSession session, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            backupConfigService.delete(id);
            return redirectWithSuccess("/itsupport/backups", "Backup configuration deleted successfully!", redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return redirectWithError("/itsupport/backups", "Failed to delete backup configuration: " + e.getMessage(), redirectAttributes);
        }
    }
}
