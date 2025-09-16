-- Operations Managers Table Migration
-- Add this to your existing database setup

USE bus_booking_db;

-- Create operations_managers table
CREATE TABLE operations_managers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    employee_id VARCHAR(20) UNIQUE,
    department VARCHAR(100),
    role ENUM('OPERATIONS_MANAGER', 'SENIOR_OPERATIONS_MANAGER', 'ADMIN') NOT NULL DEFAULT 'OPERATIONS_MANAGER',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_employee_id (employee_id),
    INDEX idx_role (role),
    INDEX idx_status (status),
    INDEX idx_active (is_active)
);

-- Insert sample operations managers
INSERT INTO operations_managers (
    username, email, password, first_name, last_name, phone_number,
    employee_id, department, role, status
) VALUES 
-- Admin user
('admin', 'admin@buscompany.com', 'admin123', 'System', 'Administrator', '1234567890',
 'EMP001', 'IT Administration', 'ADMIN', 'ACTIVE'),

-- Senior Operations Manager
('senior_ops', 'senior.ops@buscompany.com', 'senior123', 'Sarah', 'Johnson', '2345678901',
 'EMP002', 'Operations', 'SENIOR_OPERATIONS_MANAGER', 'ACTIVE'),

-- Regular Operations Managers
('ops_manager1', 'ops1@buscompany.com', 'ops123', 'Michael', 'Smith', '3456789012',
 'EMP003', 'Operations', 'OPERATIONS_MANAGER', 'ACTIVE'),

('ops_manager2', 'ops2@buscompany.com', 'ops123', 'Jennifer', 'Davis', '4567890123',
 'EMP004', 'Operations', 'OPERATIONS_MANAGER', 'ACTIVE'),

('ops_manager3', 'ops3@buscompany.com', 'ops123', 'Robert', 'Wilson', '5678901234',
 'EMP005', 'Route Planning', 'OPERATIONS_MANAGER', 'ACTIVE');

-- Insert some sample routes if they don't exist
INSERT IGNORE INTO routes (
    route_code, origin, destination, distance, duration, base_fare, status, stops
) VALUES 
('RT001', 'New York', 'Boston', 215.5, 240, 45.00, 'ACTIVE', 'Hartford, Springfield'),
('RT002', 'Los Angeles', 'San Francisco', 382.0, 360, 75.00, 'ACTIVE', 'Bakersfield, Fresno'),
('RT003', 'Chicago', 'Detroit', 283.2, 300, 55.00, 'ACTIVE', 'Kalamazoo, Battle Creek'),
('RT004', 'Miami', 'Orlando', 235.8, 270, 40.00, 'ACTIVE', 'Fort Lauderdale, West Palm Beach'),
('RT005', 'Seattle', 'Portland', 173.6, 180, 35.00, 'ACTIVE', 'Olympia, Centralia'),
('RT006', 'Dallas', 'Houston', 239.2, 240, 42.00, 'SUSPENDED', 'Huntsville, College Station'),
('RT007', 'Phoenix', 'Tucson', 116.4, 120, 28.00, 'INACTIVE', NULL),
('RT008', 'Denver', 'Colorado Springs', 112.5, 90, 25.00, 'ACTIVE', 'Castle Rock'),
('RT009', 'Atlanta', 'Charlotte', 244.3, 240, 48.00, 'ACTIVE', 'Greenville, Spartanburg'),
('RT010', 'Las Vegas', 'Reno', 448.7, 420, 68.00, 'ACTIVE', 'Carson City');

COMMIT;

-- Display created data
SELECT 'Operations Managers created:' as Info;
SELECT id, username, email, first_name, last_name, role, status, employee_id 
FROM operations_managers 
ORDER BY role DESC, first_name;

SELECT 'Sample Routes available:' as Info;
SELECT route_code, origin, destination, distance, duration, base_fare, status 
FROM routes 
ORDER BY route_code;