# Usa imagen base oficial de OpenJDK
FROM eclipse-temurin:21-jdk-jammy

# Define carpeta de trabajo en contenedor
WORKDIR /app

# Copia el archivo JAR generado en el build local
COPY target/vetcare_back-0.0.1-SNAPSHOT.jar app.jar

# Expone puerto 8080 (o el que uses)
EXPOSE 8080

# Comando para ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
