package com.busSystem.BookingSchedule.service;

import com.busSystem.BookingSchedule.model.OperationsManager;
import com.busSystem.BookingSchedule.repository.OperationsManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OperationsManagerService {
    
    @Autowired
    private OperationsManagerRepository operationsManagerRepository;
    
    // Create
    public boolean registerManager(OperationsManager manager) {
        try {
            // Check if username, email, or employee ID already exists
            if (operationsManagerRepository.existsByUsername(manager.getUsername())) {
                return false; // Username already exists
            }
            if (operationsManagerRepository.existsByEmail(manager.getEmail())) {
                return false; // Email already exists
            }
            if (manager.getEmployeeId() != null && 
                operationsManagerRepository.existsByEmployeeId(manager.getEmployeeId())) {
                return false; // Employee ID already exists
            }
            
            // Simple password encoding (in production, use BCrypt)
            manager.setPassword(manager.getPassword());
            
            operationsManagerRepository.save(manager);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Read
    public Optional<OperationsManager> findById(Long id) {
        return operationsManagerRepository.findById(id);
    }
    
    public Optional<OperationsManager> findByUsername(String username) {
        return operationsManagerRepository.findByUsername(username);
    }
    
    public Optional<OperationsManager> findByEmail(String email) {
        return operationsManagerRepository.findByEmail(email);
    }
    
    public Optional<OperationsManager> findByEmployeeId(String employeeId) {
        return operationsManagerRepository.findByEmployeeId(employeeId);
    }
    
    public List<OperationsManager> getAllActiveManagers() {
        return operationsManagerRepository.findAllActiveManagers();
    }
    
    public List<OperationsManager> searchManagers(String search) {
        return operationsManagerRepository.searchActiveManagers(search);
    }
    
    public List<OperationsManager> getManagersByRole(OperationsManager.ManagerRole role) {
        return operationsManagerRepository.findActiveManagersByRole(role);
    }
    
    public List<OperationsManager> getManagersByStatus(OperationsManager.ManagerStatus status) {
        return operationsManagerRepository.findByStatus(status);
    }
    
    // Authentication
    public Optional<OperationsManager> authenticate(String identifier, String password) {
        Optional<OperationsManager> manager = 
            operationsManagerRepository.findByUsernameOrEmailIdentifier(identifier);
        
        if (manager.isPresent() && manager.get().isActive()) {
            // In a real application, you would compare hashed passwords
            if (manager.get().getPassword().equals(password)) {
                // Update last login
                manager.get().setLastLogin(LocalDateTime.now());
                operationsManagerRepository.save(manager.get());
                return manager;
            }
        }
        return Optional.empty();
    }
    
    // Update
    public boolean updateManager(OperationsManager manager) {
        try {
            if (manager.getId() != null && 
                operationsManagerRepository.existsById(manager.getId())) {
                operationsManagerRepository.save(manager);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean updateProfile(Long id, OperationsManager updatedManager) {
        Optional<OperationsManager> existingManager = operationsManagerRepository.findById(id);
        if (existingManager.isPresent()) {
            OperationsManager manager = existingManager.get();
            
            // Update allowed fields
            manager.setFirstName(updatedManager.getFirstName());
            manager.setLastName(updatedManager.getLastName());
            manager.setPhoneNumber(updatedManager.getPhoneNumber());
            manager.setDepartment(updatedManager.getDepartment());
            
            operationsManagerRepository.save(manager);
            return true;
        }
        return false;
    }
    
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Optional<OperationsManager> manager = operationsManagerRepository.findById(id);
        if (manager.isPresent()) {
            // In a real application, you would validate against hashed password
            if (manager.get().getPassword().equals(oldPassword)) {
                manager.get().setPassword(newPassword);
                operationsManagerRepository.save(manager.get());
                return true;
            }
        }
        return false;
    }
    
    public boolean updateStatus(Long id, OperationsManager.ManagerStatus status) {
        Optional<OperationsManager> manager = operationsManagerRepository.findById(id);
        if (manager.isPresent()) {
            manager.get().setStatus(status);
            operationsManagerRepository.save(manager.get());
            return true;
        }
        return false;
    }
    
    public boolean updateRole(Long id, OperationsManager.ManagerRole role) {
        Optional<OperationsManager> manager = operationsManagerRepository.findById(id);
        if (manager.isPresent()) {
            manager.get().setRole(role);
            operationsManagerRepository.save(manager.get());
            return true;
        }
        return false;
    }
    
    // Soft Delete
    public boolean deactivateManager(Long id) {
        Optional<OperationsManager> manager = operationsManagerRepository.findById(id);
        if (manager.isPresent()) {
            manager.get().setIsActive(false);
            manager.get().setStatus(OperationsManager.ManagerStatus.INACTIVE);
            operationsManagerRepository.save(manager.get());
            return true;
        }
        return false;
    }
    
    public boolean reactivateManager(Long id) {
        Optional<OperationsManager> manager = operationsManagerRepository.findById(id);
        if (manager.isPresent()) {
            manager.get().setIsActive(true);
            manager.get().setStatus(OperationsManager.ManagerStatus.ACTIVE);
            operationsManagerRepository.save(manager.get());
            return true;
        }
        return false;
    }
    
    // Hard Delete (for testing purposes only)
    public boolean deleteManager(Long id) {
        try {
            if (operationsManagerRepository.existsById(id)) {
                operationsManagerRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Validation helpers
    public boolean isUsernameAvailable(String username) {
        return !operationsManagerRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !operationsManagerRepository.existsByEmail(email);
    }
    
    public boolean isEmployeeIdAvailable(String employeeId) {
        return !operationsManagerRepository.existsByEmployeeId(employeeId);
    }
    
    // Statistics
    public long getActiveManagerCount() {
        return operationsManagerRepository.countActiveManagers();
    }
}