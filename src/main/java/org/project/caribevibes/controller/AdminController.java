package org.project.caribevibes.controller;

import org.project.caribevibes.dto.response.BookingResponseDTO;
import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.service.contact.ContactService;
import org.project.caribevibes.service.booking.BookingService;
import org.project.caribevibes.service.hotel.HotelService;
import org.project.caribevibes.service.destination.DestinationService;
import org.project.caribevibes.service.pdf.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private PdfService pdfService;

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

    /**
     * Obtiene todas las reservas del sistema para administradores.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo por el cual ordenar (default: bookingDate)
     * @param sortDir Dirección de ordenamiento (default: desc)
     * @return ResponseEntity con página de reservas
     */
    @GetMapping("/bookings")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Admin solicitando todas las reservas - página: {}, tamaño: {}", page, size);
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<BookingResponseDTO> bookings = bookingService.getAllBookingsForAdmin(pageable);
            
            logger.debug("Recuperadas {} reservas para administrador", bookings.getTotalElements());
            return ResponseEntity.ok(bookings);
            
        } catch (Exception e) {
            logger.error("Error al obtener reservas para administrador: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene una reserva específica por ID para administradores.
     * 
     * @param id ID de la reserva
     * @return ResponseEntity con los datos de la reserva
     */
    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        logger.debug("Admin solicitando reserva con ID: {}", id);
        
        try {
            BookingResponseDTO booking = bookingService.getBookingByIdForAdmin(id);
            return ResponseEntity.ok(booking);
            
        } catch (Exception e) {
            logger.error("Error al obtener reserva {} para administrador: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene estadísticas detalladas de reservas para el panel de administración.
     * 
     * @return ResponseEntity con estadísticas de reservas
     */
    @GetMapping("/bookings/statistics")
    public ResponseEntity<Map<String, Object>> getBookingStatistics() {
        logger.debug("Admin solicitando estadísticas de reservas");
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Estadísticas básicas
            long totalBookings = bookingService.countAllBookings();
            long confirmedBookings = bookingService.countBookingsByStatus("CONFIRMED");
            long pendingBookings = bookingService.countBookingsByStatus("PENDING");
            long cancelledBookings = bookingService.countBookingsByStatus("CANCELLED");
            
            stats.put("total", totalBookings);
            stats.put("confirmed", confirmedBookings);
            stats.put("pending", pendingBookings);
            stats.put("cancelled", cancelledBookings);
            
            logger.debug("Estadísticas de reservas obtenidas exitosamente");
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de reservas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualiza el estado de una reserva (solo para administradores).
     * 
     * @param id ID de la reserva
     * @param status Nuevo estado de la reserva
     * @return ResponseEntity con el resultado de la operación
     */
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<Map<String, String>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        logger.debug("Admin actualizando estado de reserva {} a: {}", id, status);
        
        try {
            bookingService.updateBookingStatusByAdmin(id, status);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Estado de reserva actualizado exitosamente");
            response.put("bookingId", id.toString());
            response.put("newStatus", status);
            
            logger.info("Estado de reserva {} actualizado a {} por administrador", id, status);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar estado de reserva {}: {}", id, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al actualizar estado de reserva");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint temporal para completar una reserva (solo para pruebas).
     * 
     * @param bookingId ID de la reserva a completar
     * @return ResponseEntity con el resultado
     */
    @PostMapping("/complete-booking/{bookingId}")
    public ResponseEntity<Map<String, Object>> completeBooking(@PathVariable Long bookingId) {
        logger.debug("Completando reserva ID: {}", bookingId);
        
        try {
            bookingService.completeBooking(bookingId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reserva completada exitosamente");
            response.put("bookingId", bookingId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al completar reserva ID {}: {}", bookingId, e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", "No se pudo completar la reserva: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Descarga el voucher PDF de una reserva específica
     * 
     * @param id ID de la reserva
     * @return ResponseEntity con el archivo PDF del voucher
     */
    @GetMapping("/bookings/{id}/voucher")
    public ResponseEntity<byte[]> downloadBookingVoucher(@PathVariable Long id) {
        logger.debug("Admin descargando voucher para reserva ID: {}", id);
        
        try {
            // Obtener la reserva
            Optional<Booking> bookingOpt = bookingService.findBookingById(id);
            if (bookingOpt.isEmpty()) {
                logger.warn("Reserva con ID {} no encontrada", id);
                return ResponseEntity.notFound().build();
            }
            
            Booking booking = bookingOpt.get();
            
            // Generar el PDF del voucher
            byte[] pdfContent = pdfService.generateBookingVoucher(booking);
            
            // Configurar headers para descarga
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("voucher-reserva-%d.pdf", id));
            headers.setContentLength(pdfContent.length);
            
            logger.info("Voucher generado exitosamente para reserva ID: {}", id);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            logger.error("Error al generar voucher para reserva {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}