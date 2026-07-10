# Paso 1: Usar una imagen oficial de Maven con Java 17 para compilar
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiar el archivo de configuración de dependencias y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar de forma global saltando los tests
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen ligera de ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el archivo JAR
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]