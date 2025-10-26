package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class UserService extends com.busSystem.BookingSchedule.user.Service<User, Long> {

    @Autowired
    private UserRepository userRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }
    
    public Optional<User> authenticateUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}