package com.busSystem.BookingSchedule.repository;

import com.busSystem.BookingSchedule.model.OperationsManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationsManagerRepository extends JpaRepository<OperationsManager, Long> {
    
    Optional<OperationsManager> findByUsername(String username);
    
    Optional<OperationsManager> findByEmail(String email);
    
    Optional<OperationsManager> findByEmployeeId(String employeeId);
    
    @Query("SELECT om FROM OperationsManager om WHERE (om.username = :identifier OR om.email = :identifier) AND om.isActive = true")
    Optional<OperationsManager> findByUsernameOrEmailIdentifier(@Param("identifier") String identifier);
    
    List<OperationsManager> findByStatus(OperationsManager.ManagerStatus status);
    
    List<OperationsManager> findByRole(OperationsManager.ManagerRole role);
    
    @Query("SELECT om FROM OperationsManager om WHERE om.isActive = true AND om.status = 'ACTIVE'")
    List<OperationsManager> findAllActiveManagers();
    
    @Query("SELECT om FROM OperationsManager om WHERE (om.firstName LIKE %:search% OR om.lastName LIKE %:search% OR om.username LIKE %:search% OR om.employeeId LIKE %:search%) AND om.isActive = true")
    List<OperationsManager> searchActiveManagers(@Param("search") String search);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeId(String employeeId);
    
    @Query("SELECT COUNT(om) FROM OperationsManager om WHERE om.isActive = true AND om.status = 'ACTIVE'")
    long countActiveManagers();
    
    @Query("SELECT om FROM OperationsManager om WHERE om.role = :role AND om.isActive = true AND om.status = 'ACTIVE'")
    List<OperationsManager> findActiveManagersByRole(@Param("role") OperationsManager.ManagerRole role);
}