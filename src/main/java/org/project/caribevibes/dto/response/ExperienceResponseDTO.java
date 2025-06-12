package org.project.caribevibes.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceResponseDTO {
    private Long id; // Changed from Integer to Long
    private String slug; // Added based on DestinationService
    private String name;
    private String description;
    private BigDecimal price;
    private String duration;
    private String iconUrl; // Added based on DestinationService
    private String color; // Added based on DestinationService
    private Integer displayOrder; // Added based on DestinationService
    private Boolean isActive; // Added based on DestinationService
    // Add other relevant fields from the Experience entity
}
