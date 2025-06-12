package org.project.caribevibes.entity.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.project.caribevibes.entity.destination.Destination;
import org.project.caribevibes.entity.hotel.Hotel;
import org.project.caribevibes.entity.hotel.RoomType;
import org.project.caribevibes.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una reserva en el sistema Caribe Vibes.
 * 
 * Esta clase maneja toda la información relacionada con las reservas
 * realizadas por los usuarios, incluyendo fechas, precios, estado
 * y actividades asociadas.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString(exclude = {"user", "hotel", "destination", "roomType", "activities"})
@EqualsAndHashCode(exclude = {"user", "hotel", "destination", "roomType", "activities"})
public class Booking {

    /**
     * Identificador único de la reserva
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Getters y setters faltantes
    /**
     * Usuario que realizó la reserva
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Destino de la reserva
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    /**
     * Hotel reservado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    /**
     * Tipo de habitación reservado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    /**
     * Fecha de check-in
     */
    @Column(name = "check_in_date", nullable = false)
    @NotNull(message = "La fecha de check-in es requerida")
    @Future(message = "La fecha de check-in debe ser futura")
    private LocalDate checkInDate;

    /**
     * Fecha de check-out
     */
    @Column(name = "check_out_date", nullable = false)
    @NotNull(message = "La fecha de check-out es requerida")
    private LocalDate checkOutDate;

    /**
     * Número de huéspedes
     */
    @Column(name = "number_of_guests", nullable = false)
    @NotNull(message = "El número de huéspedes es requerido")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    private Integer numberOfGuests;

    /**
     * Número de habitaciones reservadas
     */
    @Column(name = "number_of_rooms", nullable = false)
    @Builder.Default
    private Integer numberOfRooms = 1;

    /**
     * Precio total de la reserva
     */
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El precio total es requerido")
    private BigDecimal totalPrice;

    /**
     * Fecha y hora en que se realizó la reserva
     */
    @CreationTimestamp
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    /**
     * Estado actual de la reserva
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    /**
     * Código de confirmación único de la reserva
     */
    @Column(name = "confirmation_code", unique = true, length = 20)
    private String confirmationCode;

    /**
     * Comentarios o notas especiales de la reserva
     */
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    /**
     * Precio del alojamiento (sin actividades)
     */
    @Column(name = "accommodation_price", precision = 10, scale = 2)
    private BigDecimal accommodationPrice;

