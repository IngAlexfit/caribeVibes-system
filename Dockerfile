# Dockerfile multi-stage para Caribe Vibes - Sistema de Gestión Turística

# ETAPA 1: Construcción del Frontend Angular
FROM node:20-alpine AS frontend-builder

# Crear directorio de trabajo para el frontend
WORKDIR /frontend

# Copiar archivos de configuración de Node.js primero (para aprovechar cache de Docker)
COPY src/main/resources/frontend/package*.json ./

# Instalar dependencias de Node.js (incluyendo devDependencies para ng build)
RUN npm ci

# Copiar código fuente del frontend
COPY src/main/resources/frontend/ ./

# Compilar el frontend para producción
RUN npm run build:prod

# ETAPA 2: Construcción del Backend Spring Boot
FROM eclipse-temurin:21-jdk-alpine AS backend-builder

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

# Copiar código fuente del backend
COPY src/ src/

# Copiar el frontend compilado al directorio de recursos estáticos de Spring Boot
COPY --from=frontend-builder /frontend/dist/caribe-vibes-frontend/ src/main/resources/static/

# Compilar y empaquetar la aplicación
RUN ./mvnw clean package -DskipTests -B

# ETAPA 3: Runtime
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

# Copiar JAR desde la etapa de construcción del backend
COPY --from=backend-builder /build/target/caribeVibes-*.jar app.jar

# Crear directorio para uploads temporales
RUN mkdir -p /tmp/uploads && \
    chown -R caribe:caribe /tmp/uploads /app

# Cambiar al usuario no privilegiado
USER caribe

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=fly
ENV JVM_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Exponer puerto
EXPOSE 8080

# Comando de inicio con optimizaciones JVM y configuración para Fly.io
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.jar"]