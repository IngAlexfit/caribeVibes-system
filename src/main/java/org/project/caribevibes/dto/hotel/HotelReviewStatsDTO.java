package org.project.caribevibes.dto.hotel;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para estadísticas de reseñas de un hotel.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelReviewStatsDTO {
    
    private Long hotelId;
    private String hotelName;
    private Double averageRating;
    private Long totalReviews;
    
    // Distribución de calificaciones
    private Integer oneStar;
    private Integer twoStars;
    private Integer threeStars;
    private Integer fourStars;
    private Integer fiveStars;
    
    // Porcentajes de cada calificación
    private Double oneStarPercent;
    private Double twoStarsPercent;
    private Double threeStarsPercent;
    private Double fourStarsPercent;
    private Double fiveStarsPercent;
}
