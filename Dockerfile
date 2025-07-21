FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY target/book-management-system-1.0.0.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
