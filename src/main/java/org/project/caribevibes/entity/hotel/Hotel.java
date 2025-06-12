package org.project.caribevibes.entity.hotel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.project.caribevibes.entity.destination.Destination;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un hotel en el sistema Caribe Vibes.
 * 
 * Esta clase contiene toda la información relacionada con los hoteles
 * disponibles en cada destino, incluyendo sus amenidades, tipos de
 * habitaciones y servicios ofrecidos.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "hotels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Hotel {

    // Getters y setters
    /**
     * Identificador único del hotel
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Destino al que pertenece este hotel
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    @JsonIgnore
    private Destination destination;

    /**
     * Nombre del hotel
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre del hotel es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    /**
     * Descripción detallada del hotel
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Dirección física del hotel
     */
    @Column(length = 500)
    private String address;

    /**
     * URL de la imagen principal del hotel
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Clasificación del hotel en estrellas (1-5)
     */
    @Column(name = "stars")
    @Min(value = 1, message = "La clasificación mínima es 1 estrella")
    @Max(value = 5, message = "La clasificación máxima es 5 estrellas")
    private Integer stars;

    /**
     * Precio base del hotel, podría ser el precio mínimo de una habitación estándar.
     */
    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    /**
     * Calificación promedio del hotel (e.g., de 0.0 a 5.0)
     */
    @Column(name = "rating", precision = 3, scale = 2) // Example: 4.75
    private BigDecimal rating;

    /**
     * Lista de amenidades del hotel almacenadas como JSON
     * Ejemplo: ["wifi", "piscina", "spa", "gimnasio", "restaurante"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "amenities", columnDefinition = "JSON")
    private List<String> amenities;

    /**
     * Fecha y hora de creación del registro del hotel
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Número de teléfono del hotel
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Correo electrónico del hotel
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * Sitio web oficial del hotel
     */
    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    /**
     * Indica si el hotel está activo y disponible para reservas
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Política de check-in del hotel
     */
    @Column(name = "checkin_policy", columnDefinition = "TEXT")
    private String checkinPolicy;

    /**
     * Política de check-out del hotel
     */
    @Column(name = "checkout_policy", columnDefinition = "TEXT")
    private String checkoutPolicy;

    /**
     * Política de cancelación del hotel
     */
    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy;

    /**
     * Lista de tipos de habitaciones disponibles en el hotel
     */
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RoomType> roomTypes = new ArrayList<>();

    /**
     * Verifica si el hotel tiene una amenidad específica
     * 
     * @param amenity Nombre de la amenidad a verificar
     * @return true si el hotel incluye la amenidad especificada
     */
    public boolean hasAmenity(String amenity) {
        return amenities != null && amenities.contains(amenity);
    }

    /**
     * Agrega un nuevo tipo de habitación al hotel
     * 
     * @param roomType Tipo de habitación a agregar
     */
    public void addRoomType(RoomType roomType) {
        if (roomTypes == null) {
            roomTypes = new ArrayList<>();
        }
        roomTypes.add(roomType);
        roomType.setHotel(this);
    }

    /**
     * Obtiene la representación en texto de las estrellas del hotel
     * 
     * @return String con las estrellas del hotel
     */
    public String getStarsDisplay() {
        if (stars == null) {
            return "Sin clasificar";
        }
        return "★".repeat(stars) + "☆".repeat(5 - stars);
    }

    /**
     * Verifica si el hotel está disponible para reservas
     * 
     * @return true si el hotel está activo y tiene habitaciones disponibles
     */
    public boolean isAvailableForBooking() {
        return isActive && roomTypes != null && !roomTypes.isEmpty();
    }

}
