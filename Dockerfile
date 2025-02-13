# Use the official Maven image to build the application
FROM maven:3.8.6-eclipse-temurin-17 as builder
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Use the official OpenJDK image to run the application
FROM eclipse-temurin:17-jdk
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
