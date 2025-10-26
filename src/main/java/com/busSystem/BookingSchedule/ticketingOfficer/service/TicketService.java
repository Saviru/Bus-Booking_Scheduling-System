package com.busSystem.BookingSchedule.ticketingOfficer.service;

import com.busSystem.BookingSchedule.ticketingOfficer.model.Ticket;
import com.busSystem.BookingSchedule.ticketingOfficer.repository.TicketRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class TicketService extends Service<Ticket, Long> {

    @Autowired
    private TicketRepository ticketRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket save(Ticket ticket) {
        ticket.setUpdatedDate(LocalDateTime.now());
        if (ticket.getId() == null) {
            ticket.setCreatedDate(LocalDateTime.now());
            ticket.setBookingDate(LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return ticketRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Ticket> getTicketsByPassenger(Long passengerId) {
        return ticketRepository.findByPassengerId(passengerId);
    }
    
    public List<Ticket> getTicketsByRoute(Long routeId) {
        return ticketRepository.findByRouteId(routeId);
    }
    
    public List<Ticket> getTicketsByType(String ticketType) {
        return ticketRepository.findByTicketType(ticketType);
    }
    
    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }
    
    public List<Ticket> getTicketsByTravelDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return ticketRepository.findByTravelDateBetween(startDate, endDate);
    }
}