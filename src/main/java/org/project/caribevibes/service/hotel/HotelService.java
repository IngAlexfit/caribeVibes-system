package org.project.caribevibes.service.hotel;

import org.project.caribevibes.entity.hotel.Hotel;
import org.project.caribevibes.entity.hotel.RoomType;
import org.project.caribevibes.repository.hotel.HotelRepository;
import org.project.caribevibes.repository.hotel.RoomTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de hoteles y tipos de habitaciones.
 * 
 * Este servicio proporciona funcionalidades para:
 * - Gestión completa de hoteles (CRUD)
 * - Gestión de tipos de habitaciones
 * - Búsqueda y filtrado de hoteles
 * - Consulta de disponibilidad de habitaciones
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Service
@Transactional
public class HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;    /**
     * Obtiene todos los hoteles activos paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de hoteles activos
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "hotels", key = "'all-active-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Hotel> findAllActiveHotels(Pageable pageable) {
        logger.info("🔍 EJECUTANDO CONSULTA A BD - Obteniendo todos los hoteles activos con destino - página: {}, tamaño: {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        logger.info("📊 CACHE MISS - Los datos se están obteniendo desde la base de datos");
        return hotelRepository.findAllActiveHotelsWithDestination(pageable);
    }

    /**
     * Busca hoteles por destino con paginación.
     * 
     * @param destinationId ID del destino
     * @param pageable Configuración de paginación
     * @return Página de hoteles del destino especificado
     */
    @Transactional(readOnly = true)
    public Page<Hotel> findHotelsByDestination(Long destinationId, Pageable pageable) {
        logger.debug("Buscando hoteles para destino ID: {} - página: {}, tamaño: {}", 
                    destinationId, pageable.getPageNumber(), pageable.getPageSize());
        return hotelRepository.findByDestinationIdAndIsActiveTrueOrderByNameAsc(destinationId, pageable);
    }

    /**
     * Busca hoteles por rango de precios.
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @param pageable Configuración de paginación
     * @return Página de hoteles en el rango de precios especificado
     */
    @Transactional(readOnly = true)
    public Page<Hotel> findHotelsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        logger.debug("Buscando hoteles por rango de precios: ${} - ${} - página: {}, tamaño: {}", 
                    minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize());
        return hotelRepository.findByBasePriceBetweenAndIsActiveTrueOrderByBasePriceAsc(minPrice, maxPrice, pageable);
    }

    /**
     * Busca hoteles por categoría (estrellas).
     * 
     * @param stars Número de estrellas del hotel
     * @param pageable Configuración de paginación
     * @return Página de hoteles con la categoría especificada
     */
    @Transactional(readOnly = true)
    public Page<Hotel> findHotelsByStars(Integer stars, Pageable pageable) {
        logger.debug("Buscando hoteles con {} estrellas - página: {}, tamaño: {}", 
                    stars, pageable.getPageNumber(), pageable.getPageSize());
        return hotelRepository.findByStarsAndIsActiveTrueOrderByNameAsc(stars, pageable);
    }    /**
     * Obtiene un hotel por su ID.
     * 
     * @param id ID del hotel
     * @return Hotel encontrado o Optional vacío
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "hotels", key = "#id")
    public Optional<Hotel> findHotelById(Long id) {
        logger.info("🔍 EJECUTANDO CONSULTA A BD - Buscando hotel por ID: {}", id);
        logger.info("📊 CACHE MISS - Los datos se están obteniendo desde la base de datos");
        return hotelRepository.findByIdAndIsActiveTrue(id);
    }    /**
     * Crea un nuevo hotel.
     * 
     * @param hotel Hotel a crear
     * @return Hotel creado
     */
    @CacheEvict(value = "hotels", allEntries = true)
    public Hotel createHotel(Hotel hotel) {
        logger.info("Creando nuevo hotel: {}", hotel.getName());
        hotel.setIsActive(true);
        Hotel savedHotel = hotelRepository.save(hotel);
        logger.info("Hotel creado exitosamente con ID: {}", savedHotel.getId());
        return savedHotel;
    }    /**
     * Actualiza un hotel existente.
     * 
     * @param id ID del hotel a actualizar
     * @param hotelDetails Detalles actualizados del hotel
     * @return Hotel actualizado o Optional vacío si no existe
     */
    @CachePut(value = "hotels", key = "#id")
    @CacheEvict(value = "hotels", key = "'all-active-*'", beforeInvocation = true)
    public Optional<Hotel> updateHotel(Long id, Hotel hotelDetails) {
        logger.info("Actualizando hotel con ID: {}", id);
        return hotelRepository.findByIdAndIsActiveTrue(id)
                .map(existingHotel -> {
                    existingHotel.setName(hotelDetails.getName());
                    existingHotel.setDescription(hotelDetails.getDescription());
                    existingHotel.setAddress(hotelDetails.getAddress());
                    existingHotel.setPhoneNumber(hotelDetails.getPhoneNumber());
                    existingHotel.setEmail(hotelDetails.getEmail());
                    existingHotel.setWebsiteUrl(hotelDetails.getWebsiteUrl());
                    existingHotel.setStars(hotelDetails.getStars());
                    existingHotel.setAmenities(hotelDetails.getAmenities());
                    existingHotel.setImageUrl(hotelDetails.getImageUrl());
                    existingHotel.setCheckinPolicy(hotelDetails.getCheckinPolicy());
                    existingHotel.setCheckoutPolicy(hotelDetails.getCheckoutPolicy());
                    existingHotel.setCancellationPolicy(hotelDetails.getCancellationPolicy());

                    Hotel updatedHotel = hotelRepository.save(existingHotel);
                    logger.info("Hotel actualizado exitosamente: {}", updatedHotel.getName());
                    return updatedHotel;
                });
    }

    /**
     * Desactiva un hotel (eliminación lógica).
     * 
     * @param id ID del hotel a desactivar
     * @return true si se desactivó exitosamente, false si no existe
     */
    public boolean deactivateHotel(Long id) {
        logger.info("Desactivando hotel con ID: {}", id);
          return hotelRepository.findByIdAndIsActiveTrue(id)
                .map(hotel -> {
                    hotel.setIsActive(false);
                    hotelRepository.save(hotel);
                    logger.info("Hotel desactivado exitosamente: {}", hotel.getName());
                    return true;
                })
                .orElse(false);
    }

    /**
     * Obtiene todos los tipos de habitaciones activos para un hotel.
     * 
     * @param hotelId ID del hotel
     * @return Lista de tipos de habitaciones activos
     */
    @Transactional(readOnly = true)
    public List<RoomType> findRoomTypesByHotel(Long hotelId) {
        logger.debug("Obteniendo tipos de habitaciones para hotel ID: {}", hotelId);
        return roomTypeRepository.findByHotelIdAndIsActiveTrue(hotelId);
    }

    /**
     * Obtiene un tipo de habitación por su ID.
     * 
     * @param id ID del tipo de habitación
     * @return Tipo de habitación encontrado o Optional vacío
     */
    @Transactional(readOnly = true)
    public Optional<RoomType> findRoomTypeById(Long id) {
        logger.debug("Buscando tipo de habitación por ID: {}", id);
        return roomTypeRepository.findByIdAndIsActiveTrue(id);
    }

    /**
     * Crea un nuevo tipo de habitación.
     * 
     * @param roomType Tipo de habitación a crear
     * @return Tipo de habitación creado
     */
    public RoomType createRoomType(RoomType roomType) {
        logger.info("Creando nuevo tipo de habitación: {} para hotel ID: {}", 
                   roomType.getName(), roomType.getHotel().getId());
        roomType.setIsActive(true);
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        logger.info("Tipo de habitación creado exitosamente con ID: {}", savedRoomType.getId());
        return savedRoomType;
    }

    /**
     * Actualiza un tipo de habitación existente.
     * 
     * @param id ID del tipo de habitación a actualizar
     * @param roomTypeDetails Detalles actualizados del tipo de habitación
     * @return Tipo de habitación actualizado o Optional vacío si no existe
     */
    public Optional<RoomType> updateRoomType(Long id, RoomType roomTypeDetails) {        logger.info("Actualizando tipo de habitación con ID: {}", id);
          return roomTypeRepository.findByIdAndIsActiveTrue(id)
                .map(existingRoomType -> {
                    existingRoomType.setName(roomTypeDetails.getName());
                    existingRoomType.setDescription(roomTypeDetails.getDescription());
                    existingRoomType.setCapacity(roomTypeDetails.getCapacity());
                    existingRoomType.setBedType(roomTypeDetails.getBedType());
                    existingRoomType.setRoomSize(roomTypeDetails.getRoomSize());
                    existingRoomType.setPricePerNight(roomTypeDetails.getPricePerNight());
                    existingRoomType.setViewType(roomTypeDetails.getViewType());
                    existingRoomType.setTotalRooms(roomTypeDetails.getTotalRooms());
                    existingRoomType.setAvailableRooms(roomTypeDetails.getAvailableRooms());
                    
                    RoomType updatedRoomType = roomTypeRepository.save(existingRoomType);
                    logger.info("Tipo de habitación actualizado exitosamente: {}", updatedRoomType.getName());
                    return updatedRoomType;
                });
    }

    /**
     * Desactiva un tipo de habitación (eliminación lógica).
     * 
     * @param id ID del tipo de habitación a desactivar
     * @return true si se desactivó exitosamente, false si no existe
     */
    public boolean deactivateRoomType(Long id) {        logger.info("Desactivando tipo de habitación con ID: {}", id);
        
        return roomTypeRepository.findByIdAndIsActiveTrue(id)
                .map(roomType -> {
                    roomType.setIsActive(false);
                    roomTypeRepository.save(roomType);
                    logger.info("Tipo de habitación desactivado exitosamente: {}", roomType.getName());
                    return true;
                })
                .orElse(false);
    }

    /**
     * Obtiene los hoteles mejor valorados.
     * 
     * @param limit Número máximo de hoteles a retornar
     * @return Lista de hoteles mejor valorados
     */
    @Transactional(readOnly = true)
    public List<Hotel> findTopRatedHotels(int limit) {
        logger.debug("Obteniendo top {} hoteles mejor valorados", limit);
        return hotelRepository.findTopRatedHotels(Pageable.ofSize(limit)).getContent();
    }

    /**
     * Busca hoteles por nombre (búsqueda difusa).
     * 
     * @param name Nombre o parte del nombre del hotel
     * @param pageable Configuración de paginación
     * @return Página de hoteles que coinciden con el nombre
     */
    @Transactional(readOnly = true)
    public Page<Hotel> searchHotelsByName(String name, Pageable pageable) {
        logger.debug("Buscando hoteles por nombre: '{}' - página: {}, tamaño: {}", 
                    name, pageable.getPageNumber(), pageable.getPageSize());
        return hotelRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByNameAsc(name, pageable);
    }

    /**
     * Cuenta el total de hoteles activos en el sistema.
     * 
     * @return Número total de hoteles activos
     */
    @Transactional(readOnly = true)
    public long countActiveHotels() {
        logger.debug("Contando todos los hoteles activos");
        return hotelRepository.countByIsActiveTrue();
    }
}
