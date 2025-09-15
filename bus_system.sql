-- Create database
CREATE DATABASE IF NOT EXISTS bus_system;
USE bus_system;

-- Routes Table
CREATE TABLE IF NOT EXISTS routes (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      route_id VARCHAR(20) NOT NULL UNIQUE,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    stops TEXT,
    distance DOUBLE NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active'
    );

-- Schedules Table
CREATE TABLE IF NOT EXISTS schedules (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         schedule_id VARCHAR(20) NOT NULL UNIQUE,
    route_id BIGINT NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    schedule_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    remarks TEXT,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
    );

-- System Configuration Table
CREATE TABLE IF NOT EXISTS system_configs (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    last_updated DATETIME NOT NULL
    );

-- User and Role Management Tables
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_login DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
    );

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS login_history (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             login_time DATETIME NOT NULL,
                                             ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Backup & Restore Tables
CREATE TABLE IF NOT EXISTS backup_configs (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              name VARCHAR(100) NOT NULL UNIQUE,
    backup_type VARCHAR(20) NOT NULL,
    storage_location VARCHAR(255) NOT NULL,
    scheduled_time VARCHAR(100),
    retention_days INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_modified DATETIME NOT NULL,
    modified_by BIGINT,
    FOREIGN KEY (modified_by) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS backup_history (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              config_id BIGINT NOT NULL,
                                              start_time DATETIME NOT NULL,
                                              end_time DATETIME,
                                              status VARCHAR(20) NOT NULL,
    file_size_mb DOUBLE,
    file_path VARCHAR(255),
    error_message TEXT,
    initiated_by VARCHAR(20) NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (config_id) REFERENCES backup_configs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- Insert sample roles
INSERT INTO roles (name, description) VALUES
                                          ('ADMIN', 'System Administrator with full access'),
                                          ('OPERATIONS_MANAGER', 'Manages routes and schedules'),
                                          ('TICKETING_AGENT', 'Handles ticket booking and sales'),
                                          ('DRIVER', 'Bus driver with limited access'),
                                          ('IT_SUPPORT', 'Technical support staff');

-- Insert sample users (password is 'password123')
INSERT INTO users (username, password, full_name, email, active, locked, created_at, updated_at) VALUES
                                                                                                     ('admin', 'password123', 'System Administrator', 'admin@bussystem.com', TRUE, FALSE, '2025-07-15 08:30:00', '2025-07-15 08:30:00'),
                                                                                                     ('saviru', 'password123', 'Saviru Perera', 'saviru@bussystem.com', TRUE, FALSE, '2025-07-20 09:15:00', '2025-07-20 09:15:00'),
                                                                                                     ('operations1', 'password123', 'Operations Manager', 'operations@bussystem.com', TRUE, FALSE, '2025-07-22 10:45:00', '2025-07-22 10:45:00'),
                                                                                                     ('support1', 'password123', 'IT Support', 'support@bussystem.com', TRUE, FALSE, '2025-07-25 11:30:00', '2025-07-25 11:30:00'),
                                                                                                     ('driver1', 'password123', 'John Driver', 'john.driver@bussystem.com', TRUE, FALSE, '2025-07-28 14:15:00', '2025-07-28 14:15:00'),
                                                                                                     ('ticketing1', 'password123', 'Sarah Ticket', 'sarah.ticket@bussystem.com', TRUE, FALSE, '2025-08-01 09:00:00', '2025-08-01 09:00:00'),
                                                                                                     ('inactive1', 'password123', 'Inactive User', 'inactive@bussystem.com', FALSE, FALSE, '2025-08-05 10:30:00', '2025-08-05 10:30:00');

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1), -- admin has ADMIN role
                                              (2, 5), -- saviru has IT_SUPPORT role
                                              (3, 2), -- operations1 has OPERATIONS_MANAGER role
                                              (4, 5), -- support1 has IT_SUPPORT role
                                              (5, 4), -- driver1 has DRIVER role
                                              (6, 3), -- ticketing1 has TICKETING_AGENT role
                                              (2, 1); -- saviru also has ADMIN role

-- Insert sample login history
INSERT INTO login_history (user_id, login_time, ip_address, user_agent, status) VALUES
                                                                                    (2, '2025-08-12 08:45:23', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'SUCCESS'),
                                                                                    (2, '2025-08-12 13:15:42', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'SUCCESS'),
                                                                                    (3, '2025-08-12 09:30:15', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'SUCCESS'),
                                                                                    (1, '2025-08-12 10:00:05', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'SUCCESS'),
                                                                                    (4, '2025-08-12 11:15:30', '192.168.1.103', 'Mozilla/5.0 (X11; Linux x86_64)', 'SUCCESS'),
                                                                                    (5, '2025-08-12 07:45:00', '192.168.1.104', 'Mozilla/5.0 (Android 10)', 'SUCCESS'),
                                                                                    (6, '2025-08-12 08:15:45', '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'SUCCESS'),
                                                                                    (2, '2025-08-11 14:30:22', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'SUCCESS'),
                                                                                    (3, '2025-08-10 09:45:10', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'FAILED'),
                                                                                    (3, '2025-08-10 09:46:25', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'SUCCESS'),
                                                                                    (2, '2025-08-12 16:19:32', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'SUCCESS');

-- Insert sample routes
INSERT INTO routes (route_id, origin, destination, stops, distance, duration_minutes, status)
VALUES
    ('R001', 'Downtown', 'Airport', 'Mall, Hospital, University', 25.5, 45, 'active'),
    ('R002', 'North Terminal', 'South Terminal', 'Central Park, Library, Stadium', 18.2, 30, 'active'),
    ('R003', 'City Center', 'Beach Resort', 'Museum, Hotel Zone, Shopping Center', 32.7, 60, 'inactive'),
    ('R004', 'West Station', 'East Station', 'Market, Office Park, Conference Center', 15.3, 25, 'active'),
    ('R005', 'Suburban Area', 'Industrial Zone', 'Residential Complex, School, Factory', 22.8, 40, 'active'),
    ('R006', 'Train Station', 'Business District', 'Bank, Post Office, Restaurant Row', 12.5, 20, 'active'),
    ('R007', 'Mountain View', 'Lake Shore', 'Forest Trail, Camping Area, Viewpoint', 35.0, 65, 'seasonal'),
    ('R008', 'Old Town', 'New Development', 'Historical Center, Museum, Modern Mall', 10.2, 18, 'active');

-- Insert sample schedules
INSERT INTO schedules (schedule_id, route_id, departure_time, arrival_time, schedule_date, status, remarks)
VALUES
    ('SCH001', 1, '08:00:00', '08:45:00', '2025-08-12', 'active', 'Morning express service'),
    ('SCH002', 1, '12:00:00', '12:45:00', '2025-08-12', 'active', 'Afternoon service'),
    ('SCH003', 1, '18:00:00', '18:45:00', '2025-08-12', 'active', 'Evening service'),
    ('SCH004', 2, '09:00:00', '09:30:00', '2025-08-12', 'active', 'Regular service'),
    ('SCH005', 2, '14:00:00', '14:30:00', '2025-08-12', 'cancelled', 'Cancelled due to maintenance'),
    ('SCH006', 3, '10:00:00', '11:00:00', '2025-08-13', 'active', 'Weekend special service'),
    ('SCH007', 4, '07:30:00', '07:55:00', '2025-08-12', 'active', 'Early morning commuter service'),
    ('SCH008', 4, '17:30:00', '17:55:00', '2025-08-12', 'active', 'Evening commuter service'),
    ('SCH009', 5, '08:15:00', '08:55:00', '2025-08-12', 'active', 'Worker transport service'),
    ('SCH010', 6, '09:15:00', '09:35:00', '2025-08-12', 'active', 'Business express'),
    ('SCH011', 6, '13:15:00', '13:35:00', '2025-08-12', 'active', 'Lunch hour service'),
    ('SCH012', 7, '09:00:00', '10:05:00', '2025-08-16', 'active', 'Weekend recreation service'),
    ('SCH013', 1, '08:00:00', '08:45:00', '2025-08-13', 'active', 'Morning express service'),
    ('SCH014', 1, '12:00:00', '12:45:00', '2025-08-13', 'active', 'Afternoon service'),
    ('SCH015', 1, '18:00:00', '18:45:00', '2025-08-13', 'active', 'Evening service');

-- Insert sample system configurations
INSERT INTO system_configs (config_key, config_value, category, description, last_updated)
VALUES
    ('notification.email.template.booking', 'Dear {customer_name},\n\nYour booking for route {route_id} on {date} has been confirmed.\n\nThank you for choosing our service.', 'NOTIFICATION', 'Email template for booking confirmation', '2025-08-01 10:30:00'),
    ('system.timezone', 'UTC', 'SYSTEM', 'Default system timezone', '2025-08-01 10:35:00'),
    ('payment.currency', 'USD', 'PAYMENT', 'Default currency for payments', '2025-08-01 10:40:00'),
    ('system.maintenance_mode', 'false', 'SYSTEM', 'Toggle system maintenance mode', '2025-08-01 10:45:00'),
    ('notification.sms.enabled', 'true', 'NOTIFICATION', 'Enable SMS notifications', '2025-08-01 10:50:00'),
    ('system.version', '1.2.3', 'SYSTEM', 'Current system version', '2025-08-01 11:00:00'),
    ('payment.gateway.url', 'https://payment-gateway.example.com/api', 'PAYMENT', 'Payment gateway API endpoint', '2025-08-01 11:05:00'),
    ('notification.email.sender', 'noreply@bussystem.com', 'NOTIFICATION', 'Email sender address', '2025-08-01 11:10:00'),
    ('system.max_booking_days_advance', '30', 'SYSTEM', 'Maximum days in advance for booking', '2025-08-01 11:15:00'),
    ('system.session_timeout_minutes', '30', 'SYSTEM', 'User session timeout in minutes', '2025-08-01 11:20:00');

-- Insert sample backup configurations
INSERT INTO backup_configs (name, backup_type, storage_location, scheduled_time, retention_days, active, last_modified, modified_by)
VALUES
    ('Daily Database Backup', 'FULL', '/backup/database/daily', '0 0 2 * * ?', 14, TRUE, '2025-08-01 14:30:00', 2),
    ('Weekly System Backup', 'FULL', '/backup/system/weekly', '0 0 3 ? * SUN', 30, TRUE, '2025-08-01 14:40:00', 2),
    ('Monthly Archive', 'FULL', '/backup/archive/monthly', '0 0 4 1 * ?', 365, TRUE, '2025-08-01 14:50:00', 2),
    ('Differential Daily Backup', 'DIFFERENTIAL', '/backup/differential/daily', '0 0 12 * * ?', 7, TRUE, '2025-08-01 15:00:00', 2),
    ('Incremental Hourly Backup', 'INCREMENTAL', '/backup/incremental/hourly', '0 0 */4 * * ?', 3, FALSE, '2025-08-01 15:10:00', 2),
    ('Emergency Backup', 'FULL', '/backup/emergency', NULL, 10, TRUE, '2025-08-01 15:20:00', 1);

-- Insert sample backup history
INSERT INTO backup_history (config_id, start_time, end_time, status, file_size_mb, file_path, error_message, initiated_by, user_id)
VALUES
    (1, '2025-08-11 02:00:00', '2025-08-11 02:15:23', 'SUCCESS', 523.7, '/backup/database/daily/FULL_2025-08-11.backup', NULL, 'SCHEDULED', NULL),
    (1, '2025-08-10 02:00:00', '2025-08-10 02:14:45', 'SUCCESS', 518.2, '/backup/database/daily/FULL_2025-08-10.backup', NULL, 'SCHEDULED', NULL),
    (1, '2025-08-09 02:00:00', '2025-08-09 02:17:12', 'FAILED', NULL, '/backup/database/daily/FULL_2025-08-09.backup', 'Disk space error', 'SCHEDULED', NULL),
    (2, '2025-08-11 03:00:00', '2025-08-11 03:45:56', 'SUCCESS', 1245.8, '/backup/system/weekly/FULL_2025-08-11.backup', NULL, 'SCHEDULED', NULL),
    (3, '2025-08-01 04:00:00', '2025-08-01 04:30:17', 'SUCCESS', 2134.5, '/backup/archive/monthly/FULL_2025-08-01.backup', NULL, 'SCHEDULED', NULL),
    (1, '2025-08-12 14:25:00', '2025-08-12 14:40:12', 'SUCCESS', 530.3, '/backup/database/daily/FULL_2025-08-12.backup', NULL, 'MANUAL', 2),
    (4, '2025-08-12 12:00:00', '2025-08-12 12:10:35', 'SUCCESS', 127.8, '/backup/differential/daily/DIFFERENTIAL_2025-08-12.backup', NULL, 'SCHEDULED', NULL),
    (4, '2025-08-11 12:00:00', '2025-08-11 12:09:47', 'SUCCESS', 118.5, '/backup/differential/daily/DIFFERENTIAL_2025-08-11.backup', NULL, 'SCHEDULED', NULL),
    (5, '2025-08-12 08:00:00', '2025-08-12 08:05:12', 'SUCCESS', 45.3, '/backup/incremental/hourly/INCREMENTAL_2025-08-12-08.backup', NULL, 'SCHEDULED', NULL),
    (6, '2025-08-10 15:45:00', '2025-08-10 16:02:23', 'SUCCESS', 1568.7, '/backup/emergency/FULL_2025-08-10-15-45.backup', NULL, 'MANUAL', 1),
    (1, '2025-08-12 16:30:00', NULL, 'IN_PROGRESS', NULL, '/backup/database/daily/FULL_2025-08-12-16-30.backup', NULL, 'MANUAL', 2);


-- Add Complaints table to the database
CREATE TABLE IF NOT EXISTS complaints (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          reference_number VARCHAR(20) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    incident_date DATETIME NOT NULL,
    submission_date DATETIME NOT NULL,
    resolution_date DATETIME,
    resolution_notes TEXT,
    assigned_to VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    route_id VARCHAR(20),
    schedule_id VARCHAR(20)
    );

-- Insert sample complaint data
INSERT INTO complaints (reference_number, customer_name, contact_number, email, category, description,
                        incident_date, submission_date, resolution_date, resolution_notes,
                        assigned_to, status, priority, route_id, schedule_id)
VALUES
    ('CMP-20250810-A123', 'John Smith', '555-123-4567', 'john.smith@email.com', 'Delay',
     'Bus arrived 30 minutes late causing me to miss an important meeting.',
     '2025-08-10 08:30:00', '2025-08-10 14:20:00', NULL, NULL,
     NULL, 'PENDING', 'MEDIUM', 'R001', 'SCH001'),

    ('CMP-20250809-B456', 'Emma Johnson', '555-987-6543', 'emma.j@email.com', 'Staff Behavior',
     'Driver was rude and refused to wait when I was running to catch the bus.',
     '2025-08-09 17:15:00', '2025-08-09 18:30:00', '2025-08-11 10:45:00', 'Spoke with driver, issued formal warning and apology to customer.',
     'Sarah Wilson', 'RESOLVED', 'HIGH', 'R002', 'SCH004'),

    ('CMP-20250808-C789', 'Michael Brown', '555-456-7890', 'mbrown@email.com', 'Vehicle Condition',
     'The air conditioning was not working, making the ride extremely uncomfortable.',
     '2025-08-08 14:30:00', '2025-08-08 16:45:00', NULL, 'Maintenance team notified, scheduled repair for tomorrow.',
     'David Thompson', 'IN_PROGRESS', 'MEDIUM', 'R001', 'SCH002'),

    ('CMP-20250811-D012', 'Lisa Garcia', '555-234-5678', 'lisa.g@email.com', 'Safety Concern',
     'Bus driver was texting while driving and made several abrupt stops.',
     '2025-08-11 12:10:00', '2025-08-11 13:25:00', NULL, NULL,
     NULL, 'PENDING', 'URGENT', 'R003', 'SCH006'),

    ('CMP-20250807-E345', 'Robert Williams', '555-876-5432', 'rwilliams@email.com', 'Lost Item',
     'Left my laptop bag on the bus, black with company logo.',
     '2025-08-07 18:20:00', '2025-08-07 20:15:00', '2025-08-08 09:30:00', 'Item found and returned to customer at central office.',
     'Maria Rodriguez', 'CLOSED', 'HIGH', 'R002', NULL);



-- Add Feedback table to the database
CREATE TABLE IF NOT EXISTS feedback (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    contact_number VARCHAR(20),
    rating INT NOT NULL,
    comments TEXT,
    submission_date DATETIME NOT NULL,
    service_date DATETIME,
    route_id VARCHAR(20),
    schedule_id VARCHAR(20),
    feedback_type VARCHAR(20) NOT NULL DEFAULT 'GENERAL',
    internal_notes TEXT,
    tags VARCHAR(255),
    reviewed BOOLEAN NOT NULL DEFAULT FALSE,
    reviewed_by VARCHAR(100),
    review_date DATETIME
    );

-- Insert sample feedback data
INSERT INTO feedback (customer_name, email, contact_number, rating, comments,
                      submission_date, service_date, route_id, schedule_id,
                      feedback_type, internal_notes, tags, reviewed, reviewed_by, review_date)
VALUES
    ('James Wilson', 'jwilson@email.com', '555-111-2222', 5,
     'The driver was very helpful with my luggage and waited for me when I was running late.',
     '2025-08-10 16:30:00', '2025-08-10 15:00:00', 'R001', 'SCH001',
     'COMPLIMENT', 'Driver to be commended at next team meeting', 'service,staff,helpful',
     TRUE, 'Maria Lopez', '2025-08-11 09:15:00'),

    ('Sarah Thompson', 'sarah.t@email.com', '555-333-4444', 4,
     'Overall good service but the bus was a bit crowded.',
     '2025-08-09 19:45:00', '2025-08-09 18:30:00', 'R002', 'SCH004',
     'GENERAL', NULL, 'service,crowding',
     FALSE, NULL, NULL),

    ('David Chen', 'dchen@email.com', '555-555-6666', 3,
     'The bus was on time but it was not very clean inside. There was trash under the seats.',
     '2025-08-08 10:15:00', '2025-08-08 08:30:00', 'R001', 'SCH001',
     'GENERAL', 'Cleaning issue forwarded to maintenance team', 'cleanliness',
     TRUE, 'Robert Johnson', '2025-08-08 14:20:00'),

    ('Emily Rodriguez', 'emily.r@email.com', '555-777-8888', 5,
     'I would like to suggest adding a stop near the shopping mall. It would be very convenient for many passengers.',
     '2025-08-11 13:40:00', '2025-08-11 12:15:00', 'R003', NULL,
     'SUGGESTION', 'Good suggestion, added to route planning meeting agenda', 'route,suggestion',
     TRUE, 'Maria Lopez', '2025-08-11 15:30:00'),

    ('Michael Johnson', 'mjohnson@email.com', '555-999-0000', 2,
     'The bus was 20 minutes late and the driver did not apologize or explain the delay.',
     '2025-08-10 08:50:00', '2025-08-10 08:00:00', 'R002', 'SCH004',
     'COMPLAINT', NULL, 'delay,staff',
     FALSE, NULL, NULL);



-- Add Trip Log table to the database
CREATE TABLE IF NOT EXISTS trip_logs (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         driver_name VARCHAR(100) NOT NULL,
    driver_id VARCHAR(50),
    vehicle_number VARCHAR(20) NOT NULL,
    route_id VARCHAR(20) NOT NULL,
    schedule_id VARCHAR(20),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    passenger_count INT NOT NULL,
    issues_encountered TEXT,
    notes TEXT,
    fuel_consumed DOUBLE,
    mileage DOUBLE,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
    );

-- Insert sample trip log data
INSERT INTO trip_logs (driver_name, driver_id, vehicle_number, route_id, schedule_id,
                       start_time, end_time, passenger_count, issues_encountered, notes,
                       fuel_consumed, mileage, status, created_at, updated_at)
VALUES
    ('John Driver', 'D001', 'BUS-123', 'R001', 'SCH001',
     '2025-08-13 08:00:00', '2025-08-13 08:45:00', 32, NULL, 'Regular morning trip. Traffic was light.',
     12.5, 25.5, 'COMPLETED', '2025-08-13 08:46:00', '2025-08-13 08:46:00'),

    ('John Driver', 'D001', 'BUS-123', 'R001', 'SCH002',
     '2025-08-13 12:00:00', '2025-08-13 12:50:00', 28, 'Heavy traffic near the mall caused slight delay.', 'Had to take alternate route.',
     13.2, 25.5, 'DELAYED', '2025-08-13 12:51:00', '2025-08-13 12:51:00'),

    ('Sarah Smith', 'D002', 'BUS-456', 'R002', 'SCH004',
     '2025-08-13 09:00:00', '2025-08-13 09:32:00', 22, NULL, NULL,
     9.8, 18.2, 'COMPLETED', '2025-08-13 09:35:00', '2025-08-13 09:35:00'),

    ('Sarah Smith', 'D002', 'BUS-456', 'R002', 'SCH005',
     '2025-08-13 14:00:00', '2025-08-13 14:35:00', 35, 'Bus was overcrowded. Some passengers had to stand.', 'Need to consider larger buses for this route.',
     10.5, 18.2, 'COMPLETED', '2025-08-13 14:40:00', '2025-08-13 14:40:00'),

    ('Mike Johnson', 'D003', 'BUS-789', 'R003', 'SCH006',
     '2025-08-13 10:00:00', '2025-08-13 10:40:00', 0, 'Mechanical issue with the bus. Had to return to depot.', 'Maintenance team notified.',
     8.3, 15.0, 'CANCELED', '2025-08-13 10:45:00', '2025-08-13 10:45:00');




-- Add Vehicle Condition Reports table to the database
CREATE TABLE IF NOT EXISTS vehicle_condition_reports (
                                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                         vehicle_number VARCHAR(20) NOT NULL,
    driver_name VARCHAR(100) NOT NULL,
    driver_id VARCHAR(50),
    report_date DATETIME NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    odometer_reading INT,
    brakes_condition VARCHAR(20) NOT NULL,
    lights_condition VARCHAR(20) NOT NULL,
    tires_condition VARCHAR(20) NOT NULL,
    exterior_condition VARCHAR(20),
    interior_condition VARCHAR(20),
    fuel_level VARCHAR(20),
    details TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by VARCHAR(100),
    reviewed_date DATETIME,
    maintenance_notes TEXT,
    follow_up_action TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
    );

-- Insert sample vehicle condition report data
INSERT INTO vehicle_condition_reports (vehicle_number, driver_name, driver_id, report_date, report_type,
                                       odometer_reading, brakes_condition, lights_condition, tires_condition,
                                       exterior_condition, interior_condition, fuel_level, details, status,
                                       reviewed_by, reviewed_date, maintenance_notes, follow_up_action,
                                       created_at, updated_at)
VALUES
    ('BUS-123', 'John Driver', 'D001', '2025-08-18 07:30:00', 'PRE_TRIP',
     34560, 'GOOD', 'GOOD', 'GOOD', 'GOOD', 'GOOD', 'FULL', 'No issues found.', 'PENDING',
     NULL, NULL, NULL, NULL, '2025-08-18 07:35:00', '2025-08-18 07:35:00'),

    ('BUS-456', 'Sarah Smith', 'D002', '2025-08-18 08:15:00', 'PRE_TRIP',
     29845, 'FAIR', 'GOOD', 'NEEDS_ATTENTION', 'GOOD', 'GOOD', '3/4', 'Right rear tire shows significant wear pattern and may need replacement soon.', 'REVIEWED',
     'Mike Supervisor', '2025-08-18 09:00:00', 'Scheduled for tire replacement this evening.', 'Replace right rear tire.', '2025-08-18 08:20:00', '2025-08-18 09:00:00'),

    ('BUS-123', 'John Driver', 'D001', '2025-08-18 17:45:00', 'POST_TRIP',
     34630, 'GOOD', 'NEEDS_ATTENTION', 'GOOD', 'FAIR', 'FAIR', '1/4', 'Left turn signal not working properly. Interior needs cleaning.', 'PENDING',
     NULL, NULL, NULL, NULL, '2025-08-18 17:50:00', '2025-08-18 17:50:00'),

    ('BUS-789', 'Mike Johnson', 'D003', '2025-08-17 14:20:00', 'MAINTENANCE_REQUEST',
     52340, 'POOR', 'GOOD', 'GOOD', 'GOOD', 'GOOD', '1/2', 'Brakes feeling soft. Requires immediate attention.', 'IN_REPAIR',
     'David Mechanic', '2025-08-17 15:00:00', 'Brake fluid leak found. Replacing brake lines and bleeding system.', 'Complete brake system overhaul.', '2025-08-17 14:25:00', '2025-08-17 15:00:00'),

    ('BUS-456', 'Sarah Smith', 'D002', '2025-08-16 19:10:00', 'INCIDENT',
     29720, 'GOOD', 'GOOD', 'FAIR', 'POOR', 'GOOD', '1/2', 'Minor fender bender. Right side panel damaged. No injuries.', 'RESOLVED',
     'Mike Supervisor', '2025-08-16 20:00:00', 'Panel replaced and repainted.', 'No further action required.', '2025-08-16 19:15:00', '2025-08-17 12:30:00');