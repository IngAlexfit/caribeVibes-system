package org.project.caribevibes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Controlador para la gestión de caché del sistema.
 * 
 * Este controlador proporciona endpoints para administrar
 * el sistema de caché, incluyendo limpieza y estadísticas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/admin/cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheManager cacheManager;

    /**
     * Limpia todos los caches del sistema.
     * 
     * @return Respuesta con el resultado de la operación
     */
    @PostMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        logger.info("Iniciando limpieza de todos los caches");
        
        Map<String, Object> response = new HashMap<>();
        int clearedCaches = 0;
        
        try {
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    clearedCaches++;
                    logger.info("Cache '{}' limpiado exitosamente", cacheName);
                }
            }
            
            response.put("success", true);
            response.put("message", "Todos los caches han sido limpiados exitosamente");
            response.put("clearedCaches", clearedCaches);
            response.put("cacheNames", cacheManager.getCacheNames());
            
            logger.info("Limpieza de caches completada. Total limpiados: {}", clearedCaches);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al limpiar los caches: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error al limpiar los caches: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Limpia un cache específico.
     * 
     * @param cacheName Nombre del cache a limpiar
     * @return Respuesta con el resultado de la operación
     */
    @PostMapping("/clear/{cacheName}")
    public ResponseEntity<Map<String, Object>> clearSpecificCache(@PathVariable String cacheName) {
        logger.info("Iniciando limpieza del cache: {}", cacheName);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                response.put("success", true);
                response.put("message", "Cache '" + cacheName + "' limpiado exitosamente");
                logger.info("Cache '{}' limpiado exitosamente", cacheName);
            } else {
                response.put("success", false);
                response.put("message", "Cache '" + cacheName + "' no encontrado");
                logger.warn("Intento de limpiar cache inexistente: {}", cacheName);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al limpiar el cache '{}': {}", cacheName, e.getMessage());
            response.put("success", false);
            response.put("message", "Error al limpiar el cache: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Obtiene información sobre los caches disponibles.
     * 
     * @return Información de los caches del sistema
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        logger.debug("Obteniendo información de caches");
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> cacheDetails = new HashMap<>();
        
        try {
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    Map<String, Object> cacheInfo = new HashMap<>();
                    cacheInfo.put("name", cacheName);
                    cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                    
                    // Si es Caffeine, obtener estadísticas
                    if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                        var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                        var stats = caffeineCache.stats();
                        
                        Map<String, Object> statistics = new HashMap<>();
                        statistics.put("hitCount", stats.hitCount());
                        statistics.put("missCount", stats.missCount());
                        statistics.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
                        statistics.put("evictionCount", stats.evictionCount());
                        statistics.put("estimatedSize", caffeineCache.estimatedSize());
                        
                        cacheInfo.put("stats", statistics);
                    }
                    
                    cacheDetails.put(cacheName, cacheInfo);
                }
            }
            
            response.put("success", true);
            response.put("totalCaches", cacheManager.getCacheNames().size());
            response.put("cacheNames", cacheManager.getCacheNames());
            response.put("cacheDetails", cacheDetails);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener información de caches: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error al obtener información: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint de salud para verificar el estado de los caches.
     * 
     * @return Estado de salud de los caches
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getCacheHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean allHealthy = true;
            Map<String, String> cacheStatus = new HashMap<>();
            
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cacheStatus.put(cacheName, "HEALTHY");
                } else {
                    cacheStatus.put(cacheName, "UNAVAILABLE");
                    allHealthy = false;
                }
            }
            
            response.put("status", allHealthy ? "HEALTHY" : "DEGRADED");
            response.put("cacheStatus", cacheStatus);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al verificar salud de caches: {}", e.getMessage());
            response.put("status", "UNHEALTHY");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
