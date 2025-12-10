# Use official Java runtime as base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from Maven build
COPY target/eventsProject-1.0.0-SNAPSHOT.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8089

# Set environment variables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/events_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
