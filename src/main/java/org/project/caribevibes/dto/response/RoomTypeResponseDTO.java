package org.project.caribevibes.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta para la información de tipos de habitaciones.
 * 
 * Este DTO contiene toda la información necesaria para mostrar
 * los detalles de un tipo de habitación al cliente.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class RoomTypeResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer maxOccupancy;
    private String bedType;
    private Double size;
    private BigDecimal pricePerNight;
    private String amenities;
    private String imageUrl;
    private Integer totalRooms;
    private Integer availableRooms;
    private Boolean isActive;

    /**
     * Constructor por defecto.
     */
    public RoomTypeResponseDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del tipo de habitación
     * @param name Nombre del tipo de habitación
     * @param description Descripción detallada
     * @param maxOccupancy Ocupación máxima
     * @param bedType Tipo de cama
     * @param size Tamaño en metros cuadrados
     * @param pricePerNight Precio por noche
     * @param amenities Amenidades incluidas
     * @param imageUrl URL de la imagen
     * @param totalRooms Total de habitaciones de este tipo
     * @param availableRooms Habitaciones disponibles
     * @param isActive Estado activo/inactivo del tipo de habitación
     */
    public RoomTypeResponseDTO(Long id, String name, String description, Integer maxOccupancy, 
                              String bedType, Double size, BigDecimal pricePerNight, 
                              String amenities, String imageUrl, Integer totalRooms, 
                              Integer availableRooms, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
        this.bedType = bedType;
        this.size = size;
        this.pricePerNight = pricePerNight;
        this.amenities = amenities;
        this.imageUrl = imageUrl;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.isActive = isActive;
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

    public Integer getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(Integer maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
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

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "RoomTypeResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxOccupancy=" + maxOccupancy +
                ", pricePerNight=" + pricePerNight +
                ", availableRooms=" + availableRooms +
                ", isActive=" + isActive +
                '}';
    }
}
