-- Create database
CREATE DATABASE IF NOT EXISTS bus_booking_db;
USE bus_booking_db;

-- Create passengers table
CREATE TABLE if not exists passengers (
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

-- Create routes table
CREATE TABLE if not exists routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_code VARCHAR(20) UNIQUE NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    stops TEXT,
    distance DOUBLE NOT NULL,
    duration INTEGER NOT NULL,
    base_fare DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_route_code (route_code),
    INDEX idx_origin_dest (origin, destination),
    INDEX idx_status (status)
);

-- Create schedules table
CREATE TABLE if not exists schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    bus_number VARCHAR(20),
    total_seats INTEGER NOT NULL,
    available_seats INTEGER,
    status ENUM('ACTIVE', 'INACTIVE', 'CANCELLED', 'COMPLETED') DEFAULT 'ACTIVE',
    valid_from DATETIME,
    valid_until DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    INDEX idx_route_status (route_id, status),
    INDEX idx_departure (departure_time),
    INDEX idx_available_seats (available_seats)
);

-- Create bookings table
CREATE TABLE if not exists bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(50) UNIQUE NOT NULL,
    passenger_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    travel_date DATE NOT NULL,
    number_of_seats INTEGER NOT NULL,
    seat_numbers VARCHAR(100),
    seat_preference VARCHAR(20),
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'REFUNDED') DEFAULT 'PENDING',
    special_requests TEXT,
    cancellation_reason VARCHAR(255),
    refund_amount DECIMAL(10,2),
    refund_processed_at DATETIME,
    booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (passenger_id) REFERENCES passengers(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
    INDEX idx_booking_reference (booking_reference),
    INDEX idx_passenger (passenger_id),
    INDEX idx_schedule (schedule_id),
    INDEX idx_travel_date (travel_date),
    INDEX idx_status (status),
    INDEX idx_booking_date (booking_date)
);

-- Insert sample passengers (20 records)
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
 'Aisle', 'Salt Lake City, Provo, Ogden', NULL, 'Non-Vegetarian', 'Visa *8888'),

('maria_martinez', 'maria.martinez@email.com', 'martinez123_encoded', 'Maria', 'Martinez', '2468135790', 
 '1990-12-07', 'FEMALE', 'William Martinez', '1357924681', 
 'Window', 'Albuquerque, Santa Fe, Las Cruces', 'Dietary restrictions', 'Vegetarian', 'MasterCard *9999'),

('christopher_lee', 'christopher.lee@email.com', 'lee2024_encoded', 'Christopher', 'Lee', '3691472580', 
 '1985-08-19', 'MALE', 'Lisa Lee', '2580369147', 
 'Any', 'Portland, Eugene, Salem', NULL, 'Non-Vegetarian', 'Visa *1010, PayPal chris@email.com'),

('elizabeth_clark', 'elizabeth.clark@email.com', 'clark_pass_encoded', 'Elizabeth', 'Clark', '4702581639', 
 '1992-04-23', 'FEMALE', 'Michael Clark', '3691470258', 
 'Window', 'Richmond, Norfolk, Virginia Beach', NULL, 'Vegan', 'American Express *2020'),

('daniel_rodriguez', 'daniel.rodriguez@email.com', 'rodriguez123_encoded', 'Daniel', 'Rodriguez', '5813692470', 
 '1988-10-15', 'MALE', 'Ana Rodriguez', '4702581630', 
 'Aisle', 'San Antonio, El Paso, Corpus Christi', NULL, 'Halal', 'MasterCard *3030'),

('michelle_lewis', 'michelle.lewis@email.com', 'lewis2024_encoded', 'Michelle', 'Lewis', '6924703581', 
 '1991-06-08', 'FEMALE', 'David Lewis', '5813692471', 
 'Window', 'Kansas City, Topeka, Wichita', 'Mobility assistance needed', 'Vegetarian', 'Visa *4040'),

('matthew_walker', 'matthew.walker@email.com', 'walker_pass_encoded', 'Matthew', 'Walker', '7035814692', 
 '1987-01-29', 'MALE', 'Sarah Walker', '6924703582', 
 'Any', 'Little Rock, Hot Springs, Pine Bluff', NULL, 'Non-Vegetarian', 'PayPal matthew@email.com'),

