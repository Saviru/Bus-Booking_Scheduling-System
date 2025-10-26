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

# Copy sample data SQL and startup script
COPY sample_data.sql /app/sample_data.sql
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Expose ports
EXPOSE 8080 3306

# Run the startup script
CMD ["/bin/bash", "/start.sh"]

