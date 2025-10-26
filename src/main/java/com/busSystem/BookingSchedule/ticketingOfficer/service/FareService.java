package com.busSystem.BookingSchedule.ticketingOfficer.service;

import com.busSystem.BookingSchedule.ticketingOfficer.model.Fare;
import com.busSystem.BookingSchedule.ticketingOfficer.repository.FareRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class FareService extends Service<Fare, Long> {

    @Autowired
    private FareRepository fareRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Fare> getAll() {
        return fareRepository.findAll();
    }

    @Override
    public Optional<Fare> getById(Long id) {
        return fareRepository.findById(id);
    }

    @Override
    public Fare save(Fare fare) {
        fare.setUpdatedDate(LocalDateTime.now());
        if (fare.getId() == null) {
            fare.setCreatedDate(LocalDateTime.now());
        }

        // If the new fare is ACTIVE, inactivate all other fares of the same ticket type for the same route
        if ("ACTIVE".equals(fare.getStatus()) && fare.getRouteId() != null && fare.getTicketType() != null) {
            List<Fare> existingFares = fareRepository.findActiveByRouteIdAndTicketType(fare.getRouteId(), fare.getTicketType());
            for (Fare existingFare : existingFares) {
                // Don't inactivate the fare we're currently saving (in case of update)
                if (!existingFare.getId().equals(fare.getId())) {
                    existingFare.setStatus("INACTIVE");
                    existingFare.setUpdatedDate(LocalDateTime.now());
                    fareRepository.save(existingFare);
                }
            }
        }

        return fareRepository.save(fare);
    }

    @Override
    public void delete(Long id) {
        fareRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return fareRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Fare> getFaresByRoute(Long routeId) {
        return fareRepository.findByRouteId(routeId);
    }
    
    public List<Fare> getFaresByType(String ticketType) {
        return fareRepository.findByTicketType(ticketType);
    }
    
    public List<Fare> getFaresByStatus(String status) {
        return fareRepository.findByStatus(status);
    }

    public List<Fare> getActiveFaresByRouteAndType(Long routeId, String ticketType) {
        return fareRepository.findActiveByRouteIdAndTicketType(routeId, ticketType);
    }

    public List<Fare> getValidFares() {
        return fareRepository.findValidFares(LocalDateTime.now());
    }
}