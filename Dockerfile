FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY target/kassa-systeem-backend-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
