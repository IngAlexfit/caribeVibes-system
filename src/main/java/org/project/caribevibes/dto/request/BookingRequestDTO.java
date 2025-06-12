package org.project.caribevibes.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO de request para crear una nueva reserva.
 * 
 * Este DTO contiene todos los datos necesarios para
 * crear una reserva de hotel.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class BookingRequestDTO {

    @NotNull(message = "El ID del hotel es obligatorio")
    private Long hotelId;

    @NotNull(message = "El ID del tipo de habitación es obligatorio")
    private Long roomTypeId;

    @NotNull(message = "La fecha de check-in es obligatoria")
    @Future(message = "La fecha de check-in debe ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull(message = "La fecha de check-out es obligatoria")
    @Future(message = "La fecha de check-out debe ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    @NotNull(message = "El número de huéspedes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Max(value = 20, message = "No se pueden reservar más de 20 huéspedes")
    private Integer numGuests;

    @NotNull(message = "El número de habitaciones es obligatorio")
    @Min(value = 1, message = "Debe reservar al menos 1 habitación")
    @Max(value = 10, message = "No se pueden reservar más de 10 habitaciones")
    private Integer numRooms;

    @Size(max = 1000, message = "Las solicitudes especiales no pueden exceder 1000 caracteres")
    private String specialRequests;

    /**
     * Constructor por defecto.
     */
    public BookingRequestDTO() {}

    /**
     * Constructor completo.
     * 
     * @param hotelId ID del hotel
     * @param roomTypeId ID del tipo de habitación
     * @param checkInDate Fecha de check-in
     * @param checkOutDate Fecha de check-out
     * @param numGuests Número de huéspedes
     * @param numRooms Número de habitaciones
     * @param specialRequests Solicitudes especiales
     */
    public BookingRequestDTO(Long hotelId, Long roomTypeId, LocalDate checkInDate, 
                            LocalDate checkOutDate, Integer numGuests, Integer numRooms, 
                            String specialRequests) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numGuests = numGuests;
        this.numRooms = numRooms;
        this.specialRequests = specialRequests;
    }

    // Getters y Setters

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
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

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    @Override
    public String toString() {
        return "BookingRequestDTO{" +
                "hotelId=" + hotelId +
                ", roomTypeId=" + roomTypeId +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numGuests=" + numGuests +
                ", numRooms=" + numRooms +
                '}';
    }
}
