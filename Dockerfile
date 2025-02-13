FROM maven:3.8.8-amazoncorretto-21 AS builder

# Copy the source code into the container
COPY . /app

# Set the working directory
WORKDIR /app

# Clean and build the project, skipping tests
RUN mvn clean package -DskipTests

# *** KEY FIX: Use the actual JAR file name from your Maven build ***
# The following command will show you the actual JAR file name in the logs
RUN ls /app/target

# Use the Amazon Corretto JDK 21 with Alpine for the runtime stage
FROM amazoncorretto:21-alpine-jdk

# Copy the .jar file from the builder stage (adjust path if necessary)
# *** REPLACE library_management-0.0.1-SNAPSHOT.jar with the actual JAR file name if different ***
COPY --from=builder /app/target/library_management-0.0.1-SNAPSHOT.jar /app/library_management.jar

# Verify the .jar file is in the correct location (for debugging)
RUN ls -l /app/library_management.jar

# Expose port 8080 for the application
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app/library_management.jar"]