package org.project.caribevibes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador público para verificar el estado de salud del sistema.
 * 
 * Este controlador proporciona endpoints públicos (sin autenticación)
 * para verificar que la aplicación está funcionando correctamente.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    /**
     * Endpoint público de health check para verificar que la aplicación está funcionando.
     * Este endpoint no requiere autenticación y puede ser usado por el frontend
     * para verificar la conectividad con el backend.
     * 
     * @return ResponseEntity con el estado de salud básico del sistema
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.debug("Health check público solicitado");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("service", "Caribe Vibes API");
            response.put("version", "1.0.0");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Sistema funcionando correctamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en health check público: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "DOWN");
            errorResponse.put("service", "Caribe Vibes API");
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("message", "Error en el sistema");
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Endpoint público para obtener información básica de la API.
     * 
     * @return ResponseEntity con información básica de la API
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        logger.debug("Información de API solicitada");
        
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Caribe Vibes API");
        info.put("version", "1.0.0");
        info.put("description", "API REST para el sistema de reservas turísticas Caribe Vibes");
        info.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(info);
    }
}
