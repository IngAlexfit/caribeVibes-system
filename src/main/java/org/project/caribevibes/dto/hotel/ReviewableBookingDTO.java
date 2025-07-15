package org.project.caribevibes.dto.hotel;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para reservas que pueden ser reseñadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewableBookingDTO {
    
    private Long bookingId;
    private String confirmationCode;
    private LocalDateTime bookingDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    
    // Información del hotel
    private Long hotelId;
    private String hotelName;
    private String hotelImageUrl;
    private Integer hotelStars;
    
    // Información básica de la reserva
    private Integer numGuests;
    private Integer numRooms;
    private Double totalPrice;
}
