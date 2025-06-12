package org.project.caribevibes.entity.destination;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entidad que representa un destino turístico en el sistema Caribe Vibes.
 * 
 * Esta clase contiene toda la información relacionada con los destinos
 * disponibles para reservar, incluyendo descripción, precios, actividades
 * y experiencias asociadas.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "destinations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Destination {

    /**
     * Identificador único del destino
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador único de URL del destino (slug)
     * Se utiliza para crear URLs amigables
     */
    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "El slug es requerido")
    @Size(max = 100, message = "El slug no puede exceder 100 caracteres")
    private String slug;

    /**
     * Nombre del destino turístico
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre del destino es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    /**
     * Descripción breve del destino
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Descripción detallada del destino
     */
    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    /**
     * URL de la imagen principal del destino
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Lista de etiquetas del destino almacenadas como JSON
     * Ejemplo: ["Playas", "Buceo", "Relax"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "JSON")
    private List<String> tags;

    /**
     * Lista de experiencias disponibles en el destino
     * Ejemplo: ["aventura", "relax", "cultural"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experiences", columnDefinition = "JSON")
    private List<String> experiences;

    /**
     * Fecha y hora de creación del destino
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Precio base durante temporada baja
     */
    @Column(name = "low_season_price", precision = 10, scale = 2)
    private BigDecimal lowSeasonPrice;

    /**
     * Precio base durante temporada alta
     */
    @Column(name = "high_season_price", precision = 10, scale = 2)
    private BigDecimal highSeasonPrice;

    /**
     * Lista de actividades disponibles en este destino
     */
    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Activity> activities = new ArrayList<>();

    /**
     * Obtiene el precio según la temporada especificada
     * 
     * @param isHighSeason true si es temporada alta, false si es temporada baja
     * @return Precio correspondiente a la temporada
     */
    public BigDecimal getSeasonPrice(boolean isHighSeason) {
        return isHighSeason ? highSeasonPrice : lowSeasonPrice;
    }

    /**
     * Verifica si el destino tiene una experiencia específica
     * 
     * @param experience Nombre de la experiencia a verificar
     * @return true si el destino incluye la experiencia especificada
     */
    public boolean hasExperience(String experience) {
        return experiences != null && experiences.contains(experience);
    }

    /**
     * Agrega una nueva actividad al destino
     * 
     * @param activity Actividad a agregar
     */
    public void addActivity(Activity activity) {
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(activity);
        activity.setDestination(this);
    }
}
