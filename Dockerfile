FROM gradle:7.6-jdk17 AS build

WORKDIR /app

COPY gradle /app/gradle
COPY build.gradle /app
COPY settings.gradle /app

COPY src /app/src

RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/ecommerce-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
