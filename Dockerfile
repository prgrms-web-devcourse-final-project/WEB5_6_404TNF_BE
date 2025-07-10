FROM gradle:8.14.3-jdk21 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre
COPY --from=build /app/build/libs/*jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
