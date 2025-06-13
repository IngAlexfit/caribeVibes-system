package org.project.caribevibes.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta para actividades asociadas a reservas.
 * 
 * Este DTO contiene información de las actividades que
 * un cliente ha agregado a su reserva.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class BookingActivityResponseDTO {

    private Long id;
    private Integer quantity;
    private BigDecimal pricePerPerson;
    private BigDecimal totalPrice;
    private ActivityBasicDTO activity;

    /**
     * Constructor por defecto.
     */
    public BookingActivityResponseDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único de la actividad de reserva
     * @param quantity Cantidad de personas para la actividad
     * @param pricePerPerson Precio por persona
     * @param totalPrice Precio total de la actividad
     * @param activity Información básica de la actividad
     */
    public BookingActivityResponseDTO(Long id, Integer quantity, BigDecimal pricePerPerson,
                                     BigDecimal totalPrice, ActivityBasicDTO activity) {
        this.id = id;
        this.quantity = quantity;
        this.pricePerPerson = pricePerPerson;
        this.totalPrice = totalPrice;
        this.activity = activity;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(BigDecimal pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ActivityBasicDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivityBasicDTO activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "BookingActivityResponseDTO{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", activity=" + activity +
                '}';
    }
}
