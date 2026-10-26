FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

COPY gradle/ gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar

USER 1000

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]