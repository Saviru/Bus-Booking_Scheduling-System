package com.busSystem.BookingSchedule.passenger.controller;

import com.busSystem.BookingSchedule.passenger.service.BookingService;
import com.busSystem.BookingSchedule.operationsManager.service.ScheduleService;
import com.busSystem.BookingSchedule.operationsManager.service.BusService;
import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import com.busSystem.BookingSchedule.operationsManager.model.Bus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/seats")
public class SeatAvailabilityController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private BusService busService;

    @GetMapping("/availability")
    public Map<String, Object> getSeatAvailability(@RequestParam Long scheduleId,
                                                     @RequestParam String travelDate) {
        try {
            // Parse date format yyyy-MM-dd
            LocalDateTime travelDateTime;
            if (travelDate.length() == 10) {
                travelDateTime = LocalDateTime.parse(travelDate + "T00:00:00");
            } else {
                travelDateTime = LocalDateTime.parse(travelDate);
            }

            // Get the schedule to find the bus
            Optional<Schedule> scheduleOpt = scheduleService.getById(scheduleId);
            if (scheduleOpt.isEmpty()) {
                return Map.of("error", "Schedule not found");
            }

            Schedule schedule = scheduleOpt.get();
            Long busId = schedule.getBusId();

            // Get total seats from bus (default 60)
            int totalSeats = 60;
            if (busId != null) {
                Optional<Bus> busOpt = busService.getById(busId);
                if (busOpt.isPresent()) {
                    totalSeats = busOpt.get().getTotalSeats();
                }
            }

            // Get booked seats
            List<String> bookedSeats = bookingService.getBookedSeats(scheduleId, travelDateTime);

            // Generate all seat numbers
            List<String> allSeats = new ArrayList<>();
            for (int i = 1; i <= totalSeats; i++) {
                allSeats.add(String.valueOf(i));
            }

            // Calculate available seats
            List<String> availableSeats = new ArrayList<>(allSeats);
            availableSeats.removeAll(bookedSeats);

            Map<String, Object> response = new HashMap<>();
            response.put("totalSeats", totalSeats);
            response.put("bookedSeats", bookedSeats);
            response.put("availableSeats", availableSeats);
            response.put("availableCount", availableSeats.size());
            response.put("bookedCount", bookedSeats.size());

            return response;
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @GetMapping("/check")
    public Map<String, Object> checkSeatAvailability(@RequestParam Long scheduleId,
                                                       @RequestParam String travelDate,
                                                       @RequestParam String seatNumber) {
        try {
            LocalDateTime travelDateTime = LocalDateTime.parse(travelDate);
            boolean isAvailable = bookingService.isSeatAvailable(scheduleId, travelDateTime, seatNumber);

            return Map.of(
                "available", isAvailable,
                "seatNumber", seatNumber
            );
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
