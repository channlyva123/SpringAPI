# Use OpenJDK 21
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Make gradlew executable
RUN chmod +x gradlew

# Build the app without running tests
RUN ./gradlew build --no-daemon -x test

# Set environment variable for Spring Boot port
ENV PORT=8080

# Expose port
EXPOSE 8080

# Start the app
CMD ["java", "-jar", "build/libs/coffee-shop-0.0.1-SNAPSHOT.jar"]