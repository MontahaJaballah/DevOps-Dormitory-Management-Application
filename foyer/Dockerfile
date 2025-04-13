FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/4TWIN4-BugBusters-Foyer.jar .
EXPOSE 8089
CMD ["java", "-jar", "/app/4TWIN4-BugBusters-Foyer.jar"]