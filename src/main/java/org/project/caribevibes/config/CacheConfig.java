package org.project.caribevibes.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

/**
 * Configuración de cache para optimizar el rendimiento de la aplicación.
 * 
 * Implementa un sistema de cache en memoria usando Caffeine para
 * mejorar los tiempos de respuesta en consultas frecuentes como
 * usuarios, hoteles, destinos y actividades.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Configuration
@EnableCaching
@Profile("!test") // No activar cache en tests
public class CacheConfig {

    /**
     * Configuración del gestor de cache principal.
     * 
     * @return CacheManager configurado con Caffeine y políticas optimizadas
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configuración base para todos los caches
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(Duration.ofMinutes(10))
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats());
        
        // Nombres de caches predefinidos
        cacheManager.setCacheNames(java.util.Arrays.asList("users", "hotels", "destinations", "activities", 
                                  "roomTypes", "bookings", "experiences"));
        
        return cacheManager;
    }

    /**
     * Cache específico para usuarios con configuración optimizada.
     * 
     * @return CacheManager para datos de usuarios
     */
    @Bean("userCacheManager")
    public CacheManager userCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("users");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterAccess(Duration.ofMinutes(15))
                .expireAfterWrite(Duration.ofHours(2))
                .recordStats());
        return cacheManager;
    }

    /**
     * Cache específico para hoteles y destinos con mayor duración.
     * 
     * @return CacheManager para datos de hoteles y destinos
     */
    @Bean("hotelCacheManager")
    public CacheManager hotelCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("hotels", "destinations", "roomTypes");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterAccess(Duration.ofMinutes(30))
                .expireAfterWrite(Duration.ofHours(6))
                .recordStats());
        return cacheManager;
    }

    /**
     * Cache específico para actividades y experiencias.
     * 
     * @return CacheManager para datos de actividades
     */
    @Bean("activityCacheManager")
    public CacheManager activityCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("activities", "experiences");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1500)
                .expireAfterAccess(Duration.ofMinutes(20))
                .expireAfterWrite(Duration.ofHours(4))
                .recordStats());
        return cacheManager;
    }
}
