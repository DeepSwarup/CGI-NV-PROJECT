# --- Stage 1: Build the application with Maven ---
# Use an official Maven image that includes the Java JDK to compile our code
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy just the pom.xml first to leverage Docker layer caching
COPY bankApp/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# CORRECTED: Copy the entire local 'src' directory into a 'src' directory inside the container
# This preserves the correct Maven project structure (e.g., /app/src/main/java)
COPY bankApp/src ./src

# Package the application, skipping the tests
RUN mvn clean package -DskipTests


# --- Stage 2: Create the final, lightweight runtime image ---
# Use a slim OpenJDK image which is much smaller than the Maven image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled .jar file from the 'build' stage
COPY --from=build /app/target/bankApp-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port that the application runs on
EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

