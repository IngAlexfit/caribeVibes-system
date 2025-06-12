package org.project.caribevibes.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DestinationResponseDTO {
    private Long id; // Changed from Integer to Long
    private String slug;
    private String name;
    private String description;
    private String longDescription; // Added based on DestinationService
    private String location;
    private String category;
    private String imageUrl;
    private List<String> tags; // Added based on DestinationService
    private List<ExperienceResponseDTO> experiences;
    private List<ActivityDTO> activities; // Added based on DestinationService
    private BigDecimal lowSeasonPrice; // Added based on DestinationService
    private BigDecimal highSeasonPrice; // Added based on DestinationService
    private LocalDateTime createdAt; // Added based on DestinationService
    // Add other relevant fields from the Destination entity

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityDTO {
        private Long id; // Changed from Integer to Long
        private String name;
        private String description;
        private String duration;
        private BigDecimal price;
        private String difficultyLevel;
        private Integer maxCapacity;
        private Boolean isAvailable;
    }
}
