-- Sample data for demonstration

-- Insert sample operations managers
INSERT INTO operations_managers (username, email, password, first_name, last_name, phone_number, employee_id, department, role, status, is_active) VALUES 
('admin', 'admin@buscompany.com', 'admin123', 'System', 'Administrator', '1234567890', 'EMP001', 'IT Administration', 'ADMIN', 'ACTIVE', true),
('senior_ops', 'senior.ops@buscompany.com', 'senior123', 'Sarah', 'Johnson', '2345678901', 'EMP002', 'Operations', 'SENIOR_OPERATIONS_MANAGER', 'ACTIVE', true),
('ops_manager1', 'ops1@buscompany.com', 'ops123', 'Michael', 'Smith', '3456789012', 'EMP003', 'Operations', 'OPERATIONS_MANAGER', 'ACTIVE', true);

-- Insert sample routes
INSERT INTO routes (route_code, origin, destination, distance, duration, base_fare, status, stops) VALUES 
('RT001', 'New York', 'Boston', 215.5, 240, 45.00, 'ACTIVE', 'Hartford, Springfield'),
('RT002', 'Los Angeles', 'San Francisco', 382.0, 360, 75.00, 'ACTIVE', 'Bakersfield, Fresno'),
('RT003', 'Chicago', 'Detroit', 283.2, 300, 55.00, 'ACTIVE', 'Kalamazoo, Battle Creek'),
('RT004', 'Miami', 'Orlando', 235.8, 270, 40.00, 'ACTIVE', 'Fort Lauderdale, West Palm Beach'),
('RT005', 'Seattle', 'Portland', 173.6, 180, 35.00, 'ACTIVE', 'Olympia, Centralia'),
('RT006', 'Dallas', 'Houston', 239.2, 240, 42.00, 'SUSPENDED', 'Huntsville, College Station'),
('RT007', 'Phoenix', 'Tucson', 116.4, 120, 28.00, 'INACTIVE', NULL),
('RT008', 'Denver', 'Colorado Springs', 112.5, 90, 25.00, 'ACTIVE', 'Castle Rock');

-- Insert sample passengers
INSERT INTO passengers (username, email, password, first_name, last_name, phone_number, date_of_birth, gender, emergency_contact, emergency_phone, seat_preference, frequent_destinations, accessibility_needs, meal_preference, payment_methods, is_active) VALUES 
('john_doe', 'john.doe@email.com', 'password123', 'John', 'Doe', '1234567890', '1990-05-15', 'MALE', 'Jane Doe', '0987654321', 'Window', 'New York, Boston, Philadelphia', NULL, 'Non-Vegetarian', 'Visa *1234, PayPal john@email.com', true),
('jane_smith', 'jane.smith@email.com', 'securepass', 'Jane', 'Smith', '2345678901', '1985-08-22', 'FEMALE', 'John Smith', '1098765432', 'Aisle', 'Los Angeles, San Francisco, San Diego', NULL, 'Vegetarian', 'MasterCard *5678', true);