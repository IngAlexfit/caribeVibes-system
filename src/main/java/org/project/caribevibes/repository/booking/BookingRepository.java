package org.project.caribevibes.repository.booking;

import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.entity.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de reservas en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Booking, incluyendo filtros por usuario, fechas, estado
 * y análisis de ocupación hotelera.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Busca una reserva por su código de confirmación
     * 
     * @param confirmationCode Código de confirmación único
     * @return Optional con la reserva encontrada o vacío si no existe
     */
    Optional<Booking> findByConfirmationCode(String confirmationCode);

    /**
     * Obtiene todas las reservas de un usuario específico
     * 
     * @param user Usuario del cual obtener las reservas
     * @return Lista de reservas del usuario
     */
    List<Booking> findByUserOrderByBookingDateDesc(User user);

    /**
     * Obtiene todas las reservas de un usuario por ID
     * 
     * @param userId ID del usuario
     * @return Lista de reservas del usuario ordenadas por fecha de reserva descendente
     */
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

    /**
     * Obtiene reservas de un usuario por estado
     * 
     * @param userId ID del usuario
     * @param status Estado de la reserva
     * @return Lista de reservas del usuario con el estado especificado
     */
    List<Booking> findByUserIdAndStatus(Long userId, Booking.BookingStatus status);

    /**
     * Obtiene reservas activas de un usuario (confirmadas, pendientes, check-in)
     * 
     * @param userId ID del usuario
     * @return Lista de reservas activas del usuario
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "AND b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
           "ORDER BY b.checkInDate ASC")
    List<Booking> findActiveBookingsByUser(@Param("userId") Long userId);

    /**
     * Obtiene todas las reservas de un hotel específico
     * 
     * @param hotel Hotel del cual obtener las reservas
     * @return Lista de reservas del hotel
     */
    List<Booking> findByHotelOrderByCheckInDateDesc(Hotel hotel);

    /**
     * Obtiene reservas de un hotel por ID
     * 
     * @param hotelId ID del hotel
     * @return Lista de reservas del hotel
     */
    List<Booking> findByHotelIdOrderByCheckInDateDesc(Long hotelId);

    /**
     * Obtiene reservas por estado específico
     * 
     * @param status Estado de la reserva
     * @return Lista de reservas con el estado especificado
     */
    List<Booking> findByStatusOrderByBookingDateDesc(Booking.BookingStatus status);

    /**
     * Obtiene reservas en un rango de fechas de check-in
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de reservas en el rango de fechas
     */
    List<Booking> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene reservas en un rango de fechas de check-out
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de reservas en el rango de fechas
     */
    List<Booking> findByCheckOutDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Verifica conflictos de disponibilidad para un tipo de habitación en fechas específicas
     * 
     * @param roomTypeId ID del tipo de habitación
     * @param checkIn Fecha de check-in
     * @param checkOut Fecha de check-out
     * @return Lista de reservas que se superponen con las fechas especificadas
     */
    @Query("SELECT b FROM Booking b WHERE b.roomType.id = :roomTypeId " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)")
    List<Booking> findOverlappingBookings(@Param("roomTypeId") Long roomTypeId,
                                        @Param("checkIn") LocalDate checkIn,
                                        @Param("checkOut") LocalDate checkOut);

    /**
     * Cuenta reservas confirmadas para un tipo de habitación en fechas específicas
     * 
     * @param roomTypeId ID del tipo de habitación
     * @param checkIn Fecha de check-in
     * @param checkOut Fecha de check-out
     * @return Número de habitaciones reservadas en el período
     */
    @Query("SELECT SUM(b.numberOfRooms) FROM Booking b WHERE b.roomType.id = :roomTypeId " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)")
    Integer countReservedRoomsInPeriod(@Param("roomTypeId") Long roomTypeId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut);

    /**
     * Obtiene reservas por múltiples criterios
     * 
     * @param userId ID del usuario (puede ser null)
     * @param hotelId ID del hotel (puede ser null)
     * @param status Estado de la reserva (puede ser null)
     * @param startDate Fecha de inicio del rango (puede ser null)
     * @param endDate Fecha de fin del rango (puede ser null)
     * @return Lista de reservas que cumplen los criterios
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "(:userId IS NULL OR b.user.id = :userId) AND " +
           "(:hotelId IS NULL OR b.hotel.id = :hotelId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:startDate IS NULL OR b.checkInDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.checkOutDate <= :endDate) " +
           "ORDER BY b.bookingDate DESC")
    List<Booking> findByMultipleCriteria(@Param("userId") Long userId,
                                       @Param("hotelId") Long hotelId,
                                       @Param("status") Booking.BookingStatus status,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /**
     * Obtiene estadísticas de ingresos por hotel en un período
     * 
     * @param hotelId ID del hotel
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Ingresos totales del hotel en el período
     */
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.hotel.id = :hotelId " +
           "AND b.status IN ('CONFIRMED', 'COMPLETED', 'CHECKED_IN', 'CHECKED_OUT') " +
           "AND b.checkInDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueByHotelInPeriod(@Param("hotelId") Long hotelId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    /**
     * Cuenta reservas por estado en un período
     * 
     * @param status Estado de la reserva
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Número de reservas con el estado especificado
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate")
    long countBookingsByStatusInPeriod(@Param("status") Booking.BookingStatus status,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Obtiene reservas próximas a vencer (check-in en los próximos días)
     * 
     * @param days Número de días hacia adelante
     * @return Lista de reservas próximas
     */
    @Query("SELECT b FROM Booking b WHERE b.isActive = true AND b.status IN ('PENDING', 'CONFIRMED') AND " +
            "b.checkInDate BETWEEN CURRENT_DATE AND :endDate")
    List<Booking> findUpcomingBookings(@Param("endDate") LocalDate endDate);

    /**
     * Obtiene reservas que requieren check-out hoy
     * 
     * @return Lista de reservas para check-out hoy
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'CHECKED_IN' " +
           "AND b.checkOutDate = CURRENT_DATE")
    List<Booking> findBookingsForCheckoutToday();

    /**
     * Obtiene las reservas más recientes de un usuario
     * 
     * @param userId ID del usuario
     * @param limit Número máximo de reservas a retornar
     * @return Lista de reservas más recientes
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "ORDER BY b.bookingDate DESC")
    List<Booking> findRecentBookingsByUser(@Param("userId") Long userId, 
                                         @Param("limit") int limit);

    /**
     * Calcula la ocupación promedio de un hotel en un período
     * 
     * @param hotelId ID del hotel
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Porcentaje de ocupación promedio
     */
    @Query("SELECT (COUNT(b) * 100.0 / " +
           "(SELECT SUM(rt.totalRooms) FROM RoomType rt WHERE rt.hotel.id = :hotelId)) " +
           "FROM Booking b WHERE b.hotel.id = :hotelId " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT') " +
           "AND b.checkInDate BETWEEN :startDate AND :endDate")
    Double calculateOccupancyRate(@Param("hotelId") Long hotelId,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    /**
     * Obtiene todas las reservas activas paginadas
     * 
     * @param pageable Configuración de paginación
     * @return Página de reservas activas
     */
    Page<Booking> findAllByIsActiveTrue(Pageable pageable);

    /**
     * Obtiene reservas de un usuario paginadas
     * 
     * @param userId ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de reservas del usuario
     */
    Page<Booking> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    /**
     * Obtiene reservas por estado paginadas
     * 
     * @param status Estado de la reserva
     * @param pageable Configuración de paginación
     * @return Página de reservas con el estado especificado
     */
    Page<Booking> findByStatusAndIsActiveTrue(Booking.BookingStatus status, Pageable pageable);

    /**
     * Obtiene reservas en un rango de fechas de check-in paginadas
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @param pageable Configuración de paginación
     * @return Página de reservas en el rango de fechas
     */
    Page<Booking> findByCheckInDateBetweenAndIsActiveTrue(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Busca una reserva activa por su ID
     * 
     * @param id ID de la reserva
     * @return Optional con la reserva encontrada o vacío si no existe
     */
    Optional<Booking> findByIdAndIsActiveTrue(Long id);

    /**
     * Busca una reserva activa por su código de confirmación
     * 
     * @param confirmationCode Código de confirmación único
     * @return Optional con la reserva encontrada o vacío si no existe
     */
    Optional<Booking> findByConfirmationCodeAndIsActiveTrue(String confirmationCode);

    /**
     * Obtiene reservas en un rango de fechas con estado específico paginadas
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @param status Estado de la reserva
     * @param pageable Configuración de paginación
     * @return Página de reservas que cumplen los criterios
     */
    Page<Booking> findByCheckInDateBetweenAndStatusAndIsActiveTrue(
            LocalDate startDate, LocalDate endDate, Booking.BookingStatus status, Pageable pageable);

    /**
     * Cuenta el número de habitaciones reservadas para un tipo de habitación en un período específico
     * 
     * @param roomTypeId ID del tipo de habitación
     * @param checkInDate Fecha de check-in
     * @param checkOutDate Fecha de check-out
     * @return Número de habitaciones reservadas
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.roomType.id = :roomTypeId " +
           "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN')")
    Integer countBookedRooms(@Param("roomTypeId") Long roomTypeId,
                           @Param("checkInDate") LocalDate checkInDate,
                           @Param("checkOutDate") LocalDate checkOutDate);

}