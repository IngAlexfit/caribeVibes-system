package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para agregar una actividad a una reserva
 */
public class AddActivityToBookingRequestDTO {
    
    @NotNull(message = "El ID de la actividad es obligatorio")
    private Long activityId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
    
    // Constructores
    public AddActivityToBookingRequestDTO() {}
    
    public AddActivityToBookingRequestDTO(Long activityId, Integer quantity) {
        this.activityId = activityId;
        this.quantity = quantity;
    }
    
    // Getters y Setters
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "AddActivityToBookingRequestDTO{" +
                "activityId=" + activityId +
                ", quantity=" + quantity +
                '}';
    }
}
