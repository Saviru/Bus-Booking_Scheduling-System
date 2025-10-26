# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B && \
    rm -rf /root/.m2/repository/org/apache/maven

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
# Initialize MySQL if not initialized\n\
if [ ! -d "/var/lib/mysql/mysql" ]; then\n\
    mysql_install_db --user=mysql --datadir=/var/lib/mysql > /dev/null\n\
fi\n\
\n\
# Start MySQL with networking\n\
mysqld --user=mysql --bind-address=127.0.0.1 &\n\
MYSQL_PID=$!\n\
\n\
# Wait for MySQL to be ready\n\
echo "Waiting for MySQL to start..."\n\
for i in $(seq 30 -1 0); do\n\
    if mysqladmin ping -h 127.0.0.1 --silent 2>/dev/null; then\n\
        echo "MySQL is ready!"\n\
        break\n\
    fi\n\
    sleep 1\n\
done\n\
\n\
if [ "$i" = "0" ]; then\n\
    echo "MySQL failed to start"\n\
    exit 1\n\
fi\n\
\n\
# Set root password and run sample data\n\
mysql -h 127.0.0.1 -u root <<-EOSQL\n\
    ALTER USER "root"@"localhost" IDENTIFIED BY "12345678";\n\
    CREATE USER IF NOT EXISTS "root"@"127.0.0.1" IDENTIFIED BY "12345678";\n\
    GRANT ALL PRIVILEGES ON *.* TO "root"@"127.0.0.1" WITH GRANT OPTION;\n\
    FLUSH PRIVILEGES;\n\
EOSQL\n\
\n\
# Run sample data SQL\n\
mysql -h 127.0.0.1 -u root -p12345678 < /app/sample_data.sql\n\
\n\
echo "Database initialized successfully!"\n\
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

