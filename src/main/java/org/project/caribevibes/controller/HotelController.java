package org.project.caribevibes.controller;

import org.project.caribevibes.dto.request.CreateHotelRequestDTO;
import org.project.caribevibes.dto.request.UpdateHotelRequestDTO;
import org.project.caribevibes.dto.request.CreateRoomTypeRequestDTO;
import org.project.caribevibes.dto.request.UpdateRoomTypeRequestDTO;
import org.project.caribevibes.dto.response.HotelResponseDTO;
import org.project.caribevibes.dto.response.RoomTypeResponseDTO;
import org.project.caribevibes.dto.response.DestinationBasicDTO;
import org.project.caribevibes.dto.response.CountryBasicDTO;
import org.project.caribevibes.entity.hotel.Hotel;
import org.project.caribevibes.entity.hotel.RoomType;
import org.project.caribevibes.service.hotel.HotelService;
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
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones de hoteles y tipos de habitaciones.
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * la gestión de hoteles, tipos de habitaciones, búsquedas y filtros.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);

    @Autowired
    private HotelService hotelService;

    /**
     * Obtiene todos los hoteles activos con paginación.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: name)
     * @param sortDir Dirección de ordenamiento (default: asc)
     * @return ResponseEntity con página de hoteles
     */
    @GetMapping
    public ResponseEntity<Page<HotelResponseDTO>> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.debug("Obteniendo hoteles - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Hotel> hotels = hotelService.findAllActiveHotels(pageable);
        
        Page<HotelResponseDTO> hotelDTOs = hotels.map(this::convertToHotelResponseDTO);
        
        logger.debug("Retornando {} hoteles de {} total", hotelDTOs.getNumberOfElements(), hotelDTOs.getTotalElements());
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Obtiene un hotel específico por su ID.
     * 
     * @param id ID del hotel
     * @return ResponseEntity con los detalles del hotel
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long id) {
        logger.debug("Obteniendo hotel por ID: {}", id);
        
        Hotel hotel = hotelService.findHotelById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        
        HotelResponseDTO hotelDTO = convertToHotelResponseDTO(hotel);
        logger.debug("Hotel encontrado: {}", hotel.getName());
        
        return ResponseEntity.ok(hotelDTO);
    }

    /**
     * Busca hoteles por destino.
     * 
     * @param destinationId ID del destino
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de hoteles del destino
     */
    @GetMapping("/by-destination/{destinationId}")
    public ResponseEntity<Page<HotelResponseDTO>> getHotelsByDestination(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Buscando hoteles por destino ID: {} - página: {}, tamaño: {}", 
                    destinationId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Hotel> hotels = hotelService.findHotelsByDestination(destinationId, pageable);
        
        Page<HotelResponseDTO> hotelDTOs = hotels.map(this::convertToHotelResponseDTO);
        
        logger.debug("Encontrados {} hoteles para destino ID: {}", hotelDTOs.getNumberOfElements(), destinationId);
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Busca hoteles por rango de precios.
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de hoteles en el rango de precios
     */
    @GetMapping("/by-price")
    public ResponseEntity<Page<HotelResponseDTO>> getHotelsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Buscando hoteles por rango de precios: ${} - ${} - página: {}, tamaño: {}", 
                    minPrice, maxPrice, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("basePrice").ascending());
        Page<Hotel> hotels = hotelService.findHotelsByPriceRange(minPrice, maxPrice, pageable);
        
        Page<HotelResponseDTO> hotelDTOs = hotels.map(this::convertToHotelResponseDTO);
        
        logger.debug("Encontrados {} hoteles en rango de precios ${} - ${}", 
                    hotelDTOs.getNumberOfElements(), minPrice, maxPrice);
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Busca hoteles por categoría (estrellas).
     * 
     * @param stars Número de estrellas
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de hoteles de la categoría especificada
     */
    @GetMapping("/by-stars/{stars}")
    public ResponseEntity<Page<HotelResponseDTO>> getHotelsByStars(
            @PathVariable Integer stars,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Buscando hoteles con {} estrellas - página: {}, tamaño: {}", 
                    stars, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Hotel> hotels = hotelService.findHotelsByStars(stars, pageable);
        
        Page<HotelResponseDTO> hotelDTOs = hotels.map(this::convertToHotelResponseDTO);
        
        logger.debug("Encontrados {} hoteles con {} estrellas", hotelDTOs.getNumberOfElements(), stars);
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Busca hoteles por nombre.
     * 
     * @param name Nombre o parte del nombre del hotel
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de hoteles que coinciden con el nombre
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HotelResponseDTO>> searchHotelsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Buscando hoteles por nombre: '{}' - página: {}, tamaño: {}", 
                    name, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Hotel> hotels = hotelService.searchHotelsByName(name, pageable);
        
        Page<HotelResponseDTO> hotelDTOs = hotels.map(this::convertToHotelResponseDTO);
        
        logger.debug("Encontrados {} hoteles con nombre que contiene: '{}'", 
                    hotelDTOs.getNumberOfElements(), name);
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Obtiene los hoteles mejor valorados.
     * 
     * @param limit Número máximo de hoteles a retornar (default: 10)
     * @return ResponseEntity con lista de hoteles mejor valorados
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<HotelResponseDTO>> getTopRatedHotels(
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.debug("Obteniendo top {} hoteles mejor valorados", limit);
        
        List<Hotel> hotels = hotelService.findTopRatedHotels(limit);
        List<HotelResponseDTO> hotelDTOs = hotels.stream()
                .map(this::convertToHotelResponseDTO)
                .collect(Collectors.toList());
        
        logger.debug("Retornando {} hoteles mejor valorados", hotelDTOs.size());
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Obtiene los tipos de habitaciones de un hotel.
     * 
     * @param hotelId ID del hotel
     * @return ResponseEntity con lista de tipos de habitaciones
     */
    @GetMapping("/{hotelId}/room-types")
    public ResponseEntity<List<RoomTypeResponseDTO>> getRoomTypesByHotel(@PathVariable Long hotelId) {
        logger.debug("Obteniendo tipos de habitaciones para hotel ID: {}", hotelId);
        
        // Verificar que el hotel existe
        hotelService.findHotelById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", hotelId));
        
        List<RoomType> roomTypes = hotelService.findRoomTypesByHotel(hotelId);
        List<RoomTypeResponseDTO> roomTypeDTOs = roomTypes.stream()
                .map(this::convertToRoomTypeResponseDTO)
                .collect(Collectors.toList());
        
        logger.debug("Encontrados {} tipos de habitaciones para hotel ID: {}", roomTypeDTOs.size(), hotelId);
        return ResponseEntity.ok(roomTypeDTOs);
    }

    /**
     * Obtiene un tipo de habitación específico por su ID.
     * 
     * @param roomTypeId ID del tipo de habitación
     * @return ResponseEntity con los detalles del tipo de habitación
     */
    @GetMapping("/room-types/{roomTypeId}")
    public ResponseEntity<RoomTypeResponseDTO> getRoomTypeById(@PathVariable Long roomTypeId) {
        logger.debug("Obteniendo tipo de habitación por ID: {}", roomTypeId);
        
        RoomType roomType = hotelService.findRoomTypeById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de habitación", "id", roomTypeId));
        
        RoomTypeResponseDTO roomTypeDTO = convertToRoomTypeResponseDTO(roomType);
        logger.debug("Tipo de habitación encontrado: {}", roomType.getName());
        
        return ResponseEntity.ok(roomTypeDTO);
    }

    /**
     * Crea un nuevo hotel (solo para administradores).
     * 
     * @param createHotelDTO Datos del hotel a crear
     * @return ResponseEntity con el hotel creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> createHotel(@Valid @RequestBody CreateHotelRequestDTO createHotelDTO) {
        logger.info("Creando nuevo hotel: {}", createHotelDTO.getName());
        
        Hotel createdHotel = hotelService.createHotelFromDTO(createHotelDTO);
        HotelResponseDTO hotelDTO = convertToHotelResponseDTO(createdHotel);
        
        logger.info("Hotel creado exitosamente con ID: {}", createdHotel.getId());
        return ResponseEntity.status(201).body(hotelDTO);
    }

    /**
     * Actualiza un hotel existente (solo para administradores).
     * 
     * @param id ID del hotel a actualizar
     * @param updateHotelDTO Datos actualizados del hotel
     * @return ResponseEntity con el hotel actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> updateHotel(@PathVariable Long id, @Valid @RequestBody UpdateHotelRequestDTO updateHotelDTO) {
        logger.info("Actualizando hotel con ID: {}", id);
        
        Hotel updatedHotel = hotelService.updateHotelFromDTO(id, updateHotelDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        
        HotelResponseDTO hotelDTO = convertToHotelResponseDTO(updatedHotel);
        
        logger.info("Hotel actualizado exitosamente: {}", updatedHotel.getName());
        return ResponseEntity.ok(hotelDTO);
    }

    /**
     * Desactiva un hotel (solo para administradores).
     * 
     * @param id ID del hotel a desactivar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateHotel(@PathVariable Long id) {
        logger.info("Desactivando hotel con ID: {}", id);
        
        boolean deactivated = hotelService.deactivateHotel(id);
        if (!deactivated) {
            throw new ResourceNotFoundException("Hotel", "id", id);
        }
        
        logger.info("Hotel desactivado exitosamente con ID: {}", id);
        return ResponseEntity.ok("Hotel desactivado exitosamente");
    }

    /**
     * Obtiene todos los hoteles (incluidos inactivos) para panel de administración.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: name)
     * @param sortDir Dirección de ordenamiento (default: asc)
     * @return ResponseEntity con página de todos los hoteles
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<HotelResponseDTO>> getAllHotelsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.debug("Obteniendo todos los hoteles para administración - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Hotel> hotels = hotelService.findAllHotelsForAdmin(pageable);
        
        Page<HotelResponseDTO> hotelDTOs = hotels.map(this::convertToHotelResponseDTO);
        
        logger.debug("Retornando {} hoteles de {} total para administración", hotelDTOs.getNumberOfElements(), hotelDTOs.getTotalElements());
        return ResponseEntity.ok(hotelDTOs);
    }

    /**
     * Crea un nuevo tipo de habitación para un hotel (solo para administradores).
     * 
     * @param hotelId ID del hotel
     * @param createRoomTypeDTO Datos del tipo de habitación a crear
     * @return ResponseEntity con el tipo de habitación creado
     */
    @PostMapping("/{hotelId}/room-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomTypeResponseDTO> createRoomType(@PathVariable Long hotelId, @Valid @RequestBody CreateRoomTypeRequestDTO createRoomTypeDTO) {
        logger.info("Creando nuevo tipo de habitación para hotel ID: {}", hotelId);
        
        RoomType createdRoomType = hotelService.createRoomTypeFromDTO(hotelId, createRoomTypeDTO);
        RoomTypeResponseDTO roomTypeDTO = convertToRoomTypeResponseDTO(createdRoomType);
        
        logger.info("Tipo de habitación creado exitosamente con ID: {}", createdRoomType.getId());
        return ResponseEntity.status(201).body(roomTypeDTO);
    }

    /**
     * Actualiza un tipo de habitación existente (solo para administradores).
     * 
     * @param hotelId ID del hotel
     * @param roomTypeId ID del tipo de habitación a actualizar
     * @param updateRoomTypeDTO Datos actualizados del tipo de habitación
     * @return ResponseEntity con el tipo de habitación actualizado
     */
    @PutMapping("/{hotelId}/room-types/{roomTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomTypeResponseDTO> updateRoomType(@PathVariable Long hotelId, @PathVariable Long roomTypeId, @Valid @RequestBody UpdateRoomTypeRequestDTO updateRoomTypeDTO) {
        logger.info("Actualizando tipo de habitación con ID: {} para hotel ID: {}", roomTypeId, hotelId);
        
        RoomType updatedRoomType = hotelService.updateRoomTypeFromDTO(hotelId, roomTypeId, updateRoomTypeDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de habitación", "id", roomTypeId));
        
        RoomTypeResponseDTO roomTypeDTO = convertToRoomTypeResponseDTO(updatedRoomType);
        
        logger.info("Tipo de habitación actualizado exitosamente: {}", updatedRoomType.getName());
        return ResponseEntity.ok(roomTypeDTO);
    }

    /**
     * Elimina un tipo de habitación (solo para administradores).
     * 
     * @param hotelId ID del hotel
     * @param roomTypeId ID del tipo de habitación a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{hotelId}/room-types/{roomTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRoomType(@PathVariable Long hotelId, @PathVariable Long roomTypeId) {
        logger.info("Eliminando tipo de habitación con ID: {} del hotel ID: {}", roomTypeId, hotelId);
        
        boolean deleted = hotelService.deleteRoomType(hotelId, roomTypeId);
        if (!deleted) {
            throw new ResourceNotFoundException("Tipo de habitación", "id", roomTypeId);
        }
        
        logger.info("Tipo de habitación eliminado exitosamente con ID: {}", roomTypeId);
        return ResponseEntity.ok("Tipo de habitación eliminado exitosamente");
    }

    /**
     * Convierte una entidad Hotel a HotelResponseDTO.
     * 
     * @param hotel Entidad Hotel
     * @return DTO de respuesta del hotel
     */
    private HotelResponseDTO convertToHotelResponseDTO(Hotel hotel) {
        HotelResponseDTO dto = new HotelResponseDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setAddress(hotel.getAddress());
        dto.setPhone(hotel.getPhoneNumber());
        dto.setEmail(hotel.getEmail());
        dto.setWebsite(hotel.getWebsiteUrl());
        dto.setStars(hotel.getStars());
        dto.setRating(hotel.getRating()); // Mapear el rating del hotel
        // Mapear el campo basePrice de la entidad Hotel
        dto.setBasePrice(hotel.getBasePrice());
        // Mapear el campo isActive del hotel
        dto.setIsActive(hotel.getIsActive());
        // Convertir List<String> a String - utilizando String.join
        dto.setAmenities(hotel.getAmenities() != null ? String.join(", ", hotel.getAmenities()) : null);
        dto.setImageUrl(hotel.getImageUrl());
        // La latitud y longitud no están en la entidad Hotel actualmente
        dto.setLatitude(null);
        dto.setLongitude(null);
        // Las políticas de check-in/out no se manejan como LocalTime en la entidad actual
        dto.setCheckInTime(null);
        dto.setCheckOutTime(null);
        // Combinar políticas en un solo string
        String policies = String.join("\n\n", 
            hotel.getCheckinPolicy() != null ? hotel.getCheckinPolicy() : "",
            hotel.getCheckoutPolicy() != null ? hotel.getCheckoutPolicy() : "",
            hotel.getCancellationPolicy() != null ? hotel.getCancellationPolicy() : ""
        ).trim();
        dto.setPolicies(policies.isEmpty() ? null : policies);
        
        // Mapear el nombre del destino para fácil acceso en frontend
        dto.setDestinationName(hotel.getDestination() != null ? hotel.getDestination().getName() : null);
        
        // Mapear el destino si está cargado
        if (hotel.getDestination() != null) {
            DestinationBasicDTO destinationDTO = new DestinationBasicDTO();
            destinationDTO.setId(hotel.getDestination().getId());
            destinationDTO.setName(hotel.getDestination().getName());
            destinationDTO.setLocation(hotel.getDestination().getLocation());
            destinationDTO.setImageUrl(hotel.getDestination().getImageUrl());
            
            // Mapear el país si está cargado
            if (hotel.getDestination().getCountry() != null) {
                CountryBasicDTO countryDTO = new CountryBasicDTO();
                countryDTO.setId(hotel.getDestination().getCountry().getId());
                countryDTO.setName(hotel.getDestination().getCountry().getName());
                countryDTO.setCode(hotel.getDestination().getCountry().getCode());
                countryDTO.setContinent(hotel.getDestination().getCountry().getContinent());
                countryDTO.setCurrency(hotel.getDestination().getCountry().getCurrency());
                countryDTO.setPhonePrefix(hotel.getDestination().getCountry().getPhonePrefix());
                destinationDTO.setCountry(countryDTO);
            }
            
            dto.setDestination(destinationDTO);
        }

        // Mapear los tipos de habitaciones si están cargados
        if (hotel.getRoomTypes() != null && !hotel.getRoomTypes().isEmpty()) {
            List<RoomTypeResponseDTO> roomTypeDTOs = hotel.getRoomTypes().stream()
                    .map(this::convertToRoomTypeResponseDTO)
                    .collect(Collectors.toList());
            dto.setRoomTypes(roomTypeDTOs);
        }

        return dto;
    }

    /**
     * Convierte una entidad RoomType a RoomTypeResponseDTO.
     * 
     * @param roomType Entidad RoomType
     * @return DTO de respuesta del tipo de habitación
     */
    private RoomTypeResponseDTO convertToRoomTypeResponseDTO(RoomType roomType) {
        RoomTypeResponseDTO dto = new RoomTypeResponseDTO();
        dto.setId(roomType.getId());
        dto.setName(roomType.getName());
        dto.setDescription(roomType.getDescription());
        dto.setMaxOccupancy(roomType.getCapacity());
        // Convertir enum BedType a String usando getDisplayName
        dto.setBedType(roomType.getBedType() != null ? roomType.getBedType().getDisplayName() : null);
        dto.setSize(roomType.getRoomSize() != null ? roomType.getRoomSize().doubleValue() : null);
        dto.setPricePerNight(roomType.getPricePerNight());
        // La clase RoomType no tiene un método getAmenities(), asignar null o cadena vacía
        dto.setAmenities("");
        // La clase RoomType no tiene un método getImageUrl(), asignar null
        dto.setImageUrl(null);
        dto.setTotalRooms(roomType.getTotalRooms());
        dto.setAvailableRooms(roomType.getAvailableRooms());
        dto.setIsActive(roomType.getIsActive());

        return dto;

    }
}
