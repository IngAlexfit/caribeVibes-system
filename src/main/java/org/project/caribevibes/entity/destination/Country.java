package org.project.caribevibes.entity.destination;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un país en el sistema Caribe Vibes.
 * 
 * Esta clase contiene información sobre los países donde se encuentran
 * los destinos turísticos disponibles.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Entity
@Table(name = "countries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Country {

    /**
     * Identificador único del país
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del país
     */
    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "El nombre del país es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    /**
     * Código ISO 3166-1 alpha-3 del país (ej: COL, USA, BRA)
     */
    @Column(nullable = false, length = 3, unique = true)
    @NotBlank(message = "El código del país es requerido")
    @Size(min = 3, max = 3, message = "El código debe tener exactamente 3 caracteres")
    private String code;

    /**
     * Continente al que pertenece el país
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "El continente es requerido")
    @Size(max = 50, message = "El continente no puede exceder 50 caracteres")
    private String continent;

    /**
     * Código de moneda ISO 4217 (ej: COP, USD, EUR)
     */
    @Column(length = 3)
    @Size(max = 3, message = "El código de moneda no puede exceder 3 caracteres")
    private String currency;

    /**
     * Prefijo telefónico del país (ej: +57, +1, +55)
     */
    @Column(name = "phone_prefix", length = 5)
    @Size(max = 5, message = "El prefijo telefónico no puede exceder 5 caracteres")
    private String phonePrefix;

    /**
     * Indica si el país está activo en el sistema
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Lista de destinos en este país
     */
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Destination> destinations = new ArrayList<>();

    /**
     * Agrega un nuevo destino al país
     * 
     * @param destination Destino a agregar
     */
    public void addDestination(Destination destination) {
        if (destinations == null) {
            destinations = new ArrayList<>();
        }
        destinations.add(destination);
        destination.setCountry(this);
    }

    /**
     * Obtiene el nombre completo del país con el código
     * 
     * @return Nombre del país con código (ej: "Colombia (COL)")
     */
    public String getDisplayName() {
        return name + " (" + code + ")";
    }

    /**
     * Verifica si el país está disponible para destinos
     * 
     * @return true si el país está activo y tiene destinos
     */
    public boolean isAvailableForTourism() {
        return isActive && destinations != null && !destinations.isEmpty();
    }
}
