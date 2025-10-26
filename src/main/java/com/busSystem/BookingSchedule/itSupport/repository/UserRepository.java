package com.busSystem.BookingSchedule.itSupport.repository;

import com.busSystem.BookingSchedule.itSupport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByStatus(String status);
    Optional<User> findByEmailAndPassword(String email, String password);
}