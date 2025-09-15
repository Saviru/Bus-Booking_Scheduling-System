package com.busSystem.BookingSchedule.itSupport.service;

import com.busSystem.BookingSchedule.itSupport.model.LoginHistory;
import com.busSystem.BookingSchedule.itSupport.model.Role;
import com.busSystem.BookingSchedule.itSupport.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    User saveUser(User user);
    
    void deleteUser(Long id);
    
    boolean isUsernameExists(String username);
    
    boolean isEmailExists(String email);
    
    List<User> getActiveUsers();
    
    List<User> getInactiveUsers();
    
    User toggleUserActive(Long id);
    
    User toggleUserLock(Long id);
    
    void resetPassword(Long id, String newPassword);
    
    List<Role> getAllRoles();
    
    Optional<Role> getRoleById(Long id);
    
    Optional<Role> getRoleByName(String name);
    
    Role saveRole(Role role);
    
    void deleteRole(Long id);
    
    boolean isRoleNameExists(String name);
    
    User assignRolesToUser(Long userId, Set<Long> roleIds);
    
    List<LoginHistory> getUserLoginHistory(Long userId);
    
    List<LoginHistory> getLoginHistoryBetween(LocalDateTime start, LocalDateTime end);
    
    void recordLoginAttempt(String username, String ipAddress, String userAgent, String status);
}