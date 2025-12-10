# Start with OpenJDK 17 slim image
FROM eclipse-temurin:17-jdk-alpine
# Set working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/book-management-system-1.0.0.jar app.jar

# Expose port your app listens on
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
