package org.project.caribevibes.dto.hotel;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para crear una nueva reseña de hotel.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHotelReviewDTO {

    @NotNull(message = "El ID de la reserva es requerido")
    private Long bookingId;

    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    @NotBlank(message = "El título es requerido")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;

    @NotBlank(message = "El comentario es requerido")
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comment;
}