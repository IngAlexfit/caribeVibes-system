package org.project.caribevibes.controller.hotel;

import org.project.caribevibes.dto.hotel.CreateHotelReviewDTO;
import org.project.caribevibes.dto.hotel.UpdateHotelReviewDTO;
import org.project.caribevibes.dto.hotel.HotelReviewResponseDTO;
import org.project.caribevibes.dto.hotel.HotelReviewStatsDTO;
import org.project.caribevibes.dto.hotel.ReviewableBookingDTO;
import org.project.caribevibes.entity.hotel.HotelReview;
import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.service.hotel.HotelReviewService;
import org.project.caribevibes.service.booking.BookingService;
import org.project.caribevibes.service.auth.AuthService;
import org.project.caribevibes.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones de reseñas de hoteles.
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * la gestión de reseñas y calificaciones de hoteles por parte de usuarios.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/hotel-reviews")
@CrossOrigin(origins = "*")
public class HotelReviewController {

    private static final Logger logger = LoggerFactory.getLogger(HotelReviewController.class);

    @Autowired
    private HotelReviewService reviewService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AuthService authService;

    /**
     * Crea una nueva reseña de hotel.
     * 
     * @param createReviewDTO Datos de la reseña a crear
     * @return ResponseEntity con la reseña creada
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER')")
    public ResponseEntity<HotelReviewResponseDTO> createReview(@Valid @RequestBody CreateHotelReviewDTO createReviewDTO) {
        logger.info("Creando nueva reseña para reserva ID: {}", createReviewDTO.getBookingId());

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        // Obtener la reserva
        Booking booking = bookingService.findBookingById(createReviewDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", createReviewDTO.getBookingId()));

        // Crear la entidad de reseña
        HotelReview review = HotelReview.builder()
                .user(currentUser)
                .hotel(booking.getHotel())
                .booking(booking)
                .rating(createReviewDTO.getRating())
                .title(createReviewDTO.getTitle())
                .comment(createReviewDTO.getComment())
                .build();

        HotelReview savedReview = reviewService.createReview(review);
        HotelReviewResponseDTO responseDTO = convertToResponseDTO(savedReview);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Actualiza una reseña existente.
     * 
     * @param reviewId ID de la reseña a actualizar
     * @param updateReviewDTO Datos actualizados
     * @return ResponseEntity con la reseña actualizada
     */
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER')")
    public ResponseEntity<HotelReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateHotelReviewDTO updateReviewDTO) {
        
        logger.info("Actualizando reseña ID: {}", reviewId);

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        // Crear entidad con los datos actualizados
        HotelReview updatedReview = new HotelReview();
        updatedReview.setRating(updateReviewDTO.getRating());
        updatedReview.setTitle(updateReviewDTO.getTitle());
        updatedReview.setComment(updateReviewDTO.getComment());

        HotelReview savedReview = reviewService.updateReview(reviewId, currentUser.getId(), updatedReview);
        HotelReviewResponseDTO responseDTO = convertToResponseDTO(savedReview);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Elimina una reseña.
     * 
     * @param reviewId ID de la reseña a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long reviewId) {
        logger.info("Eliminando reseña ID: {}", reviewId);

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        reviewService.deleteReview(reviewId, currentUser.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Reseña eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las reseñas de un hotel.
     * 
     * @param hotelId ID del hotel
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reseñas
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<Page<HotelReviewResponseDTO>> getHotelReviews(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo reseñas para hotel ID: {}", hotelId);

        Pageable pageable = PageRequest.of(page, size);
        Page<HotelReview> reviews = reviewService.getHotelReviews(hotelId, pageable);
        Page<HotelReviewResponseDTO> responseDTOs = reviews.map(this::convertToResponseDTO);

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Obtiene las estadísticas de reseñas de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return ResponseEntity con estadísticas de reseñas
     */
    @GetMapping("/hotel/{hotelId}/stats")
    public ResponseEntity<HotelReviewStatsDTO> getHotelReviewStats(@PathVariable Long hotelId) {
        logger.debug("Obteniendo estadísticas de reseñas para hotel ID: {}", hotelId);

        HotelReviewStatsDTO stats = new HotelReviewStatsDTO();
        stats.setHotelId(hotelId);

        // Calcular rating promedio
        BigDecimal avgRating = reviewService.calculateHotelAverageRating(hotelId);
        stats.setAverageRating(avgRating != null ? avgRating.doubleValue() : 0.0);

        // Contar total de reseñas
        Long totalReviews = reviewService.countHotelReviews(hotelId);
        stats.setTotalReviews(totalReviews);

        // Obtener distribución de calificaciones
        int[] distribution = reviewService.getRatingDistribution(hotelId);
        stats.setOneStar(distribution[0]);
        stats.setTwoStars(distribution[1]);
        stats.setThreeStars(distribution[2]);
        stats.setFourStars(distribution[3]);
        stats.setFiveStars(distribution[4]);

        // Calcular porcentajes
        if (totalReviews > 0) {
            stats.setOneStarPercent((distribution[0] * 100.0) / totalReviews);
            stats.setTwoStarsPercent((distribution[1] * 100.0) / totalReviews);
            stats.setThreeStarsPercent((distribution[2] * 100.0) / totalReviews);
            stats.setFourStarsPercent((distribution[3] * 100.0) / totalReviews);
            stats.setFiveStarsPercent((distribution[4] * 100.0) / totalReviews);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene las reseñas del usuario actual.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reseñas del usuario
     */
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER')")
    public ResponseEntity<Page<HotelReviewResponseDTO>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        Pageable pageable = PageRequest.of(page, size);
        Page<HotelReview> reviews = reviewService.getUserReviews(currentUser.getId(), pageable);
        Page<HotelReviewResponseDTO> responseDTOs = reviews.map(this::convertToResponseDTO);

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Obtiene las reservas que pueden ser reseñadas por el usuario actual.
     * 
     * @return ResponseEntity con lista de reservas reseñables
     */
    @GetMapping("/reviewable-bookings")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER')")
    public ResponseEntity<List<ReviewableBookingDTO>> getReviewableBookings() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        List<Booking> reviewableBookings = reviewService.getReviewableBookings(currentUser.getId());
        List<ReviewableBookingDTO> responseDTOs = reviewableBookings.stream()
                .map(this::convertToReviewableBookingDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Busca reseñas por texto.
     * 
     * @param searchTerm Término de búsqueda
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reseñas encontradas
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HotelReviewResponseDTO>> searchReviews(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Buscando reseñas con término: {}", searchTerm);

        Pageable pageable = PageRequest.of(page, size);
        Page<HotelReview> reviews = reviewService.searchReviews(searchTerm, pageable);
        Page<HotelReviewResponseDTO> responseDTOs = reviews.map(this::convertToResponseDTO);

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Obtiene todas las reseñas de un usuario específico.
     * 
     * @param userId ID del usuario
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reseñas del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HotelReviewResponseDTO>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo reseñas del usuario: {}", userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<HotelReview> reviews = reviewService.getReviewsByUser(userId, pageable);
        List<HotelReviewResponseDTO> responseDTOs = reviews.getContent()
            .stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Convierte una entidad HotelReview a DTO de respuesta.
     * 
     * @param review Entidad HotelReview
     * @return DTO de respuesta
     */
    private HotelReviewResponseDTO convertToResponseDTO(HotelReview review) {
        HotelReviewResponseDTO dto = new HotelReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setEditable(review.isEditable());

        // Información del usuario (limitada por privacidad)
        if (review.getUser() != null) {
            dto.setUserId(review.getUser().getId()); // Para validaciones de permisos en frontend
            dto.setUserFirstName(review.getUser().getFirstName());
            String lastName = review.getUser().getLastName();
            dto.setUserLastInitial(lastName != null && !lastName.isEmpty() ? lastName.substring(0, 1) + "." : "");
        }

        // Información del hotel
        if (review.getHotel() != null) {
            dto.setHotelId(review.getHotel().getId());
            dto.setHotelName(review.getHotel().getName());
        }

        // Información de la reserva
        if (review.getBooking() != null) {
            dto.setBookingId(review.getBooking().getId());
            dto.setBookingDate(review.getBooking().getBookingDate());
        }

        return dto;
    }

    /**
     * Convierte una entidad Booking a DTO de reserva reseñable.
     * 
     * @param booking Entidad Booking
     * @return DTO de reserva reseñable
     */
    private ReviewableBookingDTO convertToReviewableBookingDTO(Booking booking) {
        ReviewableBookingDTO dto = new ReviewableBookingDTO();
        dto.setBookingId(booking.getId());
        dto.setConfirmationCode(booking.getConfirmationCode());
        dto.setBookingDate(booking.getBookingDate());
        dto.setCheckInDate(booking.getCheckInDate().atStartOfDay());
        dto.setCheckOutDate(booking.getCheckOutDate().atStartOfDay());
        dto.setNumGuests(booking.getNumGuests());
        dto.setNumRooms(booking.getNumRooms());
        dto.setTotalPrice(booking.getTotalPrice().doubleValue());

        // Información del hotel
        if (booking.getHotel() != null) {
            dto.setHotelId(booking.getHotel().getId());
            dto.setHotelName(booking.getHotel().getName());
            dto.setHotelImageUrl(booking.getHotel().getImageUrl());
            dto.setHotelStars(booking.getHotel().getStars());
        }

        return dto;
    }
}
