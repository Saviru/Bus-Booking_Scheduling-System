package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.LoginHistory;
import com.busSystem.BookingSchedule.itSupport.model.Role;
import com.busSystem.BookingSchedule.itSupport.model.User;
import com.busSystem.BookingSchedule.itSupport.repository.LoginHistoryRepository;
import com.busSystem.BookingSchedule.itSupport.repository.RoleRepository;
import com.busSystem.BookingSchedule.itSupport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          LoginHistoryRepository loginHistoryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        // In a real application, you would encrypt the password before saving
        // For simplicity, we're saving plaintext passwords here
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByActive(true);
    }

    @Override
    public List<User> getInactiveUsers() {
        return userRepository.findByActive(false);
    }

    @Override
    @Transactional
    public User toggleUserActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User toggleUserLock(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setLocked(!user.isLocked());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        // In a real application, you would encrypt the password before saving
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public boolean isRoleNameExists(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    @Transactional
    public User assignRolesToUser(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        
        // Clear existing roles
        user.getRoles().clear();
        
        // Add new roles
        Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + roleId)))
                .collect(Collectors.toSet());
        
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public List<LoginHistory> getUserLoginHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        return loginHistoryRepository.findByUserOrderByLoginTimeDesc(user);
    }

    @Override
    public List<LoginHistory> getLoginHistoryBetween(LocalDateTime start, LocalDateTime end) {
        return loginHistoryRepository.findByLoginTimeBetween(start, end);
    }

    @Override
    @Transactional
    public void recordLoginAttempt(String username, String ipAddress, String userAgent, String status) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Record login history
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUser(user);
            loginHistory.setLoginTime(LocalDateTime.now());
            loginHistory.setIpAddress(ipAddress);
            loginHistory.setUserAgent(userAgent);
            loginHistory.setStatus(status);
            loginHistoryRepository.save(loginHistory);
            
            // Update last login time if successful
            if ("SUCCESS".equals(status)) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            }
        }
    }
}