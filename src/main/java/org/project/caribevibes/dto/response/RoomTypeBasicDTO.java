package org.project.caribevibes.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta básico para información de tipos de habitaciones.
 * 
 * Este DTO contiene información esencial de un tipo de habitación
 * para ser usado en respuestas anidadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class RoomTypeBasicDTO {

    private Long id;
    private String name;
    private Integer maxOccupancy;
    private String bedType;
    private BigDecimal pricePerNight;
    private String imageUrl;

    /**
     * Constructor por defecto.
     */
    public RoomTypeBasicDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del tipo de habitación
     * @param name Nombre del tipo de habitación
     * @param maxOccupancy Ocupación máxima
     * @param bedType Tipo de cama
     * @param pricePerNight Precio por noche
     * @param imageUrl URL de la imagen
     */
    public RoomTypeBasicDTO(Long id, String name, Integer maxOccupancy, String bedType, 
                           BigDecimal pricePerNight, String imageUrl) {
        this.id = id;
        this.name = name;
        this.maxOccupancy = maxOccupancy;
        this.bedType = bedType;
        this.pricePerNight = pricePerNight;
        this.imageUrl = imageUrl;
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

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "RoomTypeBasicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxOccupancy=" + maxOccupancy +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
