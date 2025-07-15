package org.project.caribevibes.repository.hotel;

import org.project.caribevibes.entity.hotel.HotelReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos de reseñas de hoteles.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Repository
public interface HotelReviewRepository extends JpaRepository<HotelReview, Long> {

    /**
     * Encuentra todas las reseñas de un hotel específico.
     * 
     * @param hotelId ID del hotel
     * @param pageable Información de paginación
     * @return Página de reseñas
     */
    @Query("SELECT hr FROM HotelReview hr WHERE hr.hotel.id = :hotelId ORDER BY hr.createdAt DESC")
    Page<HotelReview> findByHotelIdOrderByCreatedAtDesc(@Param("hotelId") Long hotelId, Pageable pageable);

    /**
     * Encuentra todas las reseñas de un usuario específico.
     * 
     * @param userId ID del usuario
     * @param pageable Información de paginación
     * @return Página de reseñas
     */
    @Query("SELECT hr FROM HotelReview hr WHERE hr.user.id = :userId ORDER BY hr.createdAt DESC")
    Page<HotelReview> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Busca reseñas por texto en título o comentario.
     * 
     * @param searchTerm Término de búsqueda
     * @param pageable Información de paginación
     * @return Página de reseñas encontradas
     */
    @Query("SELECT hr FROM HotelReview hr WHERE " +
           "LOWER(hr.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(hr.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY hr.createdAt DESC")
    Page<HotelReview> searchByTitleOrComment(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Encuentra una reseña por ID y usuario (para validar permisos).
     * 
     * @param reviewId ID de la reseña
     * @param userId ID del usuario
     * @return Reseña opcional
     */
    @Query("SELECT hr FROM HotelReview hr WHERE hr.id = :reviewId AND hr.user.id = :userId")
    Optional<HotelReview> findByIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    /**
     * Verifica si un usuario ya ha reseñado una reserva específica.
     * 
     * @param bookingId ID de la reserva
     * @param userId ID del usuario
     * @return true si ya existe una reseña
     */
    @Query("SELECT COUNT(hr) > 0 FROM HotelReview hr WHERE hr.booking.id = :bookingId AND hr.user.id = :userId")
    boolean existsByBookingIdAndUserId(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    /**
     * Calcula el promedio de calificaciones de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Promedio de calificaciones
     */
    @Query("SELECT AVG(hr.rating) FROM HotelReview hr WHERE hr.hotel.id = :hotelId")
    BigDecimal calculateAverageRatingByHotelId(@Param("hotelId") Long hotelId);

    /**
     * Cuenta el total de reseñas de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Número total de reseñas
     */
    @Query("SELECT COUNT(hr) FROM HotelReview hr WHERE hr.hotel.id = :hotelId")
    Long countByHotelId(@Param("hotelId") Long hotelId);

    /**
     * Obtiene la distribución de calificaciones de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Lista con [rating, count] para cada calificación
     */
    @Query("SELECT hr.rating, COUNT(hr) FROM HotelReview hr WHERE hr.hotel.id = :hotelId GROUP BY hr.rating ORDER BY hr.rating")
    List<Object[]> getRatingDistributionByHotelId(@Param("hotelId") Long hotelId);

    /**
     * Encuentra reseñas recientes de un hotel.
     * 
     * @param hotelId ID del hotel
     * @param limit Número máximo de reseñas
     * @return Lista de reseñas recientes
     */
    @Query("SELECT hr FROM HotelReview hr WHERE hr.hotel.id = :hotelId ORDER BY hr.createdAt DESC")
    List<HotelReview> findRecentReviewsByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    /**
     * Verifica si un usuario puede reseñar un hotel específico.
     * 
     * @param userId ID del usuario
     * @param hotelId ID del hotel
     * @return true si puede reseñar
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user.id = :userId AND b.hotel.id = :hotelId " +
           "AND b.status = 'COMPLETED' AND b.id NOT IN " +
           "(SELECT hr.booking.id FROM HotelReview hr WHERE hr.user.id = :userId)")
    boolean canUserReviewHotel(@Param("userId") Long userId, @Param("hotelId") Long hotelId);

    /**
     * Elimina todas las reseñas de un usuario específico.
     * 
     * @param userId ID del usuario
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * Elimina todas las reseñas de un hotel específico.
     * 
     * @param hotelId ID del hotel
     */
    void deleteByHotelId(@Param("hotelId") Long hotelId);
}
