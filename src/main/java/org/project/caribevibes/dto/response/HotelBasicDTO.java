package org.project.caribevibes.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta básico para información de hoteles.
 * 
 * Este DTO contiene información esencial de un hotel
 * para ser usado en respuestas anidadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class HotelBasicDTO {

    private Long id;
    private String name;
    private String address;
    private Integer stars;
    private BigDecimal basePrice;
    private String imageUrl;
    private DestinationBasicDTO destination;

    /**
     * Constructor por defecto.
     */
    public HotelBasicDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del hotel
     * @param name Nombre del hotel
     * @param address Dirección del hotel
     * @param stars Categoría del hotel (estrellas)
     * @param basePrice Precio base por noche
     * @param imageUrl URL de la imagen del hotel
     * @param destination Información básica del destino
     */
    public HotelBasicDTO(Long id, String name, String address, Integer stars, 
                        BigDecimal basePrice, String imageUrl, DestinationBasicDTO destination) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.stars = stars;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.destination = destination;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DestinationBasicDTO getDestination() {
        return destination;
    }

    public void setDestination(DestinationBasicDTO destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "HotelBasicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stars=" + stars +
                ", destination=" + destination +
                '}';
    }
}
