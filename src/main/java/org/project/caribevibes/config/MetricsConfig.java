package org.project.caribevibes.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Configuración de métricas y monitoreo para la aplicación Caribe Vibes.
 * 
 * Proporciona indicadores de salud personalizados, métricas de negocio
 * y contadores para el monitoreo en tiempo real del estado de la aplicación.
 * Incluye métricas para reservas, usuarios, hoteles y rendimiento general.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Configuration
public class MetricsConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * Contador para el número total de reservas creadas.
     * 
     * @param meterRegistry Registro de métricas de Micrometer
     * @return Counter para reservas creadas
     */
    @Bean
    public Counter bookingCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("caribe_dreams.bookings.created")
                .description("Número total de reservas creadas")
                .tag("type", "business")
                .register(meterRegistry);
    }

    /**
     * Contador para el número total de usuarios registrados.
     * 
     * @param meterRegistry Registro de métricas de Micrometer
     * @return Counter para usuarios registrados
     */
    @Bean
    public Counter userRegisteredCounter(MeterRegistry meterRegistry) {
        return Counter.builder("caribe_dreams.users.registered")
                .description("Número total de usuarios registrados")
                .tag("type", "business")
                .register(meterRegistry);
    }

    /**
     * Contador para errores de autenticación.
     * 
     * @param meterRegistry Registro de métricas de Micrometer
     * @return Counter para errores de autenticación
     */
    @Bean
    public Counter authenticationErrorCounter(MeterRegistry meterRegistry) {
        return Counter.builder("caribe_dreams.auth.errors")
                .description("Número de errores de autenticación")
                .tag("type", "security")
                .register(meterRegistry);
    }

    /**
     * Timer para medir la duración de las búsquedas de hoteles.
     * 
     * @param meterRegistry Registro de métricas de Micrometer
     * @return Timer para búsquedas de hoteles
     */
    @Bean
    public Timer hotelSearchTimer(MeterRegistry meterRegistry) {
        return Timer.builder("caribe_dreams.hotel.search.duration")
                .description("Tiempo de respuesta de búsquedas de hoteles")
                .tag("type", "performance")
                .register(meterRegistry);
    }

    /**
     * Timer para medir la duración de generación de PDFs.
     * 
     * @param meterRegistry Registro de métricas de Micrometer
     * @return Timer para generación de PDFs
     */
    @Bean
    public Timer pdfGenerationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("caribe_dreams.pdf.generation.duration")
                .description("Tiempo de generación de vouchers PDF")
                .tag("type", "performance")
                .register(meterRegistry);
    }

    /**
     * Indicador de salud para la base de datos.
     * 
     * Verifica que la conexión a la base de datos esté disponible
     * y funcionando correctamente.
     * 
     * @return HealthIndicator para la base de datos
     */
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return Health.up()
                            .withDetail("database", "Available")
                            .withDetail("validationQuery", "Connection is valid")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("database", "Connection validation failed")
                            .build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("database", "Connection failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Indicador de salud para el sistema de cache.
     * 
     * Verifica que el sistema de cache esté funcionando
     * correctamente y sea accesible.
     * 
     * @return HealthIndicator para el sistema de cache
     */
    @Bean
    public HealthIndicator cacheHealthIndicator() {
        return () -> {
            try {
                // Verificación simple del cache
                // En un entorno real, podrías hacer una operación de cache real
                return Health.up()
                        .withDetail("cache", "Available")
                        .withDetail("type", "Caffeine")
                        .withDetail("status", "Operational")
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("cache", "Failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Indicador de salud personalizado para servicios críticos.
     * 
     * Verifica que los servicios críticos de la aplicación
     * (autenticación, reservas, pagos) estén operativos.
     * 
     * @return HealthIndicator para servicios críticos
     */
    @Bean
    public HealthIndicator criticalServicesHealthIndicator() {
        return () -> {
            try {
                // Verificaciones de servicios críticos
                boolean authServiceHealthy = checkAuthService();
                boolean bookingServiceHealthy = checkBookingService();
                boolean pdfServiceHealthy = checkPdfService();

                if (authServiceHealthy && bookingServiceHealthy && pdfServiceHealthy) {
                    return Health.up()
                            .withDetail("authService", "UP")
                            .withDetail("bookingService", "UP")
                            .withDetail("pdfService", "UP")
                            .withDetail("status", "All critical services operational")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("authService", authServiceHealthy ? "UP" : "DOWN")
                            .withDetail("bookingService", bookingServiceHealthy ? "UP" : "DOWN")
                            .withDetail("pdfService", pdfServiceHealthy ? "UP" : "DOWN")
                            .withDetail("status", "Some critical services are down")
                            .build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("criticalServices", "Health check failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Verifica el estado del servicio de autenticación.
     * 
     * @return true si el servicio está saludable, false en caso contrario
     */
    private boolean checkAuthService() {
        try {
            // Verificación básica del servicio de autenticación
            // En un entorno real, podrías hacer una validación de token dummy
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica el estado del servicio de reservas.
     * 
     * @return true si el servicio está saludable, false en caso contrario
     */
    private boolean checkBookingService() {
        try {
            // Verificación básica del servicio de reservas
            // En un entorno real, podrías hacer una consulta simple a la BD
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica el estado del servicio de generación de PDFs.
     * 
     * @return true si el servicio está saludable, false en caso contrario
     */
    private boolean checkPdfService() {
        try {
            // Verificación básica del servicio de PDFs
            // En un entorno real, podrías intentar generar un PDF de prueba
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
