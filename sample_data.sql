CREATE DATABASE IF NOT EXISTS bus_booking_system;
USE bus_booking_system;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    phone_number VARCHAR(20),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );


INSERT INTO users (email, password, first_name, last_name, role, status, phone_number) VALUES
                                                                                           ('itsupport@bus.com', 'admin123', 'John', 'Doe', 'IT_SUPPORT', 'ACTIVE', '1234567890'),
                                                                                           ('operations@bus.com', 'ops123', 'Jane', 'Smith', 'OPERATION_MANAGER', 'ACTIVE', '1234567891'),
                                                                                           ('support@bus.com', 'sup123', 'Mike', 'Johnson', 'CUSTOMER_SUPPORT', 'ACTIVE', '1234567892'),
                                                                                           ('ticket1@bus.com', 'tick123', 'Sarah', 'Wilson', 'TICKETING_OFFICER', 'ACTIVE', '1234567893'),
                                                                                           ('ticket2@bus.com', 'tick123', 'David', 'Brown', 'TICKETING_OFFICER', 'ACTIVE', '1234567894'),
                                                                                           ('driver1@bus.com', 'drv123', 'Robert', 'Davis', 'DRIVER', 'ACTIVE', '1234567895'),
                                                                                           ('driver2@bus.com', 'drv123', 'Lisa', 'Miller', 'DRIVER', 'ACTIVE', '1234567896'),
                                                                                           ('driver3@bus.com', 'drv123', 'James', 'Garcia', 'DRIVER', 'ACTIVE', '1234567897'),
                                                                                           ('passenger1@bus.com', 'pass123', 'Alice', 'Cooper', 'PASSENGER', 'ACTIVE', '1234567898'),
                                                                                           ('passenger2@bus.com', 'pass123', 'Bob', 'Martin', 'PASSENGER', 'ACTIVE', '1234567899');
