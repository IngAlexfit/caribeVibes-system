package org.project.caribevibes.dto.response;

/**
 * DTO de respuesta básico para información de destinos.
 * 
 * Este DTO contiene información esencial de un destino
 * para ser usado en respuestas anidadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class DestinationBasicDTO {

    private Long id;
    private String name;
    private String country;
    private String imageUrl;

    /**
     * Constructor por defecto.
     */
    public DestinationBasicDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del destino
     * @param name Nombre del destino
     * @param country País del destino
     * @param imageUrl URL de la imagen del destino
     */
    public DestinationBasicDTO(Long id, String name, String country, String imageUrl) {
        this.id = id;
        this.name = name;
        this.country = country;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "DestinationBasicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
