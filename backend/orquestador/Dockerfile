FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/orquestador-0.0.1-SNAPSHOT.jar /app/orquestador.jar

EXPOSE 8081

CMD ["java", "-jar", "orquestador.jar"]
