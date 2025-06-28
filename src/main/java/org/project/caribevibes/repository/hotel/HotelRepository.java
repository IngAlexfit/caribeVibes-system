package org.project.caribevibes.repository.hotel;

import org.project.caribevibes.entity.hotel.Hotel;
import org.project.caribevibes.entity.destination.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de hoteles en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Hotel, incluyendo filtros por destino, estrellas y disponibilidad.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Obtiene todos los hoteles de un destino específico
     * 
     * @param destination Destino del cual obtener los hoteles
     * @return Lista de hoteles del destino
     */
    List<Hotel> findByDestination(Destination destination);

    /**
     * Obtiene todos los hoteles de un destino por ID
     * 
     * @param destinationId ID del destino
     * @return Lista de hoteles del destino
     */
    List<Hotel> findByDestinationId(Long destinationId);

    /**
     * Obtiene hoteles activos de un destino específico
     * 
     * @param destinationId ID del destino
     * @return Lista de hoteles activos del destino
     */
    List<Hotel> findByDestinationIdAndIsActiveTrueOrderByNameAsc(Long destinationId);

    /**
     * Busca hoteles por nombre (búsqueda parcial, sin distinción de mayúsculas)
     * 
     * @param name Nombre o parte del nombre del hotel
     * @return Lista de hoteles que coinciden con el nombre
     */
    List<Hotel> findByNameContainingIgnoreCase(String name);

    /**
     * Obtiene hoteles por número de estrellas
     * 
     * @param stars Número de estrellas del hotel
     * @return Lista de hoteles con la clasificación especificada
     */
    List<Hotel> findByStars(Integer stars);

    /**
     * Obtiene hoteles con un número mínimo de estrellas
     * 
     * @param minStars Número mínimo de estrellas
     * @return Lista de hoteles con al menos el número de estrellas especificado
     */
    List<Hotel> findByStarsGreaterThanEqual(Integer minStars);

    /**
     * Obtiene todos los hoteles activos
     * 
     * @return Lista de hoteles activos
     */
    List<Hotel> findByIsActiveTrueOrderByNameAsc();

    /**
     * Obtiene hoteles con habitaciones disponibles
     * 
     * @param destinationId ID del destino
     * @return Lista de hoteles con disponibilidad
     */
    @Query("SELECT DISTINCT h FROM Hotel h " +
           "JOIN h.roomTypes rt " +
           "WHERE h.destination.id = :destinationId " +
           "AND h.isActive = true " +
           "AND rt.isActive = true " +
           "AND rt.availableRooms > 0 " +
           "ORDER BY h.stars DESC, h.name ASC")
    List<Hotel> findAvailableHotelsByDestination(@Param("destinationId") Long destinationId);

    /**
     * Cuenta el número de hoteles por destino
     * 
     * @param destinationId ID del destino
     * @return Número de hoteles en el destino
     */
    long countByDestinationIdAndIsActiveTrue(Long destinationId);

    /**
     * Obtiene hoteles ordenados por clasificación descendente
     * 
     * @param destinationId ID del destino
     * @return Lista de hoteles ordenados por estrellas
     */
    List<Hotel> findByDestinationIdAndIsActiveTrueOrderByStarsDescNameAsc(Long destinationId);

    /**
     * Busca hoteles por ciudad o dirección
     * 
     * @param location Ubicación a buscar en la dirección
     * @return Lista de hoteles en la ubicación especificada
     */
    List<Hotel> findByAddressContainingIgnoreCase(String location);

    /**
     * Obtiene estadísticas de estrellas de hoteles por destino
     * 
     * @param destinationId ID del destino
     * @return Array con [estrellas_minimas, estrellas_maximas, promedio_estrellas]
     */
    @Query("SELECT MIN(h.stars), MAX(h.stars), AVG(h.stars) " +
           "FROM Hotel h WHERE h.destination.id = :destinationId AND h.isActive = true")
    Object[] getStarsStatisticsByDestination(@Param("destinationId") Long destinationId);

    /**
     * Obtiene hoteles populares (con más reservas)
     * 
     * @param destinationId ID del destino
     * @param limit Número máximo de hoteles a retornar
     * @return Lista de hoteles más populares
     */
    @Query("SELECT h FROM Hotel h " +
           "LEFT JOIN Booking b ON h.id = b.hotel.id " +
           "WHERE h.destination.id = :destinationId AND h.isActive = true " +
           "GROUP BY h.id " +
           "ORDER BY COUNT(b.id) DESC")
    List<Hotel> findMostPopularHotels(@Param("destinationId") Long destinationId, 
                                    @Param("limit") int limit);

    /**
     * Verifica si un hotel tiene habitaciones disponibles
     * 
     * @param hotelId ID del hotel
     * @return true si el hotel tiene habitaciones disponibles
     */    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END " +
           "FROM RoomType rt WHERE rt.hotel.id = :hotelId " +
           "AND rt.isActive = true AND rt.availableRooms > 0")
    boolean hasAvailableRooms(@Param("hotelId") Long hotelId);

    /**
     * Obtiene todos los hoteles activos paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de hoteles activos
     */
    Page<Hotel> findAllByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Obtiene todos los hoteles activos con destino cargado mediante JOIN FETCH.
     * 
     * @param pageable Configuración de paginación
     * @return Página de hoteles activos con destino cargado
     */
    @Query("SELECT h FROM Hotel h LEFT JOIN FETCH h.destination d LEFT JOIN FETCH d.country LEFT JOIN FETCH h.roomTypes WHERE h.isActive = true ORDER BY h.name ASC")
    Page<Hotel> findAllActiveHotelsWithDestination(Pageable pageable);

    /**
     * Obtiene hoteles activos por destino paginados.
     * 
     * @param destinationId ID del destino
     * @param pageable Configuración de paginación
     * @return Página de hoteles del destino
     */
    Page<Hotel> findByDestinationIdAndIsActiveTrueOrderByNameAsc(Long destinationId, Pageable pageable);

    /**
     * Obtiene hoteles activos por rango de precio base paginados.
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @param pageable Configuración de paginación
     * @return Página de hoteles en el rango de precio
     */
    @Query("SELECT h FROM Hotel h WHERE h.basePrice >= :minPrice AND h.basePrice <= :maxPrice AND h.isActive = true ORDER BY h.basePrice ASC")
    Page<Hotel> findByBasePriceBetweenAndIsActiveTrueOrderByBasePriceAsc(@Param("minPrice") BigDecimal minPrice, 
                                                                        @Param("maxPrice") BigDecimal maxPrice, 
                                                                        Pageable pageable);

    /**
     * Obtiene hoteles activos por número de estrellas paginados.
     * 
     * @param stars Número de estrellas
     * @param pageable Configuración de paginación
     * @return Página de hoteles con las estrellas especificadas
     */
    Page<Hotel> findByStarsAndIsActiveTrueOrderByNameAsc(Integer stars, Pageable pageable);

    /**
     * Obtiene un hotel activo por ID.
     * 
     * @param id ID del hotel
     * @return Optional con el hotel si existe y está activo
     */
    Optional<Hotel> findByIdAndIsActiveTrue(Long id);

    /**
     * Obtiene hoteles mejor calificados paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de hoteles ordenados por calificación
     */    @Query("SELECT h FROM Hotel h WHERE h.isActive = true ORDER BY h.rating DESC, h.name ASC")
    Page<Hotel> findTopRatedHotels(Pageable pageable);

    /**
     * Busca hoteles por nombre (búsqueda parcial, sin distinción de mayúsculas) paginados.
     * 
     * @param name Nombre o parte del nombre del hotel
     * @param pageable Configuración de paginación
     * @return Página de hoteles que coinciden con el nombre
     */
    Page<Hotel> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByNameAsc(String name, Pageable pageable);
}
