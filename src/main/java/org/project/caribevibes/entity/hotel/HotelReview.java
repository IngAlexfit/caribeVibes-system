package org.project.caribevibes.entity.hotel;

import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reseña de hotel realizada por un usuario.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Entity
@Table(name = "hotel_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "guest_name", length = 200)
    private String guestName;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "is_editable")
    @Builder.Default
    private Boolean editable = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (reviewDate == null) {
            reviewDate = LocalDateTime.now();
        }
        if (booking != null) {
            checkInDate = booking.getCheckInDate();
            checkOutDate = booking.getCheckOutDate();
        }
        if (user != null) {
            guestName = user.getFirstName() + " " + 
                       (user.getLastName() != null && !user.getLastName().isEmpty() ? 
                        user.getLastName().substring(0, 1) + "." : "");
        }
    }

    public boolean isEditable() {
        return editable != null ? editable : true;
    }
}
