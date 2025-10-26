# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with MySQL and Java
FROM ubuntu:22.04

# Install MySQL, JDK and other dependencies
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y \
    mysql-server \
    openjdk-21-jre \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/BookingSchedule-0.0.1-SNAPSHOT.jar app.jar

# Copy sample data SQL
COPY sample_data.sql /app/sample_data.sql

# Create startup script
RUN echo '#!/bin/bash\n\
set -e\n\
\n\
# Start MySQL\n\
service mysql start\n\
\n\
# Wait for MySQL to be ready\n\
until mysqladmin ping -h localhost --silent; do\n\
    echo "Waiting for MySQL..."\n\
    sleep 2\n\
done\n\
\n\
# Set root password and create database\n\
mysql -u root <<-EOSQL\n\
    ALTER USER "root"@"localhost" IDENTIFIED BY "12345678";\n\
    FLUSH PRIVILEGES;\n\
EOSQL\n\
\n\
# Run sample data SQL\n\
mysql -u root -p12345678 < /app/sample_data.sql\n\
\n\
# Start Spring Boot application\n\
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bus_booking_system\n\
export SPRING_DATASOURCE_USERNAME=root\n\
export SPRING_DATASOURCE_PASSWORD=12345678\n\
\n\
java -jar /app/app.jar\n\
' > /start.sh && chmod +x /start.sh

# Expose ports
EXPOSE 8080 3306

# Run the startup script
CMD ["/start.sh"]

