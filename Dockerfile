# Use Docker-in-Docker with docker-compose
FROM docker:24-dind

# Install docker-compose
RUN apk add --no-cache docker-compose

# Set working directory
WORKDIR /app

# Copy docker-compose file and all necessary files
COPY docker-compose.yml .
COPY pom.xml .
COPY src ./src
COPY sample_data.sql .

# Create a Dockerfile for the app service
COPY Dockerfile.app ./Dockerfile

# Expose port 8080 for the application
EXPOSE 8080

# Expose port 3306 for MySQL
EXPOSE 3306

# Create startup script
RUN echo '#!/bin/sh' > /start.sh && \
    echo 'dockerd &' >> /start.sh && \
    echo 'sleep 5' >> /start.sh && \
    echo 'docker-compose up --build' >> /start.sh && \
    chmod +x /start.sh

# Run the startup script
ENTRYPOINT ["/start.sh"]

