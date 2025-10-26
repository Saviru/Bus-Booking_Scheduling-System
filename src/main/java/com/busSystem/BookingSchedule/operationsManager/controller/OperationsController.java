package com.busSystem.BookingSchedule.operationsManager.controller;

import com.busSystem.BookingSchedule.user.Controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@RequestMapping("/operations")
public class OperationsController extends Controller {

    @GetMapping
    public String operationsDashboard(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "OPERATION_MANAGER");
        if (authCheck != null) return authCheck;

        try {
            return "operations/dashboard";
        } catch (Exception e) {
            return handleException(e, model, "operations/dashboard");
        }
    }
}