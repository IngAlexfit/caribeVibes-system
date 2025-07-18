package org.project.caribevibes.repository.hotel;

import org.project.caribevibes.entity.hotel.RoomType;
import org.project.caribevibes.entity.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de tipos de habitaciones en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad RoomType, incluyendo verificación de disponibilidad,
 * filtros por precio y capacidad.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    /**
     * Obtiene todos los tipos de habitación de un hotel específico
     * 
     * @param hotel Hotel del cual obtener los tipos de habitación
     * @return Lista de tipos de habitación del hotel
     */
    List<RoomType> findByHotel(Hotel hotel);

    /**
     * Obtiene todos los tipos de habitación de un hotel por ID
     * 
     * @param hotelId ID del hotel
     * @return Lista de tipos de habitación del hotel
     */
    List<RoomType> findByHotelId(Long hotelId);

    /**
     * Obtiene tipos de habitación activos de un hotel específico
     * 
     * @param hotelId ID del hotel
     * @return Lista de tipos de habitación activos del hotel
     */
    List<RoomType> findByHotelIdAndIsActiveTrueOrderByPricePerNightAsc(Long hotelId);

    /**
     * Obtiene tipos de habitación disponibles de un hotel
     * 
     * @param hotelId ID del hotel
     * @return Lista de tipos de habitación con disponibilidad
     */
    List<RoomType> findByHotelIdAndIsActiveTrueAndAvailableRoomsGreaterThan(Long hotelId, Integer availableRooms);

    /**
     * Busca tipos de habitación por capacidad específica
     * 
     * @param capacity Capacidad requerida
     * @return Lista de tipos de habitación con la capacidad especificada
     */
    List<RoomType> findByCapacity(Integer capacity);

    /**
     * Busca tipos de habitación con capacidad mínima
     * 
     * @param minCapacity Capacidad mínima requerida
     * @return Lista de tipos de habitación con al menos la capacidad especificada
     */
    List<RoomType> findByCapacityGreaterThanEqual(Integer minCapacity);

    /**
     * Obtiene tipos de habitación por tipo de cama
     * 
     * @param bedType Tipo de cama
     * @return Lista de tipos de habitación con el tipo de cama especificado
     */
    List<RoomType> findByBedType(RoomType.BedType bedType);

    /**
     * Obtiene tipos de habitación por tipo de vista
     * 
     * @param viewType Tipo de vista
     * @return Lista de tipos de habitación con el tipo de vista especificado
     */
    List<RoomType> findByViewType(RoomType.ViewType viewType);

    /**
     * Obtiene tipos de habitación con precio menor o igual al especificado
     * 
     * @param maxPrice Precio máximo por noche
     * @return Lista de tipos de habitación dentro del rango de precio
     */
    List<RoomType> findByPricePerNightLessThanEqual(BigDecimal maxPrice);

    /**
     * Obtiene tipos de habitación en un rango de precios
     * 
     * @param minPrice Precio mínimo por noche
     * @param maxPrice Precio máximo por noche
     * @return Lista de tipos de habitación en el rango de precios
     */
    List<RoomType> findByPricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Verifica disponibilidad de habitaciones para fechas específicas
     * 
     * @param hotelId ID del hotel
     * @param checkIn Fecha de check-in
     * @param checkOut Fecha de check-out
     * @param requiredRooms Número de habitaciones requeridas
     * @return Lista de tipos de habitación disponibles
     */
    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.id = :hotelId " +
           "AND rt.isActive = true " +
           "AND rt.availableRooms >= :requiredRooms " +
           "AND rt.id NOT IN (" +
           "  SELECT DISTINCT b.roomType.id FROM Booking b " +
           "  WHERE b.hotel.id = :hotelId " +
           "  AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "  AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)" +
           ") ORDER BY rt.pricePerNight ASC")
    List<RoomType> findAvailableRoomTypes(@Param("hotelId") Long hotelId,
                                        @Param("checkIn") LocalDate checkIn,
                                        @Param("checkOut") LocalDate checkOut,
                                        @Param("requiredRooms") Integer requiredRooms);

    /**
     * Busca tipos de habitación por múltiples criterios
     * 
     * @param hotelId ID del hotel
     * @param minCapacity Capacidad mínima
     * @param maxPrice Precio máximo por noche
     * @param bedType Tipo de cama (puede ser null)
     * @param viewType Tipo de vista (puede ser null)
     * @return Lista de tipos de habitación que cumplen los criterios
     */
    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.id = :hotelId " +
           "AND rt.isActive = true " +
           "AND rt.capacity >= :minCapacity " +
           "AND rt.pricePerNight <= :maxPrice " +
           "AND (:bedType IS NULL OR rt.bedType = :bedType) " +
           "AND (:viewType IS NULL OR rt.viewType = :viewType) " +
           "ORDER BY rt.pricePerNight ASC")
    List<RoomType> findByMultipleCriteria(@Param("hotelId") Long hotelId,
                                        @Param("minCapacity") Integer minCapacity,
                                        @Param("maxPrice") BigDecimal maxPrice,
                                        @Param("bedType") RoomType.BedType bedType,
                                        @Param("viewType") RoomType.ViewType viewType);

    /**
     * Obtiene estadísticas de precios de habitaciones por hotel
     * 
     * @param hotelId ID del hotel
     * @return Array con [precio_minimo, precio_maximo, precio_promedio]
     */
    @Query("SELECT MIN(rt.pricePerNight), MAX(rt.pricePerNight), AVG(rt.pricePerNight) " +
           "FROM RoomType rt WHERE rt.hotel.id = :hotelId AND rt.isActive = true")
    Object[] getPriceStatisticsByHotel(@Param("hotelId") Long hotelId);

    /**
     * Cuenta habitaciones disponibles por hotel
     * 
     * @param hotelId ID del hotel
     * @return Número total de habitaciones disponibles
     */
    @Query("SELECT SUM(rt.availableRooms) FROM RoomType rt " +
           "WHERE rt.hotel.id = :hotelId AND rt.isActive = true")
    Integer countAvailableRoomsByHotel(@Param("hotelId") Long hotelId);

    /**
     * Obtiene el tipo de habitación más barato de un hotel
     * 
     * @param hotelId ID del hotel
     * @return Tipo de habitación con el precio más bajo
     */
    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.id = :hotelId " +
           "AND rt.isActive = true AND rt.availableRooms > 0 " +
           "ORDER BY rt.pricePerNight ASC")
    List<RoomType> findCheapestAvailableRoomType(@Param("hotelId") Long hotelId);

    /**
     * Obtiene tipos de habitación populares (con más reservas)
     * 
     * @param hotelId ID del hotel
     * @return Lista de tipos de habitación más reservados
     */
    @Query("SELECT rt FROM RoomType rt " +
           "LEFT JOIN Booking b ON rt.id = b.roomType.id " +
           "WHERE rt.hotel.id = :hotelId AND rt.isActive = true " +
           "GROUP BY rt.id " +
           "ORDER BY COUNT(b.id) DESC")
    List<RoomType> findMostPopularRoomTypes(@Param("hotelId") Long hotelId);    /**
     * Busca tipos de habitación por nombre
     * 
     * @param name Nombre del tipo de habitación
     * @return Lista de tipos de habitación que coinciden con el nombre
     */
    List<RoomType> findByNameContainingIgnoreCase(String name);

    /**
     * Obtiene tipos de habitación activos de un hotel específico.
     * 
     * @param hotelId ID del hotel
     * @return Lista de tipos de habitación activos del hotel
     */
    List<RoomType> findByHotelIdAndIsActiveTrue(Long hotelId);

    /**
     * Obtiene un tipo de habitación activo por ID.
     * 
     * @param id ID del tipo de habitación
     * @return Optional con el tipo de habitación si existe y está activo
     */
    Optional<RoomType> findByIdAndIsActiveTrue(Long id);

    /**
     * Obtiene un tipo de habitación activo por ID y hotel ID.
     * 
     * @param id ID del tipo de habitación
     * @param hotelId ID del hotel
     * @return Optional con el tipo de habitación si existe, está activo y pertenece al hotel
     */
    Optional<RoomType> findByIdAndHotelIdAndIsActiveTrue(Long id, Long hotelId);
}
