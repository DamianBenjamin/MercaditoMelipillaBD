# Paso 1: Usar una imagen oficial de Maven con Java 21 para compilar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos de configuración y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar saltando los tests para máxima velocidad
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen ligera de ejecución con Java 21 (Temurin es la oficial actual)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el archivo JAR
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]