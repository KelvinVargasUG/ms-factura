FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY gradlew build.gradle settings.gradle /app/
COPY gradle /app/gradle

COPY src /app/src

RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk upgrade --no-cache

RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8082

USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
