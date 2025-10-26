#!/bin/bash
set -e

# Create necessary directories
mkdir -p /var/lib/mysql /var/run/mysqld /tmp
chown -R mysql:mysql /var/lib/mysql /var/run/mysqld
chmod 777 /tmp

# Initialize MariaDB if not initialized
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo "Initializing MariaDB database..."
    mariadb-install-db --user=mysql --datadir=/var/lib/mysql --skip-test-db 2>&1
    if [ $? -ne 0 ]; then
        echo "Failed to initialize MariaDB"
        exit 1
    fi
    echo "MariaDB initialized successfully"
fi

# Start MariaDB
echo "Starting MariaDB server..."
mariadbd --user=mysql --datadir=/var/lib/mysql --bind-address=0.0.0.0 --port=3306 2>&1 &
MYSQL_PID=$!
sleep 2

# Check if MariaDB process is running
if ! ps -p $MYSQL_PID > /dev/null; then
    echo "ERROR: MariaDB process died immediately after starting"
    exit 1
fi

# Wait for MariaDB to be ready
echo "Waiting for MariaDB to accept connections..."
RETRIES=60
until mariadb-admin ping --silent 2>/dev/null || [ $RETRIES -eq 0 ]; do
    echo "Waiting for MariaDB... ($RETRIES attempts remaining)"
    RETRIES=$((RETRIES-1))
    sleep 2
done

if [ $RETRIES -eq 0 ]; then
    echo "ERROR: MariaDB failed to start within timeout period"
    echo "Checking MariaDB process status:"
    ps aux | grep maria || true
    exit 1
fi

echo "MariaDB is ready and accepting connections!"

# Set root password and create user
echo "Configuring MariaDB users and permissions..."
mariadb -u root <<EOSQL
    SET PASSWORD FOR 'root'@'localhost' = PASSWORD('12345678');
    CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '12345678';
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
    FLUSH PRIVILEGES;
EOSQL

# Run sample data SQL
echo "Loading sample data into database..."
mariadb -u root -p12345678 < /app/sample_data.sql

echo "Database setup completed successfully!"
echo "Starting Spring Boot application..."

# Start Spring Boot with memory constraints
exec java -Xms64m -Xmx200m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m \
    -Dspring.datasource.url=jdbc:mysql://localhost:3306/bus_booking_system \
    -Dspring.datasource.username=root \
    -Dspring.datasource.password=12345678 \
    -Dspring.jpa.hibernate.ddl-auto=none \
    -Dlogging.level.root=WARN \
    -jar /app/app.jar
