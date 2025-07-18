package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la actualización de actividades
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityRequestDTO {

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String description;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @DecimalMax(value = "1000.0", message = "El precio no puede superar 1000")
    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @NotBlank(message = "La duración es obligatoria")
    @Size(min = 1, max = 50, message = "La duración debe tener entre 1 y 50 caracteres")
    private String duration; // Cambiado de Integer a String

    @NotBlank(message = "El nivel de dificultad es obligatorio")
    private String difficultyLevel; // Nuevo campo para dificultad

    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
    @Max(value = 200, message = "La capacidad máxima no puede ser mayor a 200")
    private Integer maxCapacity;

    @NotNull(message = "El estado de disponibilidad es obligatorio")
    private Boolean isAvailable; // Cambiado de active a isAvailable
}
