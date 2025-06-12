# Dockerfile para Caribe Vibes - Sistema de Gestión Turística
# Imagen base: OpenJDK 17 Alpine (optimizada)
FROM openjdk:17-jdk-alpine

# Metadatos de la imagen
LABEL maintainer="Caribe Vibes Team <desarrollo@caribedreams.com>"
LABEL description="Sistema de gestión turística para el Caribe"
LABEL version="1.0.0"

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

# Copiar archivo JAR (construido previamente)
COPY target/caribe-vibes-1.0.0.jar app.jar

# Crear directorios para logs y uploads
RUN mkdir -p /var/log/caribe-dreams /var/caribe-dreams/uploads && \
    chown -R caribe:caribe /var/log/caribe-dreams /var/caribe-dreams /app

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
