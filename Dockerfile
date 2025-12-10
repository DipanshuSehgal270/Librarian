# --- Stage 1: Build the Application ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# This command compiles your code inside Render
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# COPY --from=build tells Docker to take the JAR from the previous stage, not from GitHub
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]