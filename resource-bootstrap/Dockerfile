FROM gradle:9.2.1-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :services:resource-service:resource-bootstrap:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/resource-service/resource-bootstrap/build/libs/*.jar app.jar
EXPOSE 8081
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
