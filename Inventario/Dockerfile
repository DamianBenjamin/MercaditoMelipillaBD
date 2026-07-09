# Paso 1: Construcción del archivo JAR usando Maven con Java 17
FROM maven:3.8.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecución de la aplicación con una imagen ligera de Java 17
FROM openjdk:21-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]