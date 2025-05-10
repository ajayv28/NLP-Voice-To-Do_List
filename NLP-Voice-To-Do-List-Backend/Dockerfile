# Stage 1: Build the JAR
FROM maven:3.9.4-eclipse-temurin-21 AS builder
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/NLP-Voice-To-Do-List-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]