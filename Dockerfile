# Use Maven 3.8.8 with Amazon Corretto JDK 21 for the build stage
FROM maven:3.8.8-amazoncorretto-21 AS builder

# Copy the source code into the container
COPY . /app

# Set the working directory
WORKDIR /app

# Clean and build the project, skipping tests
RUN mvn clean package -DskipTests

# Use the Amazon Corretto JDK 21 with Alpine for the runtime stage
FROM amazoncorretto:21-alpine-jdk

# Copy the jar file from the builder stage
# Make sure to reference the correct .jar file from the target folder
COPY --from=builder /app/target/*.jar /app/library_management.jar

# Expose port 8080 for the application
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app/library_management.jar"]
