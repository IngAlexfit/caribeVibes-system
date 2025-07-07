package org.project.caribevibes.service.booking;

import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.booking.BookingActivity;
import org.project.caribevibes.entity.destination.Activity;
import org.project.caribevibes.entity.hotel.Hotel;
import org.project.caribevibes.entity.hotel.RoomType;
import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.repository.booking.BookingRepository;
import org.project.caribevibes.repository.booking.BookingActivityRepository;
import org.project.caribevibes.repository.destination.ActivityRepository;
import org.project.caribevibes.repository.hotel.HotelRepository;
import org.project.caribevibes.repository.hotel.RoomTypeRepository;
import org.project.caribevibes.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de reservas y actividades de reservas.
 * 
 * Este servicio proporciona funcionalidades para:
 * - Gestión completa de reservas de hoteles (CRUD)
 * - Gestión de actividades asociadas a reservas
 * - Cálculo de precios y totales
 * - Validación de disponibilidad
 * - Seguimiento del estado de reservas
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Service
@Transactional
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingActivityRepository bookingActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Obtiene todas las reservas activas paginadas.
     * 
     * @param pageable Configuración de paginación
     * @return Página de reservas activas
     */
    @Transactional(readOnly = true)
    public Page<Booking> findAllActiveBookings(Pageable pageable) {
        logger.debug("Obteniendo todas las reservas activas - página: {}, tamaño: {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        return bookingRepository.findAllByIsActiveTrue(pageable);
    }

    /**
     * Obtiene las reservas de un usuario específico.
     * 
     * @param userId ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de reservas del usuario
     */
    @Transactional(readOnly = true)
    public Page<Booking> findBookingsByUser(Long userId, Pageable pageable) {
        logger.debug("Obteniendo reservas para usuario ID: {} - página: {}, tamaño: {}", 
                    userId, pageable.getPageNumber(), pageable.getPageSize());
        return bookingRepository.findByUserIdAndIsActiveTrue(userId, pageable);
    }

    /**
     * Obtiene las reservas por estado.
     * 
     * @param statusStr Estado de la reserva (PENDING, CONFIRMED, CANCELLED, COMPLETED)
     * @param pageable Configuración de paginación
     * @return Página de reservas con el estado especificado
     */
    @Transactional(readOnly = true)
    public Page<Booking> findBookingsByStatus(String statusStr, Pageable pageable) {
        logger.debug("Obteniendo reservas con estado: {} - página: {}, tamaño: {}", 
                    statusStr, pageable.getPageNumber(), pageable.getPageSize());
        Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusStr);
        return bookingRepository.findByStatusAndIsActiveTrue(status, pageable);
    }

    /**
     * Obtiene las reservas en un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de reservas en el rango de fechas
     */
    @Transactional(readOnly = true)
    public Page<Booking> findBookingsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.debug("Obteniendo reservas entre {} y {} - página: {}, tamaño: {}", 
                    startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());
        return bookingRepository.findByCheckInDateBetweenAndIsActiveTrue(startDate, endDate, pageable);
    }

    /**
     * Obtiene una reserva por su ID.
     * 
     * @param id ID de la reserva
     * @return Optional con la reserva encontrada o vacío si no existe
     */
    @Transactional(readOnly = true)
    public Optional<Booking> findBookingById(Long id) {
        logger.debug("Buscando reserva por ID: {}", id);
        return bookingRepository.findByIdAndIsActiveTrue(id);
    }

    /**
     * Crea una nueva reserva.
     * 
     * @param booking Reserva a crear
     * @return Reserva creada
     * @throws IllegalArgumentException si los datos de la reserva no son válidos
     */
    public Booking createBooking(Booking booking) {
        logger.info("Creando nueva reserva para usuario ID: {} y hotel ID: {}", 
                   booking.getUser().getId(), booking.getHotel().getId());

        // Validar que el usuario existe y está activo
        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Usuario no está activo");
        }

        // Validar que el hotel existe y está activo
        Hotel hotel = hotelRepository.findById(booking.getHotel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel no encontrado"));
        if (!hotel.getIsActive()) {
            throw new IllegalArgumentException("Hotel no está activo");
        }

        // Validar que el tipo de habitación existe y está activo
        RoomType roomType = roomTypeRepository.findById(booking.getRoomType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de habitación no encontrado"));
        if (!roomType.getIsActive()) {
            throw new IllegalArgumentException("Tipo de habitación no está activo");
        }

        // Validar disponibilidad de fechas
        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("La fecha de check-in debe ser anterior a la fecha de check-out");
        }

        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de check-in no puede ser en el pasado");
        }

        // Verificar disponibilidad de habitaciones
        boolean isAvailable = checkRoomAvailability(
                booking.getRoomType().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumRooms()
        );

        if (!isAvailable) {
            throw new IllegalArgumentException("No hay suficientes habitaciones disponibles para las fechas seleccionadas");
        }

        // Calcular el precio total
        BigDecimal totalPrice = calculateTotalPrice(booking);
        booking.setTotalPrice(totalPrice);

        // Establecer valores por defecto
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setIsActive(true); // Changed from setActive to setIsActive

        // Generar código de confirmación único
        booking.setConfirmationCode(generateConfirmationCode());

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Reserva creada exitosamente con ID: {} y código: {}", 
                   savedBooking.getId(), savedBooking.getConfirmationCode());
        
        return savedBooking;
    }

    /**
     * Actualiza una reserva existente.
     * 
     * @param id ID de la reserva a actualizar
     * @param bookingDetails Detalles actualizados de la reserva
     * @return Reserva actualizada o Optional vacío si no existe
     */
    public Optional<Booking> updateBooking(Long id, Booking bookingDetails) {
        logger.info("Actualizando reserva con ID: {}", id);
        
        return bookingRepository.findByIdAndIsActiveTrue(id)
                .map(existingBooking -> {
                    // Solo permitir actualizar ciertos campos según el estado
                    if (existingBooking.getStatus() != Booking.BookingStatus.CANCELLED &&
                        existingBooking.getStatus() != Booking.BookingStatus.COMPLETED) {

                        existingBooking.setCheckInDate(bookingDetails.getCheckInDate());
                        existingBooking.setCheckOutDate(bookingDetails.getCheckOutDate());
                        existingBooking.setNumGuests(bookingDetails.getNumGuests());
                        existingBooking.setNumRooms(bookingDetails.getNumRooms());
                        existingBooking.setSpecialRequests(bookingDetails.getSpecialRequests());
                        
                        // Recalcular precio total
                        BigDecimal newTotalPrice = calculateTotalPrice(existingBooking);
                        existingBooking.setTotalPrice(newTotalPrice);
                        
                        Booking updatedBooking = bookingRepository.save(existingBooking);
                        logger.info("Reserva actualizada exitosamente: {}", updatedBooking.getConfirmationCode());
                        return updatedBooking;
                    } else {
                        logger.warn("No se puede actualizar la reserva {} porque está en estado: {}", 
                                   id, existingBooking.getStatus());
                        return existingBooking;
                    }
                });
    }

    /**
     * Confirma una reserva.
     * 
     * @param id ID de la reserva a confirmar
     * @return true si se confirmó exitosamente, false si no se pudo confirmar
     */
    public boolean confirmBooking(Long id) {
        logger.info("Confirmando reserva con ID: {}", id);
        
        return bookingRepository.findByIdAndIsActiveTrue(id)
                .map(booking -> {
                    if (booking.getStatus() == Booking.BookingStatus.PENDING) {
                        booking.setStatus(Booking.BookingStatus.CONFIRMED);
                        bookingRepository.save(booking);
                        logger.info("Reserva confirmada exitosamente: {}", booking.getConfirmationCode());
                        return true;
                    } else {
                        logger.warn("No se puede confirmar la reserva {} porque está en estado: {}", 
                                   id, booking.getStatus());
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Cancela una reserva.
     * 
     * @param id ID de la reserva a cancelar
     * @return true si se canceló exitosamente, false si no se pudo cancelar
     */
    public boolean cancelBooking(Long id) {
        logger.info("Cancelando reserva con ID: {}", id);
        
        return bookingRepository.findByIdAndIsActiveTrue(id)
                .map(booking -> {
                    if (booking.getStatus() != Booking.BookingStatus.COMPLETED &&
                        booking.getStatus() != Booking.BookingStatus.CANCELLED) {
                        booking.setStatus(Booking.BookingStatus.CANCELLED);
                        booking.setIsActive(false);
                        bookingRepository.save(booking);
                        logger.info("Reserva cancelada exitosamente: {}", booking.getConfirmationCode());
                        return true;
                    } else {
                        logger.warn("No se puede cancelar la reserva {} porque está en estado: {}", 
                                   id, booking.getStatus());
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Completa una reserva (marca como completada).
     * 
     * @param id ID de la reserva a completar
     * @return true si se completó exitosamente, false si no se pudo completar
     */
    public boolean completeBooking(Long id) {
        logger.info("Completando reserva con ID: {}", id);
        
        return bookingRepository.findByIdAndIsActiveTrue(id)
                .map(booking -> {
                    if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
                        booking.setStatus(Booking.BookingStatus.COMPLETED);
                        bookingRepository.save(booking);
                        logger.info("Reserva completada exitosamente: {}", booking.getConfirmationCode());
                        return true;
                    } else {
                        logger.warn("No se puede completar la reserva {} porque está en estado: {}", 
                                   id, booking.getStatus());
                        return false;
                    }
                })
                .orElse(false);
    }
    
    /**
     * Agrega una actividad a una reserva.
     * 
     * @param bookingId ID de la reserva
     * @param activityId ID de la actividad
     * @param quantity Cantidad de personas para la actividad
     * @return BookingActivity creada
     * @throws IllegalArgumentException si la reserva o actividad no existen
     */
    public BookingActivity addActivityToBooking(Long bookingId, Long activityId, Integer quantity) {
        logger.info("Agregando actividad ID: {} a reserva ID: {} para {} personas", 
                   activityId, bookingId, quantity);

        Booking booking = bookingRepository.findByIdAndIsActiveTrue(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Activity activity = activityRepository.findByIdAndIsAvailableTrue(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

        BookingActivity bookingActivity = new BookingActivity();
        bookingActivity.setBooking(booking);
        bookingActivity.setActivity(activity);
        bookingActivity.setQuantity(quantity);
        bookingActivity.setPricePerPerson(activity.getPrice());
        bookingActivity.setTotalPrice(activity.getPrice().multiply(BigDecimal.valueOf(quantity)));
        bookingActivity.setScheduledDate(booking.getCheckInDate()); // Establecer la fecha programada
        bookingActivity.setIsActive(true); // Establecemos la actividad como activa

        BookingActivity savedBookingActivity = bookingActivityRepository.save(bookingActivity);
        
        // Actualizar el precio total de la reserva
        updateBookingTotalPrice(booking);
        
        logger.info("Actividad agregada exitosamente con ID: {}", savedBookingActivity.getId());
        return savedBookingActivity;
    }
    
    /**
     * Obtiene las actividades de una reserva.
     * 
     * @param bookingId ID de la reserva
     * @return Lista de actividades de la reserva
     */
    @Transactional(readOnly = true)
    public List<BookingActivity> findBookingActivities(Long bookingId) {
        logger.debug("Obteniendo actividades para reserva ID: {}", bookingId);
        return bookingActivityRepository.findByBookingIdAndIsActiveTrue(bookingId);
    }

    /**
     * Elimina una actividad de una reserva.
     * 
     * @param bookingActivityId ID de la actividad de reserva a eliminar
     * @return true si se eliminó exitosamente, false si no existe
     */
    public boolean removeActivityFromBooking(Long bookingActivityId) {
        logger.info("Eliminando actividad de reserva con ID: {}", bookingActivityId);
        
                        return bookingActivityRepository.findById(bookingActivityId)
                .map(bookingActivity -> {
                    if (Boolean.TRUE.equals(bookingActivity.getIsActive())) {
                        bookingActivity.setIsActive(false); // Realizamos eliminación lógica
                        bookingActivityRepository.save(bookingActivity); // Guardamos el cambio
                        
                        // Actualizar el precio total de la reserva
                        updateBookingTotalPrice(bookingActivity.getBooking());
                        
                        logger.info("Actividad de reserva eliminada exitosamente");
                        return true;
                    } else {
                        logger.warn("La actividad de reserva con ID: {} ya está inactiva", bookingActivityId);
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Verifica la disponibilidad de habitaciones para las fechas especificadas.
     * 
     * @param roomTypeId ID del tipo de habitación
     * @param checkInDate Fecha de check-in
     * @param checkOutDate Fecha de check-out
     * @param numRooms Número de habitaciones requeridas
     * @return true si hay disponibilidad, false en caso contrario
     */
    private boolean checkRoomAvailability(Long roomTypeId, LocalDate checkInDate, 
                                        LocalDate checkOutDate, Integer numRooms) {
        logger.debug("Verificando disponibilidad para tipo de habitación ID: {} del {} al {} para {} habitaciones", 
                    roomTypeId, checkInDate, checkOutDate, numRooms);
        
        Integer bookedRooms = bookingRepository.countBookedRooms(roomTypeId, checkInDate, checkOutDate);
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElse(null);
        
        if (roomType == null) {
            return false;
        }
        
        Integer availableRooms = roomType.getTotalRooms() - (bookedRooms != null ? bookedRooms : 0);
        boolean isAvailable = availableRooms >= numRooms;
        
        logger.debug("Habitaciones disponibles: {}, requeridas: {}, disponible: {}", 
                    availableRooms, numRooms, isAvailable);
        
        return isAvailable;
    }

    /**
     * Calcula el precio total de una reserva.
     * 
     * @param booking Reserva para calcular el precio
     * @return Precio total calculado
     */
    private BigDecimal calculateTotalPrice(Booking booking) {
        // Calcular noches
        long nights = booking.getCheckInDate().until(booking.getCheckOutDate()).getDays();
        
        // Precio base: habitaciones * noches * precio por noche
        BigDecimal basePrice = booking.getRoomType().getPricePerNight()
                .multiply(BigDecimal.valueOf(booking.getNumRooms()))
                .multiply(BigDecimal.valueOf(nights));
        
        // Sumar actividades si las hay (para reservas nuevas, el ID puede ser null)
        BigDecimal activitiesPrice = BigDecimal.ZERO;
        if (booking.getId() != null) {
            activitiesPrice = bookingActivityRepository.findByBookingIdAndIsActiveTrue(booking.getId())
                    .stream()
                    .map(BookingActivity::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        return basePrice.add(activitiesPrice);
    }

    /**
     * Actualiza el precio total de una reserva.
     * 
     * @param booking Reserva a actualizar
     */
    private void updateBookingTotalPrice(Booking booking) {
        BigDecimal newTotalPrice = calculateTotalPrice(booking);
        booking.setTotalPrice(newTotalPrice);
        bookingRepository.save(booking);
        logger.debug("Precio total actualizado para reserva ID: {} - nuevo total: ${}", 
                    booking.getId(), newTotalPrice);
    }

    /**
     * Genera un código de confirmación único para la reserva.
     * 
     * @return Código de confirmación generado
     */
    private String generateConfirmationCode() {
        String prefix = "CB"; // Caribe Vibes
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        String random = String.valueOf((int)(Math.random() * 9000) + 1000);
        return prefix + timestamp + random;
    }

    /**
     * Obtiene las reservas por código de confirmación.
     * 
     * @param confirmationCode Código de confirmación
     * @return Reserva encontrada o Optional vacío
     */
    @Transactional(readOnly = true)
    public Optional<Booking> findBookingByConfirmationCode(String confirmationCode) {
        logger.debug("Buscando reserva por código de confirmación: {}", confirmationCode);
        return bookingRepository.findByConfirmationCodeAndIsActiveTrue(confirmationCode);
    }

    /**
     * Obtiene las reservas próximas (check-in en los próximos 7 días).
     * 
     * @param pageable Configuración de paginación
     * @return Página de reservas próximas
     */
    @Transactional(readOnly = true)
    public Page<Booking> findUpcomingBookings(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        logger.debug("Obteniendo reservas próximas (entre {} y {}) - página: {}, tamaño: {}", 
                    today, nextWeek, pageable.getPageNumber(), pageable.getPageSize());
        return bookingRepository.findByCheckInDateBetweenAndStatusAndIsActiveTrue(
                today, nextWeek, Booking.BookingStatus.CONFIRMED, pageable);
    }
}
