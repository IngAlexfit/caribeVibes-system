package org.project.caribevibes.repository.booking;

import org.project.caribevibes.entity.booking.BookingActivity;
import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.destination.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de actividades en reservas del sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad BookingActivity, incluyendo filtros por reserva, actividad
 * y fecha programada.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Repository
public interface BookingActivityRepository extends JpaRepository<BookingActivity, Long> {

    /**
     * Obtiene todas las actividades de una reserva específica
     * 
     * @param booking Reserva de la cual obtener las actividades
     * @return Lista de actividades de la reserva
     */
    List<BookingActivity> findByBookingOrderByScheduledDateAsc(Booking booking);

    /**
     * Obtiene todas las actividades de una reserva por ID
     * 
     * @param bookingId ID de la reserva
     * @return Lista de actividades de la reserva ordenadas por fecha programada
     */
    List<BookingActivity> findByBookingIdOrderByScheduledDateAsc(Long bookingId);

    /**
     * Obtiene actividades por estado específico
     * 
     * @param status Estado de la actividad
     * @return Lista de actividades con el estado especificado
     */
    List<BookingActivity> findByStatusOrderByScheduledDateAsc(BookingActivity.ActivityStatus status);

    /**
     * Obtiene actividades programadas para una fecha específica
     * 
     * @param scheduledDate Fecha programada
     * @return Lista de actividades programadas para esa fecha
     */
    List<BookingActivity> findByScheduledDateOrderByScheduledDateAsc(LocalDate scheduledDate);

    /**
     * Obtiene actividades de una actividad específica
     * 
     * @param activity Actividad base
     * @return Lista de reservas de esa actividad
     */
    List<BookingActivity> findByActivityOrderByScheduledDateAsc(Activity activity);

    /**
     * Obtiene actividades de una actividad por ID
     * 
     * @param activityId ID de la actividad
     * @return Lista de reservas de esa actividad
     */
    List<BookingActivity> findByActivityIdOrderByScheduledDateAsc(Long activityId);

