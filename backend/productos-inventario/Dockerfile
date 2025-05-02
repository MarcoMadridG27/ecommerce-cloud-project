FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/productos-inventario-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY ./data /app/data
CMD ["java", "-cp", "app.jar", "main.java.com.ejemplo.app"]
