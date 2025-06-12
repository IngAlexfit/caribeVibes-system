package org.project.caribevibes.entity.destination;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad que representa un tipo de experiencia turística.
 * 
 * Las experiencias categorizar los destinos y actividades según el tipo
 * de vivencia que ofrecen (aventura, relax, cultural, etc.).
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "experiences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Experience {

    /**
     * Identificador único de la experiencia
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador único de URL de la experiencia (slug)
     */
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "El slug es requerido")
    @Size(max = 50, message = "El slug no puede exceder 50 caracteres")
    private String slug;

    /**
     * Nombre de la experiencia
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre de la experiencia es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    /**
     * Descripción de la experiencia
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * URL del ícono representativo de la experiencia
     */
    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    /**
     * Color asociado a la experiencia para la interfaz de usuario
     */
    @Column(name = "color", length = 7)
    private String color;

    /**
     * Orden de visualización de la experiencia
     */
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    /**
     * Indica si la experiencia está activa
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
