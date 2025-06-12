# Stage 1: Build the application
# Using a Maven-specific image simplifies the build process.
FROM maven:3.8.5-openjdk-17-slim AS build

# Set the working directory inside the container for the build
WORKDIR /app

# Copy your entire project into the build context
COPY . .

# Build the Spring Boot application using Maven
# -DskipTests: Skips running tests to speed up the build
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller runtime image
# Use a JRE-only image for a smaller footprint, ideal for production.
FROM openjdk:17-jre-slim

# Set the working directory for the final application
WORKDIR /app

# Expose the port your Spring Boot application runs on
# This helps Docker and hosting platforms (like Render) understand which port to expose.
EXPOSE 8080

# Copy the built JAR from the 'build' stage into the final image
# The JAR file is located in /app/target/ within the 'build' stage.
COPY --from=build /app/target/*.jar app.jar

# Define the command to run your Spring Boot application
# -Djava.security.egd=file:/dev/./urandom: Improves startup time for Spring Boot by using /dev/urandom
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
