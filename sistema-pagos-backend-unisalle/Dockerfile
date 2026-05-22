# ===== Etapa 1: Compilación =====
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

# Copiar Maven Wrapper y archivos de configuración
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias (capa cacheada)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar
COPY src src
RUN ./mvnw package -DskipTests -B

# ===== Etapa 2: Ejecución =====
FROM eclipse-temurin:23-jre
WORKDIR /app

# Copiar JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto del servicio
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
