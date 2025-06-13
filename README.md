# Caribe Vibes - Sistema de Gestión Turística

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![JWT](https://img.shields.io/badge/JWT-Security-red.svg)
![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-green.svg)

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Deployment](#deployment)
- [Monitoreo](#monitoreo)
- [Contribución](#contribución)

## 📖 Descripción

Caribe Vibes es un sistema completo de gestión turística desarrollado en Java Spring Boot que permite a los usuarios explorar y reservar experiencias turísticas en el Caribe. El sistema maneja destinos, hoteles, actividades, reservas y usuarios con una arquitectura robusta y escalable.

### 🌟 Características Principales

- **Autenticación y Autorización**: Sistema JWT con roles de usuario y administrador
- **Gestión de Destinos**: CRUD completo para destinos turísticos del Caribe
- **Gestión de Hoteles**: Administración de hoteles y tipos de habitaciones
- **Sistema de Reservas**: Booking completo con actividades opcionales
- **Generación de Vouchers**: PDFs automáticos para confirmaciones de reserva
- **Cache Inteligente**: Sistema de cache con Caffeine para optimización
- **API RESTful**: Endpoints completamente documentados con Swagger
- **Monitoreo**: Métricas con Actuator y Prometheus
- **Base de Datos**: Migraciones con Flyway
- **Testing**: Cobertura completa con JUnit 5

## 🛠️ Tecnologías

### Backend
- **Java 17**: Lenguaje de programación principal
- **Spring Boot 3.2.0**: Framework principal
- **Spring Security**: Autenticación y autorización
- **Spring Data JPA**: Persistencia de datos
- **JWT (JJWT)**: Tokens de autenticación
- **Flyway**: Migraciones de base de datos
- **Caffeine**: Sistema de cache en memoria
- **iText 7**: Generación de documentos PDF

### Base de Datos
- **MySQL 8.0**: Base de datos principal (producción)
- **PostgreSQL 15**: Alternativa de base de datos
- **H2**: Base de datos en memoria (testing)

### Documentación y Monitoreo
- **Swagger/OpenAPI 3**: Documentación de API
- **Actuator**: Endpoints de health y métricas
- **Micrometer**: Métricas de aplicación
- **Prometheus**: Recolección de métricas

### Testing
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking para unit tests
- **Spring Boot Test**: Testing de integración
- **TestContainers**: Testing con contenedores

## 🏗️ Arquitectura

El proyecto sigue una arquitectura por capas con separación clara de responsabilidades:

```
src/main/java/org/project/caribevibes/
├── config/              # Configuraciones (Security, CORS, Cache, etc.)
├── controller/          # Controladores REST por dominio
├── dto/                # Data Transfer Objects
│   ├── request/        # DTOs para requests
│   └── response/       # DTOs para responses
├── entity/             # Entidades JPA organizadas por módulo
│   ├── user/          # Usuario y roles
│   ├── destination/   # Destinos y actividades
│   ├── hotel/         # Hoteles y tipos de habitación
│   ├── booking/       # Reservas y actividades de reserva
│   └── contact/       # Mensajes de contacto
├── exception/         # Excepciones personalizadas
├── repository/        # Repositorios JPA por módulo
├── security/          # Configuración JWT y seguridad
├── service/           # Servicios de negocio por módulo
└── util/              # Utilidades y validaciones
```

### Módulos Principales

1. **Authentication & Authorization**
   - JWT token management
   - Role-based access control
   - User registration and login

2. **Destination Management**
   - Caribbean destinations
   - Activities and experiences
   - Location-based services

3. **Hotel Management**
   - Hotel catalog
   - Room types and pricing
   - Availability management

4. **Booking System**
   - Reservation creation and management
   - Activity booking
   - PDF voucher generation

5. **Contact Management**
   - Customer inquiries
   - Support messages

## 🚀 Instalación

### Prerrequisitos

- Java 17 o superior
- Maven 3.8+
- MySQL 8.0 o PostgreSQL 15
- Git

### Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/caribe-caribevibes.git
cd caribe-caribevibes
```

### Instalación de Dependencias

```bash
./mvnw clean install
```

## ⚙️ Configuración

### Variables de Entorno

Configurar las siguientes variables de entorno :

```bash
# Base de datos
DATABASE_URL=jdbc:mysql://localhost:3306/caribe_caribevibes
DATABASE_USERNAME_CARIBE_VIBES=tu_usuario
DATABASE_PASSWORD_CARIBE_VIBES=tu_password

# JWT
JWT_SECRET_CARIBE_VIBES=tu-super-secreto-jwt-para-produccion-muy-largo-y-seguro
JWT_EXPIRATION_CARIBE_VIBES=86400000

# Email (opcional)
SMTP_HOST=smtp.gmail.com
SMTP_USERNAME=noreply@caribevibes.com
SMTP_PASSWORD=tu_password_de_aplicacion

# Archivos
STORAGE_PATH=/var/caribe-caribevibes/uploads
MAX_FILE_SIZE=10MB

## Ejemplo paso a paso para configurar variables de entorno en Windows

1. Abre el menú de inicio y busca **"Editar las variables de entorno del sistema"**. Haz clic para abrir la ventana de Propiedades del sistema.
2. Haz clic en el botón **"Variables de entorno..."**.
3. En la sección **"Variables de usuario"** (o "Variables del sistema" si quieres que sean globales), haz clic en **"Nueva..."**.
4. Ingresa el **nombre** de la variable (por ejemplo, `DATABASE_URL`) y su **valor** (por ejemplo, `jdbc:mysql://localhost:3306/caribe_caribevibes`).
5. Repite el proceso para cada variable de entorno que necesites configurar.
6. Haz clic en **Aceptar** para guardar los cambios y cierra todas las ventanas.
7. Reinicia la terminal o tu equipo para que los cambios tengan efecto.

**Ejemplo rápido en terminal (CMD):**
```cmd
setx DATABASE_URL "jdbc:mysql://localhost:3306/caribe_caribevibes"
setx DATABASE_USERNAME_CARIBE_VIBES "tu_usuario"
setx DATABASE_PASSWORD_CARIBE_VIBES "tu_password"
setx JWT_SECRET_CARIBE_VIBES "tu-super-secreto-jwt-para-produccion-muy-largo-y-seguro"
setx JWT_EXPIRATION_CARIBE_VIBES "86400000"
setx SMTP_HOST "smtp.gmail.com"
setx SMTP_USERNAME "noreply@caribevibes.com"
setx SMTP_PASSWORD "tu_password_de_aplicacion"
setx STORAGE_PATH "C:\caribe-caribevibes\uploads"
setx MAX_FILE_SIZE "10MB"
```
> Nota: Si usas rutas en Windows, recuerda usar doble barra invertida (`\\`) o una sola (`\`) según corresponda.

```
### Base de Datos

#### MySQL
```sql
CREATE DATABASE caribe_caribevibes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'caribe_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON caribe_caribevibes.* TO 'caribe_user'@'localhost';
FLUSH PRIVILEGES;
```

#### PostgreSQL
```sql
CREATE DATABASE caribe_caribevibes;
CREATE USER caribe_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE caribe_caribevibes TO caribe_user;
```

### Perfiles de Configuración

- **development**: `application.yml` (H2 en memoria, logging debug)
- **test**: `application-test.yml` (H2 en memoria, configuración de testing)
- **production**: `application-prod.yml` (MySQL/PostgreSQL, configuración optimizada)

## 🎯 Uso

### Iniciar la Aplicación

```bash
# Desarrollo
./mvnw spring-boot:run

# Con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Construcción y ejecución
./mvnw clean package
java -jar target/caribe-vibes-1.0.0.jar
```

### Acceso a la Aplicación

- **API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator
- **Health Check**: http://localhost:8080/actuator/health

## 📚 API Endpoints

### Autenticación
```http
POST /api/auth/register    # Registro de usuario
POST /api/auth/login       # Login
POST /api/auth/validate    # Validar token
POST /api/auth/refresh     # Refrescar token
```

### Destinos
```http
GET    /api/destinations              # Listar destinos
GET    /api/destinations/{id}         # Obtener destino
GET    /api/destinations/{id}/hotels  # Hoteles del destino
GET    /api/destinations/{id}/activities # Actividades del destino
```

### Hoteles
```http
GET    /api/hotels                    # Listar hoteles
GET    /api/hotels/{id}               # Obtener hotel
GET    /api/hotels/{id}/rooms         # Tipos de habitación
GET    /api/hotels/search             # Buscar hoteles
```

### Reservas
```http
GET    /api/bookings                  # Listar reservas (admin)
GET    /api/bookings/my-bookings      # Mis reservas (user)
POST   /api/bookings                  # Crear reserva
GET    /api/bookings/{id}             # Obtener reserva
PUT    /api/bookings/{id}             # Actualizar reserva
GET    /api/bookings/{id}/voucher     # Descargar voucher PDF
```

### Contacto
```http
POST   /api/contact                   # Enviar mensaje
GET    /api/contact                   # Listar mensajes (admin)
GET    /api/contact/{id}              # Obtener mensaje (admin)
```

### Documentación Completa
Visita `/swagger-ui.html` para la documentación interactiva completa.

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Solo unit tests
./mvnw test -Dtest="*Test"

# Solo integration tests
./mvnw test -Dtest="*IntegrationTest"

# Con cobertura
./mvnw test jacoco:report
```

### Estructura de Tests

```
src/test/java/
├── config/
│   └── BaseIntegrationTest.java     # Clase base para tests
├── controller/
│   └── *ControllerIntegrationTest.java
├── service/
│   └── *ServiceTest.java
└── repository/
    └── *RepositoryTest.java
```

## 🚀 Deployment

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/caribe-vibes-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Construcción
docker build -t caribe-caribevibes .

# Ejecución
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod caribe-caribevibes
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:mysql://db:3306/caribe_caribevibes
    depends_on:
      - db
  
  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: caribe_caribevibes
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"
```

### Producción

1. **Variables de Entorno**: Configurar todas las variables requeridas
2. **Base de Datos**: Configurar instancia de producción
3. **SSL/TLS**: Configurar certificados HTTPS
4. **Reverse Proxy**: Nginx o Apache para balanceo de carga
5. **Monitoreo**: Prometheus + Grafana para métricas

## 📊 Monitoreo

### Métricas Disponibles

- **Health Checks**: `/actuator/health`
- **Métricas de Aplicación**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`
- **Info de Aplicación**: `/actuator/info`

### Métricas Personalizadas

- `caribe_caribevibes.bookings.created`: Contador de reservas
- `caribe_caribevibes.users.registered`: Contador de usuarios
- `caribe_caribevibes.auth.errors`: Errores de autenticación
- `caribe_caribevibes.hotel.search.duration`: Tiempo de búsquedas
- `caribe_caribevibes.pdf.generation.duration`: Tiempo de generación PDF

### Dashboards Sugeridos

- **Grafana**: Dashboards para monitoreo en tiempo real
- **Prometheus**: Alertas para métricas críticas
- **ELK Stack**: Análisis de logs

## 🔧 Configuración Avanzada

### Cache

El sistema incluye cache inteligente con Caffeine:

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterAccess=300s
```

### Rate Limiting

Configuración de límites de peticiones:

```yaml
app:
  security:
    rate-limit:
      enabled: true
      requests-per-minute: 100
```

### Profiles

- `dev`: Desarrollo local
- `test`: Testing automatizado
- `staging`: Entorno de pruebas
- `prod`: Producción

## 🤝 Contribución

### Flujo de Desarrollo

1. Fork del proyecto
2. Crear rama feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -am 'Agregar nueva funcionalidad'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

### Estándares de Código

- **Java**: Seguir convenciones de Oracle
- **Spring Boot**: Best practices de Spring
- **JSDoc**: Documentación en español
- **Testing**: Cobertura mínima 80%
- **Git**: Conventional Commits

### Code Review

- Tests unitarios obligatorios
- Documentación actualizada
- Performance evaluado
- Seguridad revisada

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👥 Equipo

- **Desarrollo Backend**: Equipo Caribe Vibes
- **Arquitectura**: Sistemas Caribe Vibes
- **DevOps**: Infraestructura Caribe Vibes

## 📞 Soporte

- **Email**: soporte@caribevibes.com
- **Documentación**: [Wiki del Proyecto](wiki-url)
- **Issues**: [GitHub Issues](issues-url)

---

**Caribe Vibes** - Experiencias Turísticas Excepcionales en el Caribe 🌴✨
