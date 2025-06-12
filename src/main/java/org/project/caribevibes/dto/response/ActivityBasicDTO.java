package org.project.caribevibes.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta básico para información de actividades.
 * 
 * Este DTO contiene información esencial de una actividad
 * para ser usado en respuestas anidadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class ActivityBasicDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private String difficulty;
    private String imageUrl;

    /**
     * Constructor por defecto.
     */
    public ActivityBasicDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único de la actividad
     * @param name Nombre de la actividad
     * @param description Descripción de la actividad
     * @param price Precio de la actividad
     * @param duration Duración en minutos
     * @param difficulty Nivel de dificultad
     * @param imageUrl URL de la imagen
     */
    public ActivityBasicDTO(Long id, String name, String description, BigDecimal price, 
                           Integer duration, String difficulty, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.difficulty = difficulty;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ActivityBasicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}
