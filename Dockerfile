FROM maven:3.8.8-amazoncorretto-21 AS builder
# Use Maven 3.8.8

COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine-jdk
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]