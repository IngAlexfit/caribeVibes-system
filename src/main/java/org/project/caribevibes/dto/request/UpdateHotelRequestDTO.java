package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para la actualización de hoteles
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHotelRequestDTO {

    @NotBlank(message = "El nombre del hotel es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String description;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;

    @NotNull(message = "El ID del destino es obligatorio")
    @Positive(message = "El ID del destino debe ser positivo")
    private Long destinationId;

    @DecimalMin(value = "1.0", message = "El precio base debe ser mayor a 1")
    @DecimalMax(value = "10000000.0", message = "El precio base no puede superar 10,000,000")
    @NotNull(message = "El precio base es obligatorio")
    private BigDecimal basePrice;

    @Min(value = 1, message = "Las estrellas del hotel deben ser al menos 1")
    @Max(value = 5, message = "Las estrellas del hotel no pueden ser mayor a 5")
    private Integer stars;

    @Size(max = 500, message = "Las amenidades no pueden superar los 500 caracteres")
    private String amenities;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Formato de teléfono inválido")
    private String phoneNumber;

    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imageUrl;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean active;
}
