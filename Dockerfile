# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins -B || true

COPY src ./src
RUN mvn clean package -DskipTests && \
    rm -rf /root/.m2

# Stage 2: Minimal runtime with MySQL and Java
FROM alpine:3.19

# Install minimal MySQL, JDK and dependencies
RUN apk add --no-cache \
    mariadb mariadb-client \
    openjdk21-jre-headless \
    bash \
    && rm -rf /var/cache/apk/* \
    && mkdir -p /run/mysqld \
    && chown mysql:mysql /run/mysqld

# Configure MySQL for low memory usage
RUN mkdir -p /etc/mysql/conf.d && \
    echo '[mysqld]\n\
skip-name-resolve\n\
innodb_buffer_pool_size=32M\n\
innodb_log_buffer_size=4M\n\
query_cache_size=0\n\
query_cache_type=0\n\
key_buffer_size=8M\n\
thread_stack=256K\n\
max_connections=20\n\
table_open_cache=32\n\
sort_buffer_size=256K\n\
read_buffer_size=256K\n\
read_rnd_buffer_size=256K\n\
max_heap_table_size=8M\n\
tmp_table_size=8M\n\
performance_schema=OFF\n\
' > /etc/mysql/conf.d/low-memory.cnf

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/BookingSchedule-0.0.1-SNAPSHOT.jar app.jar

# Copy sample data SQL
COPY sample_data.sql /app/sample_data.sql

# Create startup script
RUN printf '#!/bin/bash\n\
set -e\n\
\n\
# Create necessary directories\n\
mkdir -p /var/lib/mysql /var/run/mysqld /tmp\n\
chown -R mysql:mysql /var/lib/mysql /var/run/mysqld\n\
chmod 777 /tmp\n\
\n\
# Initialize MariaDB if not initialized\n\
if [ ! -d "/var/lib/mysql/mysql" ]; then\n\
    echo "Initializing MariaDB database..."\n\
    mariadb-install-db --user=mysql --datadir=/var/lib/mysql --skip-test-db 2>&1\n\
    if [ $? -ne 0 ]; then\n\
        echo "Failed to initialize MariaDB"\n\
        exit 1\n\
    fi\n\
    echo "MariaDB initialized successfully"\n\
fi\n\
\n\
# Start MariaDB in safe mode for initialization\n\
echo "Starting MariaDB server..."\n\
mariadbd --user=mysql --datadir=/var/lib/mysql --bind-address=0.0.0.0 --port=3306 2>&1 &\n\
MYSQL_PID=$!\n\
sleep 2\n\
\n\
# Check if MariaDB process is running\n\
if ! ps -p $MYSQL_PID > /dev/null; then\n\
    echo "ERROR: MariaDB process died immediately after starting"\n\
    exit 1\n\
fi\n\
\n\
# Wait for MariaDB to be ready\n\
echo "Waiting for MariaDB to accept connections..."\n\
RETRIES=60\n\
until mariadb-admin ping --silent 2>/dev/null || [ $RETRIES -eq 0 ]; do\n\
    echo "Waiting for MariaDB... ($RETRIES attempts remaining)"\n\
    RETRIES=$((RETRIES-1))\n\
    sleep 2\n\
done\n\
\n\
if [ $RETRIES -eq 0 ]; then\n\
    echo "ERROR: MariaDB failed to start within timeout period"\n\
    echo "Checking MariaDB process status:"\n\
    ps aux | grep maria || true\n\
    exit 1\n\
fi\n\
\n\
echo "MariaDB is ready and accepting connections!"\n\
\n\
# Set root password and create user\n\
echo "Configuring MariaDB users and permissions..."\n\
mariadb -u root <<-EOSQL 2>&1\n\
    SET PASSWORD FOR "root"@"localhost" = PASSWORD("12345678");\n\
    CREATE USER IF NOT EXISTS "root"@"%" IDENTIFIED BY "12345678";\n\
    GRANT ALL PRIVILEGES ON *.* TO "root"@"%" WITH GRANT OPTION;\n\
    FLUSH PRIVILEGES;\n\
EOSQL\n\
\n\
# Run sample data SQL\n\
echo "Loading sample data into database..."\n\
mariadb -u root -p12345678 < /app/sample_data.sql 2>&1\n\
\n\
echo "Database setup completed successfully!"\n\
echo "Starting Spring Boot application..."\n\
\n\
# Start Spring Boot with memory constraints\n\
exec java -Xms64m -Xmx200m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m \\\n\
    -Dspring.datasource.url=jdbc:mysql://localhost:3306/bus_booking_system \\\n\
    -Dspring.datasource.username=root \\\n\
    -Dspring.datasource.password=12345678 \\\n\
    -Dspring.jpa.hibernate.ddl-auto=none \\\n\
    -Dlogging.level.root=WARN \\\n\
    -jar /app/app.jar\n' > /start.sh && chmod +x /start.sh

# Expose ports
EXPOSE 8080 3306

# Run the startup script
CMD ["/bin/bash", "/start.sh"]

