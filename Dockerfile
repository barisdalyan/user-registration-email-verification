FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/user-registration-email-verification-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
