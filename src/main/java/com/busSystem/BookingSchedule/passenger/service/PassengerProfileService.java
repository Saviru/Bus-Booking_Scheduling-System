package com.busSystem.BookingSchedule.passenger.service;

import com.busSystem.BookingSchedule.passenger.model.PassengerProfile;
import com.busSystem.BookingSchedule.passenger.repository.PassengerProfileRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class PassengerProfileService extends Service<PassengerProfile, Long> {

    @Autowired
    private PassengerProfileRepository passengerProfileRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<PassengerProfile> getAll() {
        return passengerProfileRepository.findAll();
    }

    @Override
    public Optional<PassengerProfile> getById(Long id) {
        return passengerProfileRepository.findById(id);
    }

    @Override
    public PassengerProfile save(PassengerProfile profile) {
        profile.setUpdatedDate(LocalDateTime.now());
        if (profile.getId() == null) {
            profile.setCreatedDate(LocalDateTime.now());
        }
        return passengerProfileRepository.save(profile);
    }

    @Override
    public void delete(Long id) {
        passengerProfileRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return passengerProfileRepository.existsById(id);
    }

    // Specific business logic methods only
    public Optional<PassengerProfile> getProfileByUserId(Long userId) {
        return passengerProfileRepository.findByUserId(userId);
    }

    public boolean existsByUserId(Long userId) {
        return passengerProfileRepository.existsByUserId(userId);
    }
}