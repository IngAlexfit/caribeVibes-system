package org.project.caribevibes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuración CORS para el sistema Caribe Vibes.
 * 
 * Esta configuración permite el acceso desde diferentes orígenes,
 * especialmente durante el desarrollo y para integración con frontend.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Configuration
public class CorsConfig {

    /**
     * Configura las políticas CORS para la aplicación.
     * 
     * @return Fuente de configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir todos los orígenes para desarrollo (usar específicos en producción)
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Permitir todos los headers
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        
        // Headers expuestos (que el cliente puede leer)
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers",
            "Authorization",
            "Content-Disposition",
            "Content-Type",
            "Content-Length",
            "Cache-Control"
        ));
        
        // Permitir credenciales
        configuration.setAllowCredentials(true);
        
        // Tiempo máximo de cache para la respuesta pre-flight (1 hora)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