    /**
     * Obtiene actividades programadas en un rango de fechas
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de actividades programadas en el rango
     */
    List<BookingActivity> findByScheduledDateBetweenOrderByScheduledDateAsc(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene actividades de un usuario específico
     * 
     * @param userId ID del usuario
     * @return Lista de actividades reservadas por el usuario
     */
    @Query("SELECT ba FROM BookingActivity ba WHERE ba.booking.user.id = :userId " +
           "ORDER BY ba.scheduledDate ASC")
    List<BookingActivity> findByUserId(@Param("userId") Long userId);

    /**
     * Obtiene actividades próximas de un usuario
     * 
     * @param userId ID del usuario
     * @param days Número de días hacia adelante
     * @return Lista de actividades próximas del usuario
     */
    @Query("SELECT ba FROM BookingActivity ba WHERE ba.booking.user.id = :userId " +
           "AND ba.scheduledDate BETWEEN CURRENT_DATE AND :endDate " +
           "ORDER BY ba.scheduledDate ASC")
    List<BookingActivity> findUpcomingByUserId(@Param("userId") Long userId, @Param("endDate") LocalDate endDate);
    
    /**
     * Busca una actividad de reserva por su ID y que esté activa.
     *
     * @param id El ID de la actividad de reserva.
     * @return Un Optional que contiene la actividad de reserva si se encuentra y está activa, o vacío en caso contrario.
     */
    Optional<BookingActivity> findByIdAndIsActiveTrue(Long id);

    /**
     * Busca todas las actividades de reserva asociadas a una reserva específica y que estén activas.
     *
     * @param booking La reserva.
     * @return Una lista de actividades de reserva activas para la reserva dada.
     */
    List<BookingActivity> findByBookingAndIsActiveTrue(Booking booking);

    /**
     * Busca todas las actividades de reserva asociadas a una actividad específica y que estén activas.
     *
     * @param activity La actividad.
     * @return Una lista de actividades de reserva activas para la actividad dada.
     */
    List<BookingActivity> findByActivityAndIsActiveTrue(Activity activity);

    /**
     * Obtiene actividades programadas para hoy
     * 
     * @return Lista de actividades programadas para hoy
     */
    @Query("SELECT ba FROM BookingActivity ba WHERE ba.scheduledDate = CURRENT_DATE " +
           "AND ba.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY ba.scheduledDate ASC")
    List<BookingActivity> findTodaysActivities();

    /**
     * Cuenta participantes totales para una actividad en una fecha específica
     * 
     * @param activityId ID de la actividad
     * @param scheduledDate Fecha programada
     * @return Número total de participantes
     */
    @Query("SELECT SUM(ba.quantity) FROM BookingActivity ba " +
           "WHERE ba.activity.id = :activityId " +
           "AND ba.scheduledDate = :scheduledDate " +
           "AND ba.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS')")
    Integer countParticipantsByActivityAndDate(@Param("activityId") Long activityId,
                                             @Param("scheduledDate") LocalDate scheduledDate);

    /**
     * Verifica disponibilidad de una actividad para una fecha específica
     * 
     * @param activityId ID de la actividad
     * @param scheduledDate Fecha programada
     * @param requestedParticipants Número de participantes solicitados
     * @return true si hay capacidad disponible
     */
    @Query("SELECT CASE WHEN " +
           "(SELECT a.maxCapacity FROM Activity a WHERE a.id = :activityId) - " +
           "COALESCE((SELECT SUM(ba.quantity) FROM BookingActivity ba " +
           "WHERE ba.activity.id = :activityId AND ba.scheduledDate = :scheduledDate " +
           "AND ba.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS')), 0) " +
           ">= :requestedParticipants THEN true ELSE false END")
    boolean isActivityAvailable(@Param("activityId") Long activityId,
                              @Param("scheduledDate") LocalDate scheduledDate,
                              @Param("requestedParticipants") Integer requestedParticipants);

    /**
     * Obtiene actividades por múltiples criterios
     * 
     * @param bookingId ID de la reserva (puede ser null)
     * @param activityId ID de la actividad (puede ser null)
     * @param status Estado de la actividad (puede ser null)
     * @param startDate Fecha de inicio del rango (puede ser null)
     * @param endDate Fecha de fin del rango (puede ser null)
     * @return Lista de actividades que cumplen los criterios
     */
    @Query("SELECT ba FROM BookingActivity ba WHERE " +
           "(:bookingId IS NULL OR ba.booking.id = :bookingId) AND " +
           "(:activityId IS NULL OR ba.activity.id = :activityId) AND " +
           "(:status IS NULL OR ba.status = :status) AND " +
           "(:startDate IS NULL OR ba.scheduledDate >= :startDate) AND " +
           "(:endDate IS NULL OR ba.scheduledDate <= :endDate) " +
           "ORDER BY ba.scheduledDate ASC")
    List<BookingActivity> findByMultipleCriteria(@Param("bookingId") Long bookingId,
                                               @Param("activityId") Long activityId,
                                               @Param("status") BookingActivity.ActivityStatus status,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * Obtiene estadísticas de participación por actividad
     * 
     * @param activityId ID de la actividad
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Array con [total_sesiones, total_participantes, participantes_promedio]
     */
    @Query("SELECT COUNT(ba), SUM(ba.quantity), AVG(ba.quantity) " +
           "FROM BookingActivity ba WHERE ba.activity.id = :activityId " +
           "AND ba.scheduledDate BETWEEN :startDate AND :endDate " +
           "AND ba.status = 'COMPLETED'")
    Object[] getParticipationStatistics(@Param("activityId") Long activityId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /**
     * Obtiene actividades canceladas en un período
     * 
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Lista de actividades canceladas
     */
    @Query("SELECT ba FROM BookingActivity ba WHERE ba.status = 'CANCELLED' " +
           "AND ba.scheduledDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ba.scheduledDate DESC")
    List<BookingActivity> findCancelledActivitiesInPeriod(@Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    /**
     * Obtiene actividades más populares (con más participantes)
     * 
     * @param limit Número máximo de actividades a retornar
     * @return Lista de actividades más populares
     */
    @Query("SELECT ba.activity, COUNT(ba), SUM(ba.quantity) " +
           "FROM BookingActivity ba WHERE ba.status = 'COMPLETED' " +
           "GROUP BY ba.activity " +
           "ORDER BY SUM(ba.quantity) DESC")
    List<Object[]> findMostPopularActivities(@Param("limit") int limit);

    /**
     * Obtiene actividades activas de una reserva por ID
     * 
     * @param bookingId ID de la reserva
     * @return Lista de actividades activas de la reserva
     */
    List<BookingActivity> findByBookingIdAndIsActiveTrue(Long bookingId);

    
}
