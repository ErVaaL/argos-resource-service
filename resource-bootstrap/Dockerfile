FROM gradle:9.2.1-jdk21 AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle.kts build.gradle.kts ./

COPY resource-core/build.gradle.kts resource-core/build.gradle.kts
COPY resource-application/build.gradle.kts resource-application/build.gradle.kts
COPY resource-adapters/grpc/build.gradle.kts resource-adapters/grpc/build.gradle.kts
COPY resource-adapters/mongo/build.gradle.kts resource-adapters/mongo/build.gradle.kts
COPY resource-bootstrap/build.gradle.kts resource-bootstrap/build.gradle.kts

RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=secret,id=gpr_user \
    --mount=type=secret,id=gpr_token \
    GH_USER="$(cat /run/secrets/gpr_user)" \
    GH_TOKEN="$(cat /run/secrets/gpr_token)" \
    ./gradlew -q :resource-bootstrap:dependencies --no-daemon || true

COPY resource-core resource-core
COPY resource-application resource-application
COPY resource-adapters resource-adapters
COPY resource-bootstrap resource-bootstrap

RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=secret,id=gpr_user \
    --mount=type=secret,id=gpr_token \
    GH_USER="$(cat /run/secrets/gpr_user)" \
    GH_TOKEN="$(cat /run/secrets/gpr_token)" \
    ./gradlew :resource-bootstrap:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/resource-bootstrap/build/libs/*SNAPSHOT.jar /app/app.jar

EXPOSE 8081
EXPOSE 9091

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
