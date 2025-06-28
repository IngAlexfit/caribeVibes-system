package org.project.caribevibes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API.
 * 
 * Esta configuración define la documentación automática de todos los
 * endpoints de la API REST, incluyendo esquemas de autenticación JWT
 * y información detallada de cada operación.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configuración principal de OpenAPI.
     * 
     * @return Configuración OpenAPI completa con información de la API,
     *         esquemas de seguridad y servidores disponibles
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .components(securityComponents())
                .addSecurityItem(securityRequirement());
    }

    /**
     * Información básica de la API.
     * 
     * @return Objeto Info con metadatos de la API
     */
    private Info apiInfo() {
        return new Info()
                .title("Caribe Vibes API")
                .description("API REST para el sistema de gestión turística Caribe Vibes. " +
                           "Esta API permite gestionar destinos, hoteles, reservas, actividades " +
                           "y toda la funcionalidad del sistema de turismo.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipo de Desarrollo Caribe Vibes")
                        .email("desarrollo@caribevibes.com")
                        .url("https://www.caribevibes.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Lista de servidores disponibles.
     * 
     * @return Lista de servidores (desarrollo, producción)
     */
    private List<Server> serverList() {
        Server developmentServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desarrollo");

        Server productionServer = new Server()
                .url("https://api.caribevibes.com")
                .description("Servidor de Producción");

        return List.of(developmentServer, productionServer);
    }

    /**
     * Componentes de seguridad para autenticación JWT.
     * 
     * @return Componentes con esquemas de seguridad JWT
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", 
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Autenticación JWT. " +
                                           "Formato: 'Bearer {token}' donde {token} es el JWT obtenido en el login."));
    }

    /**
     * Requisitos de seguridad para los endpoints.
     * 
     * @return Configuración de seguridad requerida
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}