('ashley_hall', 'ashley.hall@email.com', 'hall123_encoded', 'Ashley', 'Hall', '8146925703', 
 '1993-11-12', 'FEMALE', 'Brandon Hall', '7035814693', 
 'Window', 'Baton Rouge, New Orleans, Shreveport', NULL, 'Vegetarian', 'Visa *5050, MasterCard *6060'),

('joshua_allen', 'joshua.allen@email.com', 'allen2024_encoded', 'Joshua', 'Allen', '9257036814', 
 '1989-07-04', 'MALE', 'Amanda Allen', '8146925704', 
 'Aisle', 'Charleston, Columbia, Greenville', NULL, 'Non-Vegetarian', 'American Express *7070'),

('amanda_young', 'amanda.young@email.com', 'young_pass_encoded', 'Amanda', 'Young', '0368147925', 
 '1994-03-26', 'FEMALE', 'Joshua Young', '9257036815', 
 'Window', 'Raleigh, Charlotte, Greensboro', NULL, 'Vegan', 'Visa *8080, PayPal amanda@email.com');

-- Insert sample routes
INSERT INTO routes (route_code, origin, destination, stops, distance, duration, base_fare) VALUES
('RT001', 'New York', 'Boston', 'Hartford,Springfield', 215.5, 240, 45.00),
('RT002', 'Los Angeles', 'San Francisco', 'Bakersfield,Fresno', 382.1, 420, 75.00),
('RT003', 'Chicago', 'Detroit', 'Kalamazoo,Grand Rapids', 284.3, 300, 55.00),
('RT004', 'Miami', 'Orlando', 'Fort Lauderdale,West Palm Beach', 235.2, 270, 40.00),
('RT005', 'Seattle', 'Portland', 'Olympia,Centralia', 173.8, 180, 35.00),
('RT006', 'Atlanta', 'Charlotte', 'Greenville,Spartanburg', 244.1, 285, 50.00),
('RT007', 'Dallas', 'Houston', 'Huntsville,Conroe', 241.5, 270, 48.00),
('RT008', 'Phoenix', 'Las Vegas', 'Flagstaff,Kingman', 297.4, 330, 60.00),
('RT009', 'Denver', 'Salt Lake City', 'Grand Junction,Green River', 525.3, 480, 85.00),
('RT010', 'Washington DC', 'Philadelphia', 'Baltimore,Wilmington', 140.8, 165, 38.00);

-- Insert sample schedules
INSERT INTO schedules (route_id, departure_time, arrival_time, bus_number, total_seats, available_seats) VALUES
(1, '08:00:00', '12:00:00', 'BUS001', 45, 45),
(1, '14:00:00', '18:00:00', 'BUS002', 45, 45),
(1, '20:00:00', '00:00:00', 'BUS003', 45, 45),
(2, '07:30:00', '14:30:00', 'BUS004', 50, 50),
(2, '15:00:00', '22:00:00', 'BUS005', 50, 50),
(3, '09:00:00', '14:00:00', 'BUS006', 40, 40),
(3, '16:30:00', '21:30:00', 'BUS007', 40, 40),
(4, '06:00:00', '10:30:00', 'BUS008', 48, 48),
(4, '11:00:00', '15:30:00', 'BUS009', 48, 48),
(4, '17:00:00', '21:30:00', 'BUS010', 48, 48),
(5, '08:30:00', '11:30:00', 'BUS011', 35, 35),
(5, '13:00:00', '16:00:00', 'BUS012', 35, 35),
(5, '18:00:00', '21:00:00', 'BUS013', 35, 35),
(6, '07:00:00', '11:45:00', 'BUS014', 42, 42),
(6, '15:30:00', '20:15:00', 'BUS015', 42, 42),
(7, '09:30:00', '14:00:00', 'BUS016', 46, 46),
(7, '16:00:00', '20:30:00', 'BUS017', 46, 46),
(8, '10:00:00', '15:30:00', 'BUS018', 44, 44),
(8, '19:00:00', '00:30:00', 'BUS019', 44, 44),
(9, '06:30:00', '14:30:00', 'BUS020', 52, 52),
(9, '17:00:00', '01:00:00', 'BUS021', 52, 52),
(10, '08:15:00', '11:00:00', 'BUS022', 38, 38),
(10, '14:30:00', '17:15:00', 'BUS023', 38, 38),
(10, '19:45:00', '22:30:00', 'BUS024', 38, 38);

