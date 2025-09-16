-- Create database
CREATE DATABASE IF NOT EXISTS bus_booking_db;
USE bus_booking_db;

-- Create passengers table
CREATE TABLE passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(10),
    date_of_birth VARCHAR(10),
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(15),
    seat_preference VARCHAR(20),
    frequent_destinations TEXT,
    accessibility_needs TEXT,
    meal_preference VARCHAR(50),
    payment_methods TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (is_active)
);

-- Insert 20 sample passengers
INSERT INTO passengers (
    username, email, password, first_name, last_name, phone_number, 
    date_of_birth, gender, emergency_contact, emergency_phone,
    seat_preference, frequent_destinations, accessibility_needs, 
    meal_preference, payment_methods
) VALUES 
('john_doe', 'john.doe@email.com', 'password123_encoded', 'John', 'Doe', '1234567890', 
 '1990-05-15', 'MALE', 'Jane Doe', '0987654321', 
 'Window', 'New York, Boston, Philadelphia', NULL, 'Non-Vegetarian', 'Visa *1234, PayPal john@email.com'),

('jane_smith', 'jane.smith@email.com', 'securepass_encoded', 'Jane', 'Smith', '2345678901', 
 '1985-08-22', 'FEMALE', 'John Smith', '1098765432', 
 'Aisle', 'Los Angeles, San Francisco, San Diego', NULL, 'Vegetarian', 'MasterCard *5678'),

('mike_johnson', 'mike.johnson@email.com', 'mypass123_encoded', 'Mike', 'Johnson', '3456789012', 
 '1992-12-03', 'MALE', 'Sarah Johnson', '2109876543', 
 'Any', 'Chicago, Detroit, Milwaukee', 'Wheelchair accessible', 'Non-Vegetarian', 'Visa *9876'),

('sarah_williams', 'sarah.williams@email.com', 'williams2024_encoded', 'Sarah', 'Williams', '4567890123', 
 '1988-03-17', 'FEMALE', 'Mike Williams', '3210987654', 
 'Window', 'Miami, Orlando, Tampa', NULL, 'Vegan', 'American Express *1111, PayPal sarah@email.com'),

('david_brown', 'david.brown@email.com', 'brownpass_encoded', 'David', 'Brown', '5678901234', 
 '1995-07-28', 'MALE', 'Lisa Brown', '4321098765', 
 'Aisle', 'Seattle, Portland, Vancouver', NULL, 'Non-Vegetarian', 'Visa *2222'),

('lisa_davis', 'lisa.davis@email.com', 'davis123_encoded', 'Lisa', 'Davis', '6789012345', 
 '1991-11-09', 'FEMALE', 'David Davis', '5432109876', 
 'Window', 'Atlanta, Charlotte, Nashville', NULL, 'Vegetarian', 'MasterCard *3333'),

('robert_wilson', 'robert.wilson@email.com', 'wilson2024_encoded', 'Robert', 'Wilson', '7890123456', 
 '1987-01-14', 'MALE', 'Mary Wilson', '6543210987', 
 'Any', 'Dallas, Houston, Austin', 'Extra legroom needed', 'Non-Vegetarian', 'Visa *4444, PayPal robert@email.com'),

('mary_taylor', 'mary.taylor@email.com', 'taylor_pass_encoded', 'Mary', 'Taylor', '8901234567', 
 '1993-06-25', 'FEMALE', 'Robert Taylor', '7654321098', 
 'Window', 'Phoenix, Tucson, Flagstaff', NULL, 'Halal', 'MasterCard *5555'),

('james_anderson', 'james.anderson@email.com', 'anderson123_encoded', 'James', 'Anderson', '9012345678', 
 '1989-04-11', 'MALE', 'Jennifer Anderson', '8765432109', 
 'Aisle', 'Denver, Colorado Springs, Boulder', NULL, 'Non-Vegetarian', 'Visa *6666'),

('jennifer_thomas', 'jennifer.thomas@email.com', 'thomas2024_encoded', 'Jennifer', 'Thomas', '0123456789', 
 '1994-09-30', 'FEMALE', 'James Thomas', '9876543210', 
 'Window', 'Las Vegas, Reno, Carson City', NULL, 'Vegetarian', 'American Express *7777, PayPal jennifer@email.com'),

('william_garcia', 'william.garcia@email.com', 'garcia_pass_encoded', 'William', 'Garcia', '1357924680', 
 '1986-02-18', 'MALE', 'Maria Garcia', '0246813579', 