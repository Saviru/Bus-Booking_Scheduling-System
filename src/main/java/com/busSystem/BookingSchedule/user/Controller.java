package com.busSystem.BookingSchedule.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class Controller {

    // Common session validation methods
    protected boolean isUserLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    protected boolean hasRole(HttpSession session, String requiredRole) {
        Object userRole = session.getAttribute("userRole");
        return userRole != null && userRole.equals(requiredRole);
    }

    protected boolean hasAnyRole(HttpSession session, String... roles) {
        Object userRole = session.getAttribute("userRole");
        if (userRole == null) return false;

        for (String role : roles) {
            if (userRole.equals(role)) return true;
        }
        return false;
    }

    protected Long getCurrentUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Long) userId : null;
    }

    protected String getCurrentUserRole(HttpSession session) {
        Object userRole = session.getAttribute("userRole");
        return userRole != null ? (String) userRole : null;
    }

    // Common redirect methods
    protected String redirectToLogin() {
        return "redirect:/login";
    }

    protected String redirectToUnauthorized() {
        return "redirect:/unauthorized";
    }

    protected String redirectWithSuccess(String path, String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:" + path;
    }

    protected String redirectWithError(String path, String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return "redirect:" + path;
    }

    // Common model attribute methods
    protected void addSuccessMessage(Model model, String message) {
        model.addAttribute("successMessage", message);
    }

    protected void addErrorMessage(Model model, String message) {
        model.addAttribute("errorMessage", message);
    }

    protected void addWarningMessage(Model model, String message) {
        model.addAttribute("warningMessage", message);
    }

    protected void addInfoMessage(Model model, String message) {
        model.addAttribute("infoMessage", message);
    }

    // Common authorization check method
    protected String checkAuthorization(HttpSession session, String... allowedRoles) {
        if (!isUserLoggedIn(session)) {
            return redirectToLogin();
        }

        if (!hasAnyRole(session, allowedRoles)) {
            return redirectToUnauthorized();
        }

        return null; // Authorization passed
    }

    // Common exception handling
    protected String handleException(Exception e, Model model, String defaultView) {
        addErrorMessage(model, "An error occurred: " + e.getMessage());
        return defaultView;
    }

    // Common pagination helper
    protected void addPaginationAttributes(Model model, int currentPage, int totalPages, int pageSize) {
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("hasPrevious", currentPage > 0);
        model.addAttribute("hasNext", currentPage < totalPages - 1);
    }
}