-- Insert sample bookings
INSERT INTO bookings (
    booking_reference, passenger_id, schedule_id, travel_date, number_of_seats, 
    seat_preference, total_amount, payment_method, payment_reference, status, special_requests
) VALUES
('BK' + UNIX_TIMESTAMP() + '001', 1, 1, '2025-01-15', 2, 'Window', 90.00, 'Credit Card', 'PAY123456', 'CONFIRMED', 'Extra legroom if possible'),
('BK' + UNIX_TIMESTAMP() + '002', 2, 4, '2025-01-16', 1, 'Aisle', 75.00, 'PayPal', 'PP789012', 'CONFIRMED', NULL),
('BK' + UNIX_TIMESTAMP() + '003', 3, 6, '2025-01-17', 1, 'Any', 55.00, 'Debit Card', 'DB345678', 'PENDING', 'Wheelchair accessible seat'),
('BK' + UNIX_TIMESTAMP() + '004', 4, 8, '2025-01-18', 3, 'Window', 120.00, 'Credit Card', 'CC901234', 'CONFIRMED', NULL),
('BK' + UNIX_TIMESTAMP() + '005', 5, 11, '2025-01-19', 2, 'Aisle', 70.00, 'Bank Transfer', 'BT567890', 'CONFIRMED', NULL),
('BK' + UNIX_TIMESTAMP() + '006', 6, 14, '2025-01-20', 1, 'Window', 50.00, 'Credit Card', 'CC234567', 'PENDING', NULL),
('BK' + UNIX_TIMESTAMP() + '007', 7, 16, '2025-01-21', 2, 'Any', 96.00, 'PayPal', 'PP678901', 'CONFIRMED', 'Extra legroom needed'),
('BK' + UNIX_TIMESTAMP() + '008', 8, 18, '2025-01-22', 1, 'Window', 60.00, 'Credit Card', 'CC345678', 'CONFIRMED', NULL),
('BK' + UNIX_TIMESTAMP() + '009', 9, 20, '2025-01-23', 4, 'Aisle', 340.00, 'Bank Transfer', 'BT789012', 'PENDING', 'Group booking'),
('BK' + UNIX_TIMESTAMP() + '010', 10, 22, '2025-01-24', 2, 'Window', 76.00, 'Credit Card', 'CC456789', 'CONFIRMED', NULL),
('BK' + UNIX_TIMESTAMP() + '011', 1, 3, '2024-12-20', 1, 'Window', 45.00, 'Credit Card', 'PAY987654', 'COMPLETED', NULL),
('BK' + UNIX_TIMESTAMP() + '012', 2, 7, '2024-12-15', 2, 'Aisle', 110.00, 'PayPal', 'PP123456', 'COMPLETED', NULL),
('BK' + UNIX_TIMESTAMP() + '013', 3, 2, '2024-12-25', 1, 'Any', 45.00, 'Cash', NULL, 'CANCELLED', 'Emergency cancellation'),
('BK' + UNIX_TIMESTAMP() + '014', 11, 5, '2025-01-25', 1, 'Aisle', 75.00, 'Credit Card', 'CC567890', 'CONFIRMED', NULL),
('BK' + UNIX_TIMESTAMP() + '015', 12, 9, '2025-01-26', 2, 'Window', 80.00, 'PayPal', 'PP234567', 'PENDING', 'Dietary restrictions noted');

-- Update available seats in schedules based on confirmed bookings
UPDATE schedules s SET available_seats = total_seats - (
    SELECT COALESCE(SUM(b.number_of_seats), 0) 
    FROM bookings b 
    WHERE b.schedule_id = s.id 
    AND b.status IN ('PENDING', 'CONFIRMED')
);