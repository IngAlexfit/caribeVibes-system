package org.project.caribevibes.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO de respuesta para la información de hoteles.
 * 
 * Este DTO contiene toda la información necesaria para mostrar
 * los detalles de un hotel al cliente, incluyendo ubicación,
 * servicios, precios y políticas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class HotelResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String website;
    private Integer stars;
    private BigDecimal rating; // Calificación promedio de usuarios
    private BigDecimal basePrice;
    private String amenities;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkInTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkOutTime;
    
    private String policies;
    private String destinationName; // Nombre del destino para fácil acceso
    private DestinationBasicDTO destination;
    private List<RoomTypeResponseDTO> roomTypes;

    /**
     * Constructor por defecto.
     */
    public HotelResponseDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del hotel
     * @param name Nombre del hotel
     * @param description Descripción del hotel
     * @param address Dirección del hotel
     * @param phone Teléfono de contacto
     * @param email Email de contacto
     * @param website Sitio web del hotel
     * @param stars Categoría del hotel (estrellas)
     * @param basePrice Precio base por noche
     * @param amenities Servicios y amenidades
     * @param imageUrl URL de la imagen principal
     * @param latitude Coordenada de latitud
     * @param longitude Coordenada de longitud
     * @param checkInTime Hora de check-in
     * @param checkOutTime Hora de check-out
     * @param policies Políticas del hotel
     * @param destination Información básica del destino
     * @param roomTypes Lista de tipos de habitaciones disponibles
     */
    public HotelResponseDTO(Long id, String name, String description, String address, 
                           String phone, String email, String website, Integer stars, 
                           BigDecimal basePrice, String amenities, String imageUrl, 
                           Double latitude, Double longitude, LocalTime checkInTime, 
                           LocalTime checkOutTime, String policies, 
                           DestinationBasicDTO destination, List<RoomTypeResponseDTO> roomTypes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.stars = stars;
        this.basePrice = basePrice;
        this.amenities = amenities;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.policies = policies;
        this.destination = destination;
        this.roomTypes = roomTypes;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getPolicies() {
        return policies;
    }

    public void setPolicies(String policies) {
        this.policies = policies;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public DestinationBasicDTO getDestination() {
        return destination;
    }

    public void setDestination(DestinationBasicDTO destination) {
        this.destination = destination;
    }

    public List<RoomTypeResponseDTO> getRoomTypes() {
        return roomTypes;
    }

    public void setRoomTypes(List<RoomTypeResponseDTO> roomTypes) {
        this.roomTypes = roomTypes;
    }

    @Override
    public String toString() {
        return "HotelResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stars=" + stars +
                ", basePrice=" + basePrice +
                ", destination=" + destination +
                '}';
    }
}
