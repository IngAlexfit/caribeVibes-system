package org.project.caribevibes.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de diagnóstico para verificar el estado de la aplicación.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/debug")
public class DiagnosticController {

    /**
     * Endpoint para verificar si los archivos estáticos están presentes.
     * 
     * @return información sobre los archivos estáticos
     */
    @GetMapping("/static-files")
    public ResponseEntity<Map<String, Object>> checkStaticFiles() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ClassPathResource indexResource = new ClassPathResource("static/index.html");
            result.put("indexHtmlExists", indexResource.exists());
            result.put("indexHtmlPath", indexResource.getURI().toString());
            
            if (indexResource.exists()) {
                String content = new String(indexResource.getInputStream().readAllBytes());
                result.put("indexHtmlSize", content.length());
                result.put("containsAppRoot", content.contains("app-root"));
            }
            
            // Verificar otros archivos comunes
            ClassPathResource faviconResource = new ClassPathResource("static/favicon.ico");
            result.put("faviconExists", faviconResource.exists());
            
        } catch (IOException e) {
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint para verificar las variables de entorno relacionadas con la configuración.
     * 
     * @return información sobre las variables de entorno
     */
    @GetMapping("/env")
    public ResponseEntity<Map<String, Object>> checkEnvironment() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("profile", System.getProperty("spring.profiles.active"));
        result.put("port", System.getenv("PORT"));
        result.put("jwtSecret", System.getenv("JWT_SECRET") != null ? "SET" : "NOT_SET");
        
        return ResponseEntity.ok(result);
    }
}
