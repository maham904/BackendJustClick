FROM maven:3.8.8-amazoncorretto-21 AS builder

COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Verify the .war file is created (debugging)
RUN ls /app/target

FROM amazoncorretto:21-alpine-jdk
 # Or tomcat:9.0-jre21-alpine if using Tomcat

# Copy the .war file
COPY --from=builder /app/target/library_management-0.0.1-SNAPSHOT.war /app/library_management.war


# Verify the .war file is copied (debugging)
RUN ls -l /app/library_management.war

EXPOSE 8080

# For a WAR file, use the appropriate command to deploy to Tomcat (or other servlet container)
# If using embedded Tomcat:
# ENTRYPOINT ["java", "-jar", "/app/library_management.war"]

# If using a separate Tomcat container, you'd copy the WAR to Tomcat's webapps directory
# and use Tomcat's startup script.
# Example (if using a Tomcat image):
# COPY --from=builder /app/target/library_management-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/
# ENTRYPOINT ["catalina.sh", "run"]

# Or if embedded tomcat
ENTRYPOINT ["java", "-jar", "/app/library_management.war"]