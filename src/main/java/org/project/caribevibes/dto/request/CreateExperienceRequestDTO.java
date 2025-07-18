package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la creación de experiencias
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExperienceRequestDTO {

    @NotBlank(message = "El título de la experiencia es obligatorio")
    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String description;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imageUrl;

    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Max(value = 30, message = "La duración no puede ser mayor a 30 días")
    private Integer duration;

    @NotBlank(message = "El tipo de experiencia es obligatorio")
    @Size(min = 2, max = 50, message = "El tipo debe tener entre 2 y 50 caracteres")
    private String type;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean active = true;
}
