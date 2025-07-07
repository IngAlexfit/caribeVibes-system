package org.project.caribevibes.controller;

import org.project.caribevibes.dto.request.BookingRequestDTO;
import org.project.caribevibes.dto.response.BookingResponseDTO;
import org.project.caribevibes.dto.response.BookingActivityResponseDTO;
import org.project.caribevibes.dto.response.UserBasicDTO;
import org.project.caribevibes.dto.response.HotelBasicDTO;
import org.project.caribevibes.dto.response.RoomTypeBasicDTO;
import org.project.caribevibes.dto.response.ActivityBasicDTO;
import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.booking.BookingActivity;
import org.project.caribevibes.entity.hotel.Hotel;
import org.project.caribevibes.entity.hotel.RoomType;
import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.service.booking.BookingService;
import org.project.caribevibes.service.hotel.HotelService;
import org.project.caribevibes.service.auth.AuthService;
import org.project.caribevibes.service.pdf.PdfService;
import org.project.caribevibes.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones de reservas y actividades de reservas.
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * la gestión de reservas de hoteles y actividades asociadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HotelService hotelService;    @Autowired
    private AuthService authService;

    @Autowired
    private PdfService pdfService;

    /**
     * Obtiene todas las reservas activas (solo para administradores).
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: bookingDate)
     * @param sortDir Dirección de ordenamiento (default: desc)
     * @return ResponseEntity con página de reservas
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Obteniendo todas las reservas - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Booking> bookings = bookingService.findAllActiveBookings(pageable);
        
        Page<BookingResponseDTO> bookingDTOs = bookings.map(this::convertToBookingResponseDTO);
        
        logger.debug("Retornando {} reservas de {} total", bookingDTOs.getNumberOfElements(), bookingDTOs.getTotalElements());
        return ResponseEntity.ok(bookingDTOs);
    }

    /**
     * Obtiene las reservas del usuario autenticado.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reservas del usuario
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Obteniendo reservas para usuario: {} - página: {}, tamaño: {}", 
                    userEmail, page, size);
        
        // Obtener el usuario actual
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        Page<Booking> bookings = bookingService.findBookingsByUser(currentUser.getId(), pageable);
        
        Page<BookingResponseDTO> bookingDTOs = bookings.map(this::convertToBookingResponseDTO);
        
        logger.debug("Retornando {} reservas para usuario: {}", bookingDTOs.getNumberOfElements(), userEmail);
        return ResponseEntity.ok(bookingDTOs);
    }

    /**
     * Obtiene una reserva específica por su ID.
     * 
     * @param id ID de la reserva
     * @return ResponseEntity con los detalles de la reserva
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        logger.debug("Obteniendo reserva por ID: {}", id);
        
        Booking booking = bookingService.findBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Verificar que el usuario puede acceder a esta reserva
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        // Solo el propietario o un admin pueden ver la reserva
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRoleNames().contains("ADMIN")) {
            throw new ResourceNotFoundException("Reserva", "id", id);
        }
        
        BookingResponseDTO bookingDTO = convertToBookingResponseDTO(booking);
        logger.debug("Reserva encontrada: {}", booking.getConfirmationCode());
        
        return ResponseEntity.ok(bookingDTO);
    }

    /**
     * Busca una reserva por código de confirmación.
     * 
     * @param confirmationCode Código de confirmación de la reserva
     * @return ResponseEntity con los detalles de la reserva
     */
    @GetMapping("/by-confirmation/{confirmationCode}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        logger.debug("Buscando reserva por código de confirmación: {}", confirmationCode);
        
        Booking booking = bookingService.findBookingByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "código de confirmación", confirmationCode));
        
        // Verificar que el usuario puede acceder a esta reserva
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRoleNames().contains("ADMIN")) {
            throw new ResourceNotFoundException("Reserva", "código de confirmación", confirmationCode);
        }
        
        BookingResponseDTO bookingDTO = convertToBookingResponseDTO(booking);
        logger.debug("Reserva encontrada por código: {}", confirmationCode);
        
        return ResponseEntity.ok(bookingDTO);
    }

    /**
     * Obtiene las reservas por estado.
     * 
     * @param status Estado de las reservas
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reservas del estado especificado
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> getBookingsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo reservas por estado: {} - página: {}, tamaño: {}", 
                    status, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        Page<Booking> bookings = bookingService.findBookingsByStatus(status, pageable);
        
        Page<BookingResponseDTO> bookingDTOs = bookings.map(this::convertToBookingResponseDTO);
        
        logger.debug("Encontradas {} reservas con estado: {}", bookingDTOs.getNumberOfElements(), status);
        return ResponseEntity.ok(bookingDTOs);
    }

    /**
     * Obtiene las reservas próximas (check-in en los próximos 7 días).
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de reservas próximas
     */
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> getUpcomingBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo reservas próximas - página: {}, tamaño: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("checkInDate").ascending());
        Page<Booking> bookings = bookingService.findUpcomingBookings(pageable);
        
        Page<BookingResponseDTO> bookingDTOs = bookings.map(this::convertToBookingResponseDTO);
        
        logger.debug("Encontradas {} reservas próximas", bookingDTOs.getNumberOfElements());
        return ResponseEntity.ok(bookingDTOs);
    }

    /**
     * Crea una nueva reserva.
     * 
     * @param bookingRequest Datos de la reserva a crear
     * @return ResponseEntity con la reserva creada
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Creando nueva reserva para usuario: {}", userEmail);
        
        // Obtener el usuario actual
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        // Obtener hotel y tipo de habitación
        Hotel hotel = hotelService.findHotelById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", bookingRequest.getHotelId()));
        
        RoomType roomType = hotelService.findRoomTypeById(bookingRequest.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de habitación", "id", bookingRequest.getRoomTypeId()));
        
        // Crear la entidad Booking
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setHotel(hotel);
        booking.setDestination(hotel.getDestination()); // Set the destination from the hotel
        booking.setRoomType(roomType);
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setNumGuests(bookingRequest.getNumGuests());
        booking.setNumRooms(bookingRequest.getNumRooms());
        booking.setSpecialRequests(bookingRequest.getSpecialRequests());
        
        Booking createdBooking = bookingService.createBooking(booking);
        BookingResponseDTO bookingDTO = convertToBookingResponseDTO(createdBooking);
        
        logger.info("Reserva creada exitosamente con código: {}", createdBooking.getConfirmationCode());
        return ResponseEntity.status(201).body(bookingDTO);
    }

    /**
     * Confirma una reserva.
     * 
     * @param id ID de la reserva a confirmar
     * @return ResponseEntity con mensaje de confirmación
     */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> confirmBooking(@PathVariable Long id) {
        logger.info("Confirmando reserva con ID: {}", id);
        
        boolean confirmed = bookingService.confirmBooking(id);
        if (!confirmed) {
            throw new ResourceNotFoundException("Reserva", "id", id);
        }
        
        Map<String, String> response = Map.of(
            "message", "Reserva confirmada exitosamente",
            "status", "CONFIRMED"
        );
        
        logger.info("Reserva confirmada exitosamente con ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela una reserva.
     * 
     * @param id ID de la reserva a cancelar
     * @return ResponseEntity con mensaje de confirmación
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long id) {
        logger.info("Cancelando reserva con ID: {}", id);
        
        // Verificar que el usuario puede cancelar esta reserva
        Booking booking = bookingService.findBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRoleNames().contains("ADMIN")) {
            throw new ResourceNotFoundException("Reserva", "id", id);
        }
        
        boolean cancelled = bookingService.cancelBooking(id);
        if (!cancelled) {
            throw new IllegalArgumentException("No se puede cancelar esta reserva");
        }
        
        Map<String, String> response = Map.of(
            "message", "Reserva cancelada exitosamente",
            "status", "CANCELLED"
        );
        
        logger.info("Reserva cancelada exitosamente con ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Agrega una actividad a una reserva.
     * 
     * @param bookingId ID de la reserva
     * @param activityId ID de la actividad
     * @param quantity Cantidad de personas para la actividad
     * @return ResponseEntity con la actividad agregada
     */
    @PostMapping("/{bookingId}/activities")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookingActivityResponseDTO> addActivityToBooking(
            @PathVariable Long bookingId,
            @RequestParam Long activityId,
            @RequestParam Integer quantity) {
        
        logger.info("Agregando actividad ID: {} a reserva ID: {} para {} personas", 
                   activityId, bookingId, quantity);
        
        // Verificar que el usuario puede modificar esta reserva
        Booking booking = bookingService.findBookingById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", bookingId));
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRoleNames().contains("ADMIN")) {
            throw new ResourceNotFoundException("Reserva", "id", bookingId);
        }
        
        BookingActivity bookingActivity = bookingService.addActivityToBooking(bookingId, activityId, quantity);
        BookingActivityResponseDTO activityDTO = convertToBookingActivityResponseDTO(bookingActivity);
        
        logger.info("Actividad agregada exitosamente a reserva ID: {}", bookingId);
        return ResponseEntity.status(201).body(activityDTO);
    }

    /**
     * Obtiene las actividades de una reserva.
     * 
     * @param bookingId ID de la reserva
     * @return ResponseEntity con lista de actividades de la reserva
     */
    @GetMapping("/{bookingId}/activities")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingActivityResponseDTO>> getBookingActivities(@PathVariable Long bookingId) {
        logger.debug("Obteniendo actividades para reserva ID: {}", bookingId);
        
        // Verificar que el usuario puede acceder a esta reserva
        Booking booking = bookingService.findBookingById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", bookingId));
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = authService.findUserByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));
        
        if (!booking.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRoleNames().contains("ADMIN")) {
            throw new ResourceNotFoundException("Reserva", "id", bookingId);
        }
        
        List<BookingActivity> activities = bookingService.findBookingActivities(bookingId);
        List<BookingActivityResponseDTO> activityDTOs = activities.stream()
                .map(this::convertToBookingActivityResponseDTO)
                .collect(Collectors.toList());
        
        logger.debug("Encontradas {} actividades para reserva ID: {}", activityDTOs.size(), bookingId);
        return ResponseEntity.ok(activityDTOs);
    }

    /**
     * Convierte una entidad Booking a BookingResponseDTO.
     * 
     * @param booking Entidad Booking
     * @return DTO de respuesta de la reserva
     */
    private BookingResponseDTO convertToBookingResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setConfirmationCode(booking.getConfirmationCode());
        dto.setBookingDate(booking.getBookingDate());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumGuests(booking.getNumGuests());
        dto.setNumRooms(booking.getNumRooms());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus().toString());
        dto.setSpecialRequests(booking.getSpecialRequests());
        
        // Convertir información del usuario
        if (booking.getUser() != null) {
            UserBasicDTO userDTO = new UserBasicDTO();
            userDTO.setId(booking.getUser().getId());
            userDTO.setFirstName(booking.getUser().getFirstName());
            userDTO.setLastName(booking.getUser().getLastName());
            userDTO.setEmail(booking.getUser().getEmail());
            dto.setUser(userDTO);
        }
        
        // Convertir información del hotel
        if (booking.getHotel() != null) {
            HotelBasicDTO hotelDTO = new HotelBasicDTO();
            hotelDTO.setId(booking.getHotel().getId());
            hotelDTO.setName(booking.getHotel().getName());
            hotelDTO.setStars(booking.getHotel().getStars());
            hotelDTO.setImageUrl(booking.getHotel().getImageUrl()); // Corrected method name
            dto.setHotel(hotelDTO);
        }
        
        // Convertir información del tipo de habitación
        if (booking.getRoomType() != null) {
            RoomTypeBasicDTO roomTypeDTO = new RoomTypeBasicDTO();
            roomTypeDTO.setId(booking.getRoomType().getId());
            roomTypeDTO.setName(booking.getRoomType().getName());
            roomTypeDTO.setPricePerNight(booking.getRoomType().getPricePerNight());
            roomTypeDTO.setMaxOccupancy(booking.getRoomType().getCapacity()); // Map capacity to maxOccupancy
            dto.setRoomType(roomTypeDTO);
        }
        
        // Obtener y convertir actividades
        List<BookingActivity> activities = bookingService.findBookingActivities(booking.getId());
        if (activities != null && !activities.isEmpty()) {
            List<BookingActivityResponseDTO> activityDTOs = activities.stream()
                    .map(this::convertToBookingActivityResponseDTO)
                    .collect(Collectors.toList());
            dto.setActivities(activityDTOs);
        }
        
        return dto;
    }

    /**
     * Convierte una entidad BookingActivity a BookingActivityResponseDTO.
     * 
     * @param bookingActivity Entidad BookingActivity
     * @return DTO de respuesta de la actividad de reserva
     */
    private BookingActivityResponseDTO convertToBookingActivityResponseDTO(BookingActivity bookingActivity) {
        BookingActivityResponseDTO dto = new BookingActivityResponseDTO();
        dto.setId(bookingActivity.getId());
        dto.setQuantity(bookingActivity.getQuantity());
        dto.setPricePerPerson(bookingActivity.getPricePerPerson());
        dto.setTotalPrice(bookingActivity.getTotalPrice());
        
        // Convertir información de la actividad
        if (bookingActivity.getActivity() != null) {
            ActivityBasicDTO activityDTO = new ActivityBasicDTO();
            activityDTO.setId(bookingActivity.getActivity().getId());
            activityDTO.setName(bookingActivity.getActivity().getName());
            activityDTO.setDescription(bookingActivity.getActivity().getDescription());
            activityDTO.setPrice(bookingActivity.getActivity().getPrice());
            // Note: Activity entity doesn't have imageUrl field, setting to null or removing this line
            dto.setActivity(activityDTO);
        }
        
        return dto;
    }

    /**
     * Genera y descarga el voucher PDF de una reserva específica.
     * 
     * @param bookingId ID de la reserva
     * @return ResponseEntity con el archivo PDF
     */
    @GetMapping("/{bookingId}/voucher")
    @PreAuthorize("hasRole('CLIENT') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadVoucher(@PathVariable Long bookingId) {
        logger.info("Generando voucher PDF para reserva ID: {}", bookingId);
        
        try {
            // Obtener información del usuario autenticado
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = authService.findUserByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            
            // Obtener la reserva
            Booking booking = bookingService.findBookingById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", bookingId));

            // Verificar que el usuario tiene acceso a esta reserva (solo su propia reserva o admin)
            if (!currentUser.getRoleNames().contains("ADMIN") && 
                !booking.getUser().getId().equals(currentUser.getId())) {
                logger.warn("Usuario {} intentó acceder a reserva de otro usuario: {}", currentUser.getEmail(), booking.getUser().getEmail());
                throw new ResourceNotFoundException("Reserva", "id", bookingId);
            }

            // Generar el voucher PDF
            logger.info("Generando voucher PDF para reserva ID: {}", bookingId);
            byte[] pdfBytes = pdfService.generateBookingVoucher(booking);
            
            // Configurar headers para la descarga
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"voucher-reserva-" + bookingId + ".pdf\"")
                    .body(pdfBytes);
                    
        } catch (ResourceNotFoundException e) {
            logger.error("Reserva no encontrada: {}", bookingId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error generando voucher para reserva {}: {}", bookingId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
