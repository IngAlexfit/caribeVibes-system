package org.project.caribevibes.entity.destination;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * Entidad que representa una actividad turística disponible en un destino.
 * 
 * Las actividades son experiencias específicas que los usuarios pueden
 * realizar en cada destino, como buceo, senderismo, tours culturales, etc.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Activity {

    /**
     * Identificador único de la actividad
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Destino al que pertenece esta actividad
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    @JsonIgnore
    private Destination destination;

    /**
     * Nombre de la actividad
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre de la actividad es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    /**
     * Descripción detallada de la actividad
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Duración estimada de la actividad
     * Ejemplo: "2 horas", "Día completo", "3-4 horas"
     */
    @Column(length = 50)
    private String duration;

    /**
     * Precio de la actividad por persona
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Nivel de dificultad de la actividad
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.EASY;

    /**
     * Capacidad máxima de participantes por sesión
     */
    @Column(name = "max_capacity")
    private Integer maxCapacity;

    /**
     * Indica si la actividad está disponible actualmente
     */
    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Enumeración para los niveles de dificultad de las actividades
     */
    public enum DifficultyLevel {
        /**
         * Actividad fácil, apta para todas las edades
         */
        EASY("Fácil"),
        
        /**
         * Actividad de dificultad moderada
         */
        MODERATE("Moderado"),
        
        /**
         * Actividad difícil, requiere buena condición física
         */
        HARD("Difícil"),
        
        /**
         * Actividad extrema, solo para expertos
         */
        EXTREME("Extremo");

        private final String displayName;

        DifficultyLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Verifica si la actividad está disponible para reservar
     * 
     * @return true si la actividad está disponible
     */
    public boolean isBookable() {
        return isAvailable && price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Calcula el precio total para un número específico de participantes
     * 
     * @param participants Número de participantes
     * @return Precio total de la actividad
     */
    public BigDecimal calculateTotalPrice(int participants) {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(participants));
    }
}
