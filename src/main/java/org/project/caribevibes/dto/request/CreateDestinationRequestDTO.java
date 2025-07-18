package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para la creación de destinos
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDestinationRequestDTO {

    @NotBlank(message = "El nombre del destino es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String description;

    @NotBlank(message = "El país es obligatorio")
    @Size(min = 2, max = 100, message = "El país debe tener entre 2 y 100 caracteres")
    private String country;

    @NotBlank(message = "La región es obligatoria")
    @Size(min = 2, max = 100, message = "La región debe tener entre 2 y 100 caracteres")
    private String region;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imageUrl;

    @Size(max = 100, message = "El clima no puede superar los 100 caracteres")
    private String climate;

    @Size(max = 100, message = "El idioma no puede superar los 100 caracteres")
    private String language;

    @Size(max = 50, message = "La moneda no puede superar los 50 caracteres")
    private String currency;

    @Size(max = 50, message = "La zona horaria no puede superar los 50 caracteres")
    private String timezone;

    @Min(value = 1, message = "La popularidad debe ser al menos 1")
    @Max(value = 5, message = "La popularidad no puede ser mayor a 5")
    private Integer popularityScore;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean active = true;

    // Lista de actividades para crear junto con el destino
    private List<CreateActivityRequestDTO> activities;

    // Lista de experiencias para crear junto con el destino
    private List<CreateExperienceRequestDTO> experiences;
}
