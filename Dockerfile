# Dockerfile multi-stage para Caribe Vibes - Sistema de Gestión Turística

# ETAPA 1: Construcción
FROM eclipse-temurin:21-jdk-alpine AS builder

# Metadatos de la imagen
LABEL maintainer="Caribe Vibes Team <desarrollo@caribevibes.com>"
LABEL description="Sistema de gestión turística para el Caribe"
LABEL version="1.0.0"

# Instalar Maven
RUN apk add --no-cache maven

# Crear directorio de trabajo
WORKDIR /build

# Copiar archivos de configuración de Maven primero (para aprovechar cache de Docker)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Hacer mvnw ejecutable
RUN chmod +x mvnw

# Descargar dependencias (se cachea si no cambia pom.xml)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src/ src/

# Compilar y empaquetar la aplicación
RUN ./mvnw clean package -DskipTests -B

# ETAPA 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

# Crear usuario no privilegiado para seguridad
RUN addgroup -g 1001 -S caribe && \
    adduser -u 1001 -S caribe -G caribe

# Instalar dependencias necesarias
RUN apk add --no-cache \
    tzdata \
    curl \
    && rm -rf /var/cache/apk/*

# Configurar zona horaria
ENV TZ=America/Bogota
RUN cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Crear directorios de trabajo
WORKDIR /app

# Copiar JAR desde la etapa de construcción
COPY --from=builder /build/target/caribeVibes-*.jar app.jar

# Crear directorios para logs y uploads
RUN mkdir -p /var/log/caribe-vibes /var/caribe-vibes/uploads && \
    chown -R caribe:caribe /var/log/caribe-vibes /var/caribe-vibes /app

# Cambiar al usuario no privilegiado
USER caribe

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio con optimizaciones JVM
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]