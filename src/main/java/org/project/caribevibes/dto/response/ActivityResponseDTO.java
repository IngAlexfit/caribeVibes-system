package org.project.caribevibes.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para actividades
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String duration; // Cambiado de Integer a String para coincidir con la entidad
    private String difficultyLevel; // Para el enum DifficultyLevel
    private Integer maxCapacity;
    private Boolean isAvailable; // Cambiado de active a isAvailable
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Datos del destino asociado
    private Long destinationId;
    private String destinationName;
}
