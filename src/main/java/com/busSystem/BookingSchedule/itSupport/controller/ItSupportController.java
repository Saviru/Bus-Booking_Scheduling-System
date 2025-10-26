package com.busSystem.BookingSchedule.itSupport.controller;

import com.busSystem.BookingSchedule.user.Controller;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@org.springframework.stereotype.Controller
@RequestMapping("/itsupport")
public class ItSupportController extends Controller {

    @Autowired
    private UserService userService;
    
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        String authCheck = checkAuthorization(session, "IT_SUPPORT");
        if (authCheck != null) return authCheck;

        try {
            return "itSupport/dashboard";
        } catch (Exception e) {
            return handleException(e, model, "itSupport/dashboard");
        }
    }
}