package com.busSystem.BookingSchedule.service;

import com.busSystem.BookingSchedule.model.Passenger;
import com.busSystem.BookingSchedule.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PassengerService {
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    // Create
    public boolean registerPassenger(Passenger passenger) {
        try {
            // Check if username or email already exists
            if (passengerRepository.existsByUsername(passenger.getUsername())) {
                return false; // Username already exists
            }
            if (passengerRepository.existsByEmail(passenger.getEmail())) {
                return false; // Email already exists
            }
            
            // Simple password encoding (in production, use BCrypt)
            passenger.setPassword(passenger.getPassword());
            
            passengerRepository.save(passenger);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Read
    public Optional<Passenger> findById(Long id) {
        return passengerRepository.findById(id);
    }
    
    public Optional<Passenger> findByUsername(String username) {
        return passengerRepository.findByUsername(username);
    }
    
    public Optional<Passenger> findByEmail(String email) {
        return passengerRepository.findByEmail(email);
    }
    
    public List<Passenger> getAllActivePassengers() {
        return passengerRepository.findAllActivePassengers();
    }
    
    public List<Passenger> searchPassengers(String name) {
        return passengerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }
    
    // Authentication
    public Optional<Passenger> authenticate(String identifier, String password) {
        Optional<Passenger> passenger = passengerRepository.findByUsernameOrEmailIdentifier(identifier);

        return passenger;
    }
    
    // Update
    public boolean updatePassenger(Passenger passenger) {
        try {
            if (passenger.getId() != null && passengerRepository.existsById(passenger.getId())) {
                passengerRepository.save(passenger);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean updateProfile(Long id, Passenger updatedPassenger) {
        Optional<Passenger> existingPassenger = passengerRepository.findById(id);
        if (existingPassenger.isPresent()) {
            Passenger passenger = existingPassenger.get();
            
            // Update allowed fields
            passenger.setFirstName(updatedPassenger.getFirstName());
            passenger.setLastName(updatedPassenger.getLastName());
            passenger.setPhoneNumber(updatedPassenger.getPhoneNumber());
            passenger.setDateOfBirth(updatedPassenger.getDateOfBirth());
            passenger.setGender(updatedPassenger.getGender());
            passenger.setEmergencyContact(updatedPassenger.getEmergencyContact());
            passenger.setEmergencyPhone(updatedPassenger.getEmergencyPhone());
            passenger.setSeatPreference(updatedPassenger.getSeatPreference());
            passenger.setFrequentDestinations(updatedPassenger.getFrequentDestinations());
            passenger.setAccessibilityNeeds(updatedPassenger.getAccessibilityNeeds());
            passenger.setMealPreference(updatedPassenger.getMealPreference());
            
            passengerRepository.save(passenger);
            return true;
        }
        return false;
    }
    
    public boolean updatePaymentMethods(Long id, String paymentMethods) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            passenger.get().setPaymentMethods(paymentMethods);
            passengerRepository.save(passenger.get());
            return true;
        }
        return false;
    }
    
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            passenger.get().setPassword(newPassword);
            passengerRepository.save(passenger.get());
            return true;
        }
        return false;
    }
    
    // Delete
    public boolean deletePassenger(Long id) {
        try {
            if (passengerRepository.existsById(id)) {
                passengerRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean deactivatePassenger(Long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            passenger.get().setIsActive(false);
            passengerRepository.save(passenger.get());
            return true;
        }
        return false;
    }
    
    public boolean removePaymentMethod(Long id, String paymentMethod) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            String currentMethods = passenger.get().getPaymentMethods();
            if (currentMethods != null) {
                String updatedMethods = currentMethods.replace(paymentMethod, "").replace(",,", ",");
                passenger.get().setPaymentMethods(updatedMethods);
                passengerRepository.save(passenger.get());
                return true;
            }
        }
        return false;
    }

    
    public boolean isUsernameAvailable(String username) {
        return !passengerRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !passengerRepository.existsByEmail(email);
    }
}