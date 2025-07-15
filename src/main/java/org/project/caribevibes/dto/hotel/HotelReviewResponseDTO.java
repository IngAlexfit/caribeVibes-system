package org.project.caribevibes.dto.hotel;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para reseñas de hotel.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelReviewResponseDTO {
    
    private Long id;
    private Integer rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean editable;
    
    // Información del usuario (limitada por privacidad)
    private Long userId; // ID del usuario para validaciones de permisos
    private String userFirstName;
    private String userLastInitial; // Solo inicial del apellido
    
    // Información básica del hotel
    private Long hotelId;
    private String hotelName;
    
    // Información de la reserva
    private Long bookingId;
    private LocalDateTime bookingDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String guestName;
    private LocalDateTime reviewDate;
}
