package com.bustickets.busticketsystem;

import com.bustickets.busticketsystem.model.User;
import com.bustickets.busticketsystem.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BusTicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusTicketSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserService userService) {
        return args -> {
            // Create admin account if it doesn't exist
            if (userService.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole("ADMIN");
                userService.save(admin);
            }
        };
    }
}