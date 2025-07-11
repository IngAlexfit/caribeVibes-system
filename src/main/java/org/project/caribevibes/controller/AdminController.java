package org.project.caribevibes.controller;

import org.project.caribevibes.service.contact.ContactService;
import org.project.caribevibes.service.booking.BookingService;
import org.project.caribevibes.service.hotel.HotelService;
import org.project.caribevibes.service.destination.DestinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para operaciones de administración del sistema.
 * 
 * Este controlador proporciona endpoints para que los administradores
 * puedan acceder a estadísticas y funciones de gestión del sistema.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private DestinationService destinationService;

    /**
     * Obtiene estadísticas generales del sistema para el dashboard de administración.
     * 
     * @return ResponseEntity con estadísticas del sistema
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        logger.debug("Obteniendo estadísticas generales del sistema");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // Estadísticas de contactos
            long[] contactStats = contactService.getContactStatistics();
            Map<String, Object> contacts = new HashMap<>();
            contacts.put("total", contactStats[0]);
            contacts.put("new", contactStats[1]);
            contacts.put("read", contactStats[2]);
            contacts.put("replied", contactStats[3]);
            
            // Contar elementos básicos
            long totalBookings = bookingService.countAllBookings();
            long totalHotels = hotelService.countActiveHotels();
            long totalDestinations = destinationService.countActiveDestinations();
            
            // Construir respuesta
            statistics.put("contacts", contacts);
            statistics.put("totalBookings", totalBookings);
            statistics.put("totalHotels", totalHotels);
            statistics.put("totalDestinations", totalDestinations);
            
            logger.debug("Estadísticas del sistema obtenidas exitosamente");
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas del sistema: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", "No se pudieron obtener las estadísticas del sistema");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtiene el estado de salud del sistema.
     * 
     * @return ResponseEntity con el estado de salud
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        logger.debug("Verificando estado de salud del sistema");
        
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verificaciones básicas
            boolean contactServiceHealthy = contactService != null;
            boolean bookingServiceHealthy = bookingService != null;
            boolean hotelServiceHealthy = hotelService != null;
            boolean destinationServiceHealthy = destinationService != null;
            
            boolean overallHealthy = contactServiceHealthy && bookingServiceHealthy && 
                                   hotelServiceHealthy && destinationServiceHealthy;
            
            health.put("status", overallHealthy ? "HEALTHY" : "DEGRADED");
            health.put("contactService", contactServiceHealthy ? "UP" : "DOWN");
            health.put("bookingService", bookingServiceHealthy ? "UP" : "DOWN");
            health.put("hotelService", hotelServiceHealthy ? "UP" : "DOWN");
            health.put("destinationService", destinationServiceHealthy ? "UP" : "DOWN");
            health.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            logger.error("Error al verificar salud del sistema: {}", e.getMessage(), e);
            health.put("status", "UNHEALTHY");
            health.put("error", e.getMessage());
            health.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.internalServerError().body(health);
        }
    }
}