package org.project.caribevibes.entity.hotel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * Entidad que representa un tipo de habitación en un hotel.
 * 
 * Esta clase define los diferentes tipos de habitaciones disponibles
 * en cada hotel, con sus características, precios y disponibilidad.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "room_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoomType {

    /**
     * Identificador único del tipo de habitación
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hotel al que pertenece este tipo de habitación
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;

    /**
     * Nombre del tipo de habitación
     * Ejemplos: "Individual", "Doble", "Suite", "Suite Presidencial"
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre del tipo de habitación es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;

    /**
     * Descripción detallada del tipo de habitación
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Capacidad máxima de huéspedes en este tipo de habitación
     */
    @Column(nullable = false)
    @NotNull(message = "La capacidad es requerida")
    @Min(value = 1, message = "La capacidad mínima es 1 huésped")
    private Integer capacity;

    /**
     * Precio por noche de este tipo de habitación
     */
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio por noche es requerido")
    @Min(value = 0, message = "El precio debe ser mayor a 0")
    private BigDecimal pricePerNight;

    /**
     * Número de habitaciones disponibles de este tipo
     */
    @Column(name = "available_rooms", nullable = false)
    @Builder.Default
    private Integer availableRooms = 0;

    /**
     * Número total de habitaciones de este tipo en el hotel
     */
    @Column(name = "total_rooms", nullable = false)
    @Builder.Default
    private Integer totalRooms = 0;

    /**
     * Tamaño de la habitación en metros cuadrados
     */
    @Column(name = "room_size")
    private Integer roomSize;

    /**
     * Tipo de cama disponible en la habitación
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type")
    @Builder.Default
    private BedType bedType = BedType.DOUBLE;

    /**
     * Indica si el tipo de habitación está activo
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Vista disponible desde la habitación
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "view_type")
    @Builder.Default
    private ViewType viewType = ViewType.CITY;

    /**
     * Enumeración para los tipos de cama disponibles
     */
    public enum BedType {
        /**
         * Cama individual
         */
        SINGLE("Cama Individual"),
        
        /**
         * Cama doble
         */
        DOUBLE("Cama Doble"),
        
        /**
         * Cama king size
         */
        KING("Cama King"),
        
        /**
         * Cama queen size
         */
        QUEEN("Cama Queen"),
        
        /**
         * Dos camas individuales
         */
        TWIN_BEDS("Dos Camas Individuales"),
        
        /**
         * Sofá cama
         */
        SOFA_BED("Sofá Cama");

        private final String displayName;

        BedType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeración para los tipos de vista disponibles
     */
    public enum ViewType {
        /**
         * Vista al mar
         */
        OCEAN("Vista al Mar"),
        
        /**
         * Vista a la ciudad
         */
        CITY("Vista a la Ciudad"),
        
        /**
         * Vista al jardín
         */
        GARDEN("Vista al Jardín"),
        
        /**
         * Vista a la montaña
         */
        MOUNTAIN("Vista a la Montaña"),
        
        /**
         * Vista a la piscina
         */
        POOL("Vista a la Piscina"),
        
        /**
         * Vista interior
         */
        INTERIOR("Vista Interior");

        private final String displayName;

        ViewType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Verifica si hay habitaciones disponibles de este tipo
     * 
     * @return true si hay habitaciones disponibles
     */
    public boolean hasAvailableRooms() {
        return isActive && availableRooms != null && availableRooms > 0;
    }

    /**
     * Verifica si se pueden reservar un número específico de habitaciones
     * 
     * @param requestedRooms Número de habitaciones solicitadas
     * @return true si es posible realizar la reserva
     */
    public boolean canBook(int requestedRooms) {
        return hasAvailableRooms() && availableRooms >= requestedRooms;
    }

    /**
     * Calcula el precio total para una estancia de múltiples noches
     * 
     * @param nights Número de noches
     * @param rooms Número de habitaciones
     * @return Precio total de la estancia
     */
    public BigDecimal calculateTotalPrice(int nights, int rooms) {
        if (pricePerNight == null) {
            return BigDecimal.ZERO;
        }
        return pricePerNight.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(rooms));
    }

    /**
     * Reduce la disponibilidad de habitaciones después de una reserva
     * 
     * @param roomsBooked Número de habitaciones reservadas
     */
    public void bookRooms(int roomsBooked) {
        if (availableRooms >= roomsBooked) {
            availableRooms -= roomsBooked;
        }
    }

    /**
     * Aumenta la disponibilidad de habitaciones después de una cancelación
     * 
     * @param roomsReleased Número de habitaciones liberadas
     */
    public void releaseRooms(int roomsReleased) {
        availableRooms = Math.min(availableRooms + roomsReleased, totalRooms);
    }
}
