package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la creación de tipos de habitación
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomTypeRequestDTO {

    @NotBlank(message = "El nombre del tipo de habitación es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String description;

    @DecimalMin(value = "1.0", message = "El precio debe ser mayor a 1")
    @DecimalMax(value = "10000000.0", message = "El precio no puede superar 10,000,000")
    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 10, message = "La capacidad no puede ser mayor a 10")
    @NotNull(message = "La capacidad es obligatoria")
    private Integer capacity;

    @Min(value = 1, message = "Debe haber al menos 1 habitación disponible")
    @Max(value = 100, message = "No pueden haber más de 100 habitaciones disponibles")
    @NotNull(message = "Las habitaciones disponibles son obligatorias")
    private Integer availableRooms;

    @Size(max = 500, message = "Las amenidades no pueden superar los 500 caracteres")
    private String amenities;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imageUrl;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean active = true;
}
