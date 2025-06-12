package org.project.caribevibes.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para la información de reservas.
 * 
 * Este DTO contiene toda la información necesaria para mostrar
 * los detalles de una reserva al cliente, incluyendo hotel,
 * habitación, fechas, precios y actividades asociadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class BookingResponseDTO {

    private Long id;
    private String confirmationCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookingDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    
    private Integer numGuests;
    private Integer numRooms;
    private BigDecimal totalPrice;
    private String status;
    private String specialRequests;
    
    private UserBasicDTO user;
    private HotelBasicDTO hotel;
    private RoomTypeBasicDTO roomType;
    private List<BookingActivityResponseDTO> activities;

    /**
     * Constructor por defecto.
     */
    public BookingResponseDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único de la reserva
     * @param confirmationCode Código de confirmación
     * @param bookingDate Fecha de creación de la reserva
     * @param checkInDate Fecha de check-in
     * @param checkOutDate Fecha de check-out
     * @param numGuests Número de huéspedes
     * @param numRooms Número de habitaciones
     * @param totalPrice Precio total de la reserva
     * @param status Estado de la reserva
     * @param specialRequests Solicitudes especiales
     * @param user Información básica del usuario
     * @param hotel Información básica del hotel
     * @param roomType Información básica del tipo de habitación
     * @param activities Lista de actividades asociadas
     */
    public BookingResponseDTO(Long id, String confirmationCode, LocalDateTime bookingDate,
                             LocalDate checkInDate, LocalDate checkOutDate, Integer numGuests,
                             Integer numRooms, BigDecimal totalPrice, String status,
                             String specialRequests, UserBasicDTO user, HotelBasicDTO hotel,
                             RoomTypeBasicDTO roomType, List<BookingActivityResponseDTO> activities) {
        this.id = id;
        this.confirmationCode = confirmationCode;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numGuests = numGuests;
        this.numRooms = numRooms;
        this.totalPrice = totalPrice;
        this.status = status;
        this.specialRequests = specialRequests;
        this.user = user;
        this.hotel = hotel;
        this.roomType = roomType;
        this.activities = activities;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
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

    public Integer getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(Integer numGuests) {
        this.numGuests = numGuests;
    }

    public Integer getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(Integer numRooms) {
        this.numRooms = numRooms;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public UserBasicDTO getUser() {
        return user;
    }

    public void setUser(UserBasicDTO user) {
        this.user = user;
    }

    public HotelBasicDTO getHotel() {
        return hotel;
    }

    public void setHotel(HotelBasicDTO hotel) {
        this.hotel = hotel;
    }

    public RoomTypeBasicDTO getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeBasicDTO roomType) {
        this.roomType = roomType;
    }

    public List<BookingActivityResponseDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<BookingActivityResponseDTO> activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return "BookingResponseDTO{" +
                "id=" + id +
                ", confirmationCode='" + confirmationCode + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
