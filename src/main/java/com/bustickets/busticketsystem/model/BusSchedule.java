package com.bustickets.busticketsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "bus_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private BusRoute busRoute;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column(nullable = false)
    private String status = "NORMAL"; // Default status: NORMAL, DELAYED, CANCELLED
}