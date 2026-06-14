FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/messenger-project-0.0.1-SNAPSHOT.jar app.jar

USER 1000

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]