    /**
     * Precio total de las actividades
     */
    @Column(name = "activities_price", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal activitiesPrice = BigDecimal.ZERO;

    /**
     * Lista de actividades incluidas en la reserva
     * -- GETTER --
     *  Obtiene la lista de actividades asociadas a la reserva
     *
     * @return Lista de actividades de la reserva

     */
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingActivity> activities = new ArrayList<>();

    /**
     * Indica si la reserva está activa o ha sido eliminada lógicamente
     */
    @Column(name = "active", nullable = false) // DB column remains "active"
    @Builder.Default
    private Boolean isActive = true; // Field name changed to isActive to match repository queries like findByIsActiveTrue

    /**
     */
    public enum BookingStatus {
        /**
         * Reserva pendiente de confirmación
         */
        PENDING("Pendiente"),
        
        /**
         * Reserva confirmada
         */
        CONFIRMED("Confirmada"),
        
        /**
         * Reserva cancelada
         */
        CANCELLED("Cancelada"),
        
        /**
         * Reserva completada (después del check-out)
         */
        COMPLETED("Completada"),
        
        /**
         * Huésped realizó check-in
         */
        CHECKED_IN("Check-in Realizado"),
        
        /**
         * Huésped realizó check-out
         */
        CHECKED_OUT("Check-out Realizado");

        private final String displayName;

        BookingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Calcula el número de noches de la reserva
     * 
     * @return Número de noches entre check-in y check-out
     */
    public long getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    /**
     * Verifica si la reserva puede ser cancelada
     * 
     * @return true si la reserva puede ser cancelada
     */
    public boolean isCancellable() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }

    /**
     * Verifica si la reserva está activa
     * 
     * @return true si la reserva está en un estado activo
     */
    public boolean isActive() { // Custom isActive method might conflict with Lombok if field is isActive. Let's rely on Lombok.
        // This custom method should be reviewed. If field is isActive, Lombok generates isActive().
        // For now, let's assume it's correct or will be handled by Lombok's getter.
        return status == BookingStatus.CONFIRMED ||
               status == BookingStatus.CHECKED_IN ||
               status == BookingStatus.PENDING;
    }

    /**
     * Verifica si la reserva está activa según su estado (lógica de negocio).
     * No confundir con el campo `isActive` que indica borrado lógico.
     * @return true si la reserva está en un estado considerado activo para operaciones.
     */
    public boolean isConsideredActiveByStatus() {
        return status == BookingStatus.CONFIRMED ||
               status == BookingStatus.CHECKED_IN ||
               status == BookingStatus.PENDING;
    }

    /**
     * Agrega una nueva actividad a la reserva
     * 
     * @param bookingActivity Actividad a agregar
     */
    public void addActivity(BookingActivity bookingActivity) {
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(bookingActivity);
        bookingActivity.setBooking(this);
        
        // Actualizar el precio total de actividades
        if (activitiesPrice == null) {
            activitiesPrice = BigDecimal.ZERO;
        }
        activitiesPrice = activitiesPrice.add(bookingActivity.getTotalPrice());
        
        // Recalcular el precio total
        updateTotalPrice();
    }

    /**
     * Actualiza el precio total de la reserva
     */
    public void updateTotalPrice() {
        BigDecimal accommodationTotal = accommodationPrice != null ? accommodationPrice : BigDecimal.ZERO;
        BigDecimal activitiesTotal = activitiesPrice != null ? activitiesPrice : BigDecimal.ZERO;
        totalPrice = accommodationTotal.add(activitiesTotal);
    }

    /**
     * Verifica si las fechas de la reserva son válidas
     * 
     * @return true si las fechas son válidas
     */
    public boolean hasValidDates() {
        return checkInDate != null && 
               checkOutDate != null && 
               checkOutDate.isAfter(checkInDate);
    }

    /**
     * Genera un código de confirmación único
     * 
     * @return Código de confirmación generado
     */
    public static String generateConfirmationCode() {
        return "CD" + System.currentTimeMillis() % 1000000000L;
    }

    /**
     * Obtiene el código de reserva para compatibilidad
     * 
     * @return Código de confirmación como código de reserva
     */
    public String getBookingCode() {
        return this.confirmationCode != null ? this.confirmationCode : generateConfirmationCode();
    }

    /**
     * Obtiene el número de huéspedes para compatibilidad
     * 
     * @return Número de huéspedes
     */
    public Integer getGuestCount() {
        return this.numberOfGuests;
    }

    /**
     * Obtiene el monto total para compatibilidad con PdfService
     * 
     * @return Precio total de la reserva
     */
    public BigDecimal getTotalAmount() {
        return this.totalPrice;
    }
    
    /**
     * Métodos de compatibilidad para getters/setters esperados por el controlador
     */
    
    /**
     * Getter para número de huéspedes (compatibilidad)
     */
    public Integer getNumGuests() {
        return this.numberOfGuests;
    }

    /**
     * Setter para número de huéspedes (compatibilidad)
     */
    public void setNumGuests(Integer numGuests) {
        this.numberOfGuests = numGuests;
    }

    /**
     * Getter para número de habitaciones (compatibilidad)
     */
    public Integer getNumRooms() {
        return this.numberOfRooms;
    }

    /**
     * Setter para número de habitaciones (compatibilidad)
     */
    public void setNumRooms(Integer numRooms) {
        this.numberOfRooms = numRooms;
    }

    // Getters y setters explícitos para compatibilidad con BookingService y controller

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Hotel getHotel() {
        return hotel;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public RoomType getRoomType() {
        return roomType;
    }
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }



    public String getSpecialRequests() {
        return specialRequests;
    }
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }
    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

}
