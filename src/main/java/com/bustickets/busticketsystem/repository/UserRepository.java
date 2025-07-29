package com.bustickets.busticketsystem.repository;

import com.bustickets.busticketsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}