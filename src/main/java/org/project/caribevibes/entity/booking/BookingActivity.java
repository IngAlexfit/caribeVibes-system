package org.project.caribevibes.entity.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.project.caribevibes.entity.destination.Activity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "booking_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString(exclude = {"booking", "activity"})
@EqualsAndHashCode(exclude = {"booking", "activity"})
public class BookingActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    @Getter @Setter private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "scheduled_date", nullable = false)
    @NotNull(message = "La fecha programada es requerida")
    private LocalDate scheduledDate;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "Debe haber al menos 1 participante")
    private Integer quantity;

    @Column(name = "price_per_person", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio por persona es requerido")
    private BigDecimal pricePerPerson;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio total es requerido")
    @Getter @Setter private BigDecimal totalPrice;

    @Column(name = "is_active", nullable = false) // Changed column name
    @Builder.Default
    private Boolean isActive = true; // Field name is isActive

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ActivityStatus status = ActivityStatus.SCHEDULED;

    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (quantity != null && pricePerPerson != null) {
            this.totalPrice = pricePerPerson.multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    public enum ActivityStatus {
        SCHEDULED("Programada"),
        COMPLETED("Completada"),
        CANCELLED("Cancelada");

        private final String displayName;

        ActivityStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}