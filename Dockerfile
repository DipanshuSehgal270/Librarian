FROM openjdk:17-jdk-slim

EXPOSE 8443

COPY target/book-management-system.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
