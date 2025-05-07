FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /app/target/orquestador-0.0.1-SNAPSHOT.jar /app/orquestador.jar
EXPOSE 8081
CMD ["java", "-jar", "orquestador.jar"]