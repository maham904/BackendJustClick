# Use the official Maven image to build the application
FROM maven:3.8.6-amazoncorretto-21 as builder  # Updated: Use a Maven image with JDK 21
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Use the official Amazon Corretto image to run the application (JDK 21)
FROM amazoncorretto:21-alpine-jdk # Updated: Use a JDK 21 base image
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]