package org.project.caribevibes.service.hotel;

import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.hotel.HotelReview;
import org.project.caribevibes.repository.booking.BookingRepository;
import org.project.caribevibes.repository.hotel.HotelReviewRepository;
import org.project.caribevibes.exception.ResourceNotFoundException;
import org.project.caribevibes.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para gestión de reseñas de hoteles.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Service
@Transactional
public class HotelReviewService {

    private static final Logger logger = LoggerFactory.getLogger(HotelReviewService.class);

    @Autowired
    private HotelReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Crea una nueva reseña de hotel.
     * 
     * @param review Entidad de reseña a crear
     * @return Reseña creada
     * @throws BusinessException si ya existe una reseña para esta reserva
     */
    public HotelReview createReview(HotelReview review) {
        logger.info("Creando nueva reseña para hotel ID: {} por usuario ID: {}", 
                   review.getHotel().getId(), review.getUser().getId());

        // Verificar que no existe una reseña previa para esta reserva
        if (reviewRepository.existsByBookingIdAndUserId(review.getBooking().getId(), review.getUser().getId())) {
            throw new BusinessException("Ya existe una reseña para esta reserva");
        }

        // Verificar que la reserva esté completada o que haya pasado la fecha de check-out
        Booking booking = review.getBooking();
        boolean isReviewable = Booking.BookingStatus.COMPLETED.equals(booking.getStatus()) ||
                              (Booking.BookingStatus.CHECKED_OUT.equals(booking.getStatus()) && 
                               booking.getCheckOutDate().isBefore(LocalDate.now()));
        
        if (!isReviewable) {
            throw new BusinessException("Solo se pueden reseñar reservas completadas");
        }

        return reviewRepository.save(review);
    }

    /**
     * Actualiza una reseña existente.
     * 
     * @param reviewId ID de la reseña
     * @param userId ID del usuario (para validar permisos)
     * @param updatedReview Datos actualizados
     * @return Reseña actualizada
     * @throws ResourceNotFoundException si no se encuentra la reseña
     */
    public HotelReview updateReview(Long reviewId, Long userId, HotelReview updatedReview) {
        logger.info("Actualizando reseña ID: {} por usuario ID: {}", reviewId, userId);

        // SEGURIDAD: findByIdAndUserId garantiza que solo el propietario pueda actualizar su reseña
        HotelReview existingReview = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña", "id", reviewId));

        // Actualizar campos
        existingReview.setRating(updatedReview.getRating());
        existingReview.setTitle(updatedReview.getTitle());
        existingReview.setComment(updatedReview.getComment());

        return reviewRepository.save(existingReview);
    }

    /**
     * Elimina una reseña.
     * 
     * @param reviewId ID de la reseña
     * @param userId ID del usuario (para validar permisos)
     * @throws ResourceNotFoundException si no se encuentra la reseña
     */
    public void deleteReview(Long reviewId, Long userId) {
        logger.info("Eliminando reseña ID: {} por usuario ID: {}", reviewId, userId);

        // SEGURIDAD: findByIdAndUserId garantiza que solo el propietario pueda eliminar su reseña
        HotelReview review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña", "id", reviewId));

        reviewRepository.delete(review);
    }

    /**
     * Obtiene todas las reseñas de un hotel.
     * 
     * @param hotelId ID del hotel
     * @param pageable Información de paginación
     * @return Página de reseñas
     */
    @Transactional(readOnly = true)
    public Page<HotelReview> getHotelReviews(Long hotelId, Pageable pageable) {
        logger.debug("Obteniendo reseñas para hotel ID: {}", hotelId);
        return reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId, pageable);
    }

    /**
     * Obtiene las reseñas de un usuario.
     * 
     * @param userId ID del usuario
     * @param pageable Información de paginación
     * @return Página de reseñas
     */
    @Transactional(readOnly = true)
    public Page<HotelReview> getUserReviews(Long userId, Pageable pageable) {
        logger.debug("Obteniendo reseñas para usuario ID: {}", userId);
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Obtiene las reseñas de un usuario (alias para getUserReviews).
     * 
     * @param userId ID del usuario
     * @param pageable Información de paginación
     * @return Página de reseñas
     */
    @Transactional(readOnly = true)
    public Page<HotelReview> getReviewsByUser(Long userId, Pageable pageable) {
        return getUserReviews(userId, pageable);
    }

    /**
     * Busca reseñas por texto.
     * 
     * @param searchTerm Término de búsqueda
     * @param pageable Información de paginación
     * @return Página de reseñas encontradas
     */
    @Transactional(readOnly = true)
    public Page<HotelReview> searchReviews(String searchTerm, Pageable pageable) {
        logger.debug("Buscando reseñas con término: {}", searchTerm);
        return reviewRepository.searchByTitleOrComment(searchTerm, pageable);
    }

    /**
     * Calcula el promedio de calificaciones de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Promedio de calificaciones
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateHotelAverageRating(Long hotelId) {
        return reviewRepository.calculateAverageRatingByHotelId(hotelId);
    }

    /**
     * Cuenta el total de reseñas de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Número total de reseñas
     */
    @Transactional(readOnly = true)
    public Long countHotelReviews(Long hotelId) {
        return reviewRepository.countByHotelId(hotelId);
    }

    /**
     * Obtiene la distribución de calificaciones de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Array con conteos [1-star, 2-star, 3-star, 4-star, 5-star]
     */
    @Transactional(readOnly = true)
    public int[] getRatingDistribution(Long hotelId) {
        List<Object[]> distribution = reviewRepository.getRatingDistributionByHotelId(hotelId);
        int[] result = new int[5]; // Para calificaciones 1-5

        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            if (rating >= 1 && rating <= 5) {
                result[rating - 1] = count.intValue();
            }
        }

        return result;
    }

    /**
     * Obtiene las reservas que pueden ser reseñadas por un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de reservas reseñables
     */
    @Transactional(readOnly = true)
    public List<Booking> getReviewableBookings(Long userId) {
        logger.debug("Obteniendo reservas reseñables para usuario ID: {}", userId);
        
        // Buscar reservas completadas que no han sido reseñadas
        return bookingRepository.findCompletedBookingsWithoutReviews(userId);
    }

    /**
     * Verifica si un usuario puede reseñar un hotel específico.
     * 
     * @param userId ID del usuario
     * @param hotelId ID del hotel
     * @return true si puede reseñar
     */
    @Transactional(readOnly = true)
    public boolean canUserReviewHotel(Long userId, Long hotelId) {
        return reviewRepository.canUserReviewHotel(userId, hotelId);
    }

    /**
     * Obtiene reseñas recientes de un hotel.
     * 
     * @param hotelId ID del hotel
     * @param limit Número máximo de reseñas
     * @return Lista de reseñas recientes
     */
    @Transactional(readOnly = true)
    public List<HotelReview> getRecentHotelReviews(Long hotelId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.findRecentReviewsByHotelId(hotelId, pageable);
    }
}
