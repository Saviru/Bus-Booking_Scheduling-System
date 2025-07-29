package com.bustickets.busticketsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private BusSchedule schedule;
    
    @Column(nullable = false)
    private String passengerName;
    
    @Column(nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();
